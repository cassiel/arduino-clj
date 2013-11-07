(defproject net.monomatic/glock "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [rxtx22 "1.0.6"]]
  :profiles
  {:dev {:dependencies [[midje "1.5.1"]]
         :plugins [[lein-midje "3.1.1"]]}})
