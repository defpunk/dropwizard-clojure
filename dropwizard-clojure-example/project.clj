(defproject dropwizard-clojure/dropwizard-clojure-example "0.1.0-SNAPSHOT"
  :description "Dropwizard for Clojure"
  :url "https://github.com/stevensurgnier/dropwizard-clojure"
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]                 
                 [io.dropwizard/dropwizard-core "0.7.1"]
                 [dropwizard-clojure/dropwizard-clojure "0.1.0-SNAPSHOT"]]
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :main com.example.todo.application
  :profiles {:uberjar {:aot :all}}
  :global-vars {*warn-on-reflection* true})
