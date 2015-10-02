(ns {{name}}.app
  (:require [dropwizard-clojure.core
             :refer [application defmain register-resource register-jackson-module
                     register-healthcheck add-healthcheck remove-healthcheck ApplicationSetup add-task]]
            [{{name}}.foo :as foo])
  (:gen-class))

(deftype Setup []
  ApplicationSetup
  (initialize [this bootstrap] (println "initialize running"))
  (configure [this settings env]
    (let [fr (foo/build-foo-resource settings)]
      (-> env
          (register-resource fr)
          (register-healthcheck :foobar foo/test-healthcheck)
          (add-task (foo/bar-task settings))))
      ))
  

(def {{name}}-app (application (Setup.)))

(defmain {{name}}-app)

(defn dev-run [] 
  "utility function to fire up application in dev mode"
  (.run {{name}}-app (into-array ["server" "resources/config.yml"])))

(defn rebuild []
  (.stop {{name}}-app)
  (def {{name}}-app (application (Setup.)))
  (.run {{name}}-app (into-array ["server" "resources/config.yml"]))
  )

(defn dev-reload [col]
  "utility function to reload application with the resources passed in the collection registered"
  (.reload {{name}}-app col))

(defn dev-app-stop [] 
  "utility function to stop the application"
  (.stop {{name}}-app))