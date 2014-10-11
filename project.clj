(defproject eu.cassiel/arduino "1.0.0"
  :description "Generic Arduino protocol and comms for Clojure."
  :url "https://github.com/cassiel/arduino-clj"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [rxtx22 "1.0.6"]]
  ;; I guess we need the system-wide RXTX for the Pi:
  :jvm-opts ["-Djava.library.path=/usr/lib/jni"]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles
  {:dev {:dependencies [[midje "1.5.1"]]
         :plugins [[lein-midje "3.1.1"]]}})
