(ns example.app
  (:require [dropwizard-clojure.core
             :refer [application defmain register-resource register-jackson-module
                     register-healthcheck add-healthcheck remove-healthcheck ApplicationSetup add-task]]
            [dropwizard-clojure.healthcheck :refer [healthcheck update-healthcheck]]
            [example.todo :refer [todo-resource todo-module]]
            [example.share :refer [build-shares-resource populate-shares-task]]
            [example.settings :refer [build-settings-resource]])
  (:import  [io.dropwizard.setup Environment])
  (:gen-class))

(def mock-hc (healthcheck (fn [] [true "I'm a mocked healthcheck"])))

(deftype TodoSetup []
  ApplicationSetup
  (initialize [this bootstrap] (println "initialize running"))
  (configure [this settings env]
    (let [resource (todo-resource) mod (todo-module) s-resource (build-settings-resource settings)]
      (-> env
          (register-resource resource)
          (register-resource s-resource)
          (register-resource (build-shares-resource settings))
          (register-healthcheck :mocked mock-hc)
          (register-jackson-module mod)
          (add-task (populate-shares-task settings)))
          
          )
      ))
  

(def todo-app (application (TodoSetup.)))

(defmain todo-app)

(defn dev-run [] 
  "utility function to fire up application in dev mode"
  (.run todo-app (into-array ["server" "resources/dev-todo.yml"])))

(defn rebuild []
  (.stop todo-app)
  (def todo-app (application (TodoSetup.)))
  (.run todo-app (into-array ["server" "resources/dev-todo.yml"]))
  )

(defn dev-reload [col]
  "utility function to reload application with the resources passed in the collection registered"
  (.reload todo-app col))

(defn dev-app-stop [] 
  "utility function to stop the application"
  (.stop todo-app))