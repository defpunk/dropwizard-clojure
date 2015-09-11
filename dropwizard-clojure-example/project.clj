(defproject dropwizard-clojure/dropwizard-clojure-example "0.1.1"
  :description "Dropwizard for Clojure"
  :url "https://github.com/stevensurgnier/dropwizard-clojure"
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]                 
                 [io.dropwizard/dropwizard-core "0.8.2"]
                 [dropwizard-clojure/dropwizard-clojure "0.1.2-SNAPSHOT"]
                 [org.clojars.punkisdead/lein-cucumber "1.0.4"]
                 [clj-http "2.0.0"]
                 [cheshire "5.5.0"]]
  :source-paths ["src/clojure"]
  :main example.app
  :profiles {:uberjar {:aot :all}}
  :plugins [[org.clojars.punkisdead/lein-cucumber "1.0.4"]]
  :global-vars {*warn-on-reflection* true}
  )
