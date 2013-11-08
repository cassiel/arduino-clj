(ns eu.cassiel.arduino.comms
  "Communication with Arduino, using out nybble-based protocol.
   Comms machinery cargo-culted from Clodiuno."
  (:import (java.io InputStream)
           (gnu.io SerialPort CommPortIdentifier
                   SerialPortEventListener SerialPortEvent
                   NoSuchPortException)))

(defprotocol PORT
  (xmit [this ch bytes] "Transmit with a character plus byte sequence as arguments.")
  (close [this] "Close the port."))

(defn port-identifier
  "Given a port name return its identifier."
  [port-name]
  (try
    (let [ports (CommPortIdentifier/getPortIdentifiers)]
      (loop [port (.nextElement ports)
             name (.getName port)]
        (if (= name port-name)
          port
          (recur (.nextElement ports) (.getName port)))))
    (catch Exception e (throw (NoSuchPortException.)))))

(defn open-port
  "Open serial interface."
  [identifier baudrate]
  (doto (.open identifier "clojure" 1)
    (.setSerialPortParams baudrate
                          SerialPort/DATABITS_8
                          SerialPort/STOPBITS_1
                          SerialPort/PARITY_NONE)))

(defn listener
  "Callback into `f`."
  [f]
  (proxy [SerialPortEventListener] []
    (serialEvent
      [event]
      (when (= (.getEventType event) SerialPortEvent/DATA_AVAILABLE)
        (f)))))

(def start-state {:first-nybble true
                  :current-byte 0
                  :command (char 0)
                  :data []})

(defn process-from-stream
  "Process input stream, returning new parsing state, optionally calling the
   callback with command char and args at the end of a parse."
  [state in f]
  ;; This should be a conditional, but we want tail recursion:
  (if (pos? (.available in))
    (let [b (.read in)]
      ;;(println "RAW" b)
      (cond (= b 0x80)               ; End message.
            (do ;;(println "DISPATCHING WITH" (:command state))
                (f (:command state)
                   (:data state))
                (recur start-state in f))

            (pos? (bit-and b 0x80))  ; Start message.
            (recur (assoc state
                     :command (char (bit-and b 0x7F))
                     :first-nybble true
                     :data [])
                   in f)

            (:first-nybble state)       ; Save this nybble.
            (recur (assoc state
                     :current-byte b
                     :first-nybble false)
                   in f)

            :else
            (recur (assoc state
                     :data (conj (:data state)
                                 (bit-or (bit-shift-left (:current-byte state) 4) b))
                     :first-nybble true)
                   in f)))
    state))

(defn open
  "Open a named port (`/dev/tty.XXXXX`). Takes also a map from characters to
   callback functions, each of which takes a sequence of bytes as argument."
  [port-name baudrate callback-map]

  (let [state (atom start-state)
        port (open-port (port-identifier port-name) baudrate)]
    (doto port
      (.addEventListener (listener #(swap! state
                                           process-from-stream
                                           (.getInputStream port)
                                           (fn [c args] (when-let [f (get callback-map c)]
                                                         (f args))))))
      (.notifyOnDataAvailable true))

    (reify PORT
      (xmit [this ch bytes]
        (let [os (.getOutputStream port)]
          (.write os (bit-or (int ch) 0x80))
          (doseq [b bytes] (do (.write os (bit-shift-right b 4))
                               (.write os (bit-and b 0xF))))
          (.write os 0x80)
          (.flush os)))
      (close [this] (.close port)))))
