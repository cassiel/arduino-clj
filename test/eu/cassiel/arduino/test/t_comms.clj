(ns eu.cassiel.arduino.test.t-comms
  (:require (eu.cassiel.arduino [comms :as c]))
  (:use midje.sweet))

(fact "foo"
      "X" => "X")

(defn byteify [n]
  (let [b8 (bit-and n 0xFF)]
    (byte (if (> b8 0x7F)
            (- b8 0x100)
            b8))))

(defn input-stream [ns] (java.io.ByteArrayInputStream. (byte-array (map byteify ns))))

(fact "parse no args"
      (let [a (atom nil)]
        (do
          (c/process-from-stream c/start-state
                                 (input-stream [(bit-or (int \A) 0x80)
                                                0x80])
                                 (fn [ch data] (reset! a {:cmd ch :data data})))
          @a) => {:cmd \A :data []}))

(fact "parse with args"
      (let [a (atom nil)]
        (do
          (c/process-from-stream c/start-state
                                 (input-stream [(bit-or (int \X) 0x80)
                                                5 10
                                                6 3
                                                11 14
                                                0x80])
                                 (fn [ch data] (reset! a {:cmd ch :data data})))
          @a) => {:cmd \X :data [90 99 190]}))
