(ns user
  (:require (eu.cassiel.arduino [comms :as c])))

(def p (c/open "/dev/ttyACM0" 9600 {\? println
                                    \+ (fn [[h l]] (println "TOTAL" (+ (bit-shift-left h 8) l)))}))


(c/xmit p \L [1])
(c/xmit p \L [0])

(c/xmit p \X [0])

(doseq [n (range 64 74)]
  (c/xmit p \+ [n n n 1]))

p

(c/xmit p \D [9 1])
(c/xmit p \D [5 0])
(c/xmit p \D [6 255])
(c/xmit p \D [7 0])
(c/xmit p \D [8 255])

(c/xmit p \D [5 0])

(c/xmit p \D [1 0])
(c/xmit p \D [2 0])
(c/xmit p \D [3 0])


(c/close p)

(char (bit-and 194 0x7F))

(java.io.ByteArrayInputStream. (byte-array (map byte [1 2 3])))

(byte-array (map byte [1 2 3]))

(byte -128)

(defn byteify [n]
  (let [b8 (bit-and n 0xFF)]
    (byte
     (if (> b8 0x7F)
       (- b8 0x100)
       b8))))

(defn input-stream [ns] (java.io.ByteArrayInputStream. (byte-array (map byteify ns))))

(def is (input-stream [1 2 0]))

(.read is)

(pos? (bit-and 193 0x80))


(conj [1 2 3] 4)

(char (bit-and 171 0x7F))

(when-let [f (get {\+ identity} \+)]
  (f 35))
