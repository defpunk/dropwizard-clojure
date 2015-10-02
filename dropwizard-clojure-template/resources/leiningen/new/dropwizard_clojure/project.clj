(defproject {{name}} "0.0.1-SNAPSHOT"
  :description "{{name}} "
  :url "https://github.com/stevensurgnier/dropwizard-clojure"
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"] 
                 [dropwizard-clojure/dropwizard-clojure "0.1.2-SNAPSHOT"]
                 [org.clojars.punkisdead/lein-cucumber "1.0.4"]
                 [clj-http "2.0.0"]
                 [cheshire "5.5.0"]]
  :main {{name}}.app
  :profiles {:uberjar {:aot :all}}
  :plugins [[org.clojars.punkisdead/lein-cucumber "1.0.4"]]
  :global-vars {*warn-on-reflection* true}
  )