(ns com.example.todo.core
  (:require [dropwizard-clojure.core
             :refer [defapplication defmain register-resource
                     register-healthcheck add-healthcheck remove-healthcheck]]
            [dropwizard-clojure.healthcheck :refer [healthcheck update-healthcheck]]
            [com.example.todo.resources.todo :refer [todo-resource]]
            [com.example.todo.health.todo-size :refer [todo-size]])
  (:import  [io.dropwizard.setup Environment]
            [com.codahale.metrics.health HealthCheck]
            [com.codahale.metrics.health HealthCheckRegistry]
    )
  (:gen-class))

(def mock-hc (healthcheck (fn [] [true "I'm a mocked healthcheck"])))

(defapplication todo-app 
  (fn [settings ^Environment env]
    (let [resource (todo-resource)]
      (-> env
          (register-resource resource)
          (register-healthcheck :mocked mock-hc)
          ))))

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