(ns example.app
  (:require [dropwizard-clojure.core
             :refer [defapplication defmain register-resource register-jackson-module
                     register-healthcheck add-healthcheck remove-healthcheck]]
            [dropwizard-clojure.healthcheck :refer [healthcheck update-healthcheck]]
            [example.todo :refer [todo-resource todo-module]])
  (:import  [io.dropwizard.setup Environment])
  (:gen-class))

(def mock-hc (healthcheck (fn [] [true "I'm a mocked healthcheck"])))

(defapplication todo-app 
  (fn [settings ^Environment env]
    (let [resource (todo-resource) mod (todo-module)]
      (-> env
          (register-resource resource)
          (register-healthcheck :mocked mock-hc)
          (register-jackson-module mod)
          )
      )))

(defmain todo-app)

(defn dev-run [] 
  "utility function to fire up application in dev mode"
  (.run todo-app (into-array ["server" "resources/dev-todo.yml"])))

(defn dev-reload [col]
  "utility function to reload application with the resources passed in the collection registered"
  (.reload todo-app col))

(defn dev-app-stop [] 
  "utility function to stop the application"
  (.stop todo-app))