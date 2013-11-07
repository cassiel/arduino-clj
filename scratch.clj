(ns user
  (:require (eu.cassiel.arduino [comms :as c])))

(def p (c/open "/dev/tty.usbmodemfa141" 9600 nil))

p

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
