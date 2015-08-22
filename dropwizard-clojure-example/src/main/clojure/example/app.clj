(ns example.app
  (:require [dropwizard-clojure.core
             :refer [defapplication defmain register-resource
                     register-healthcheck add-healthcheck remove-healthcheck]]
            [dropwizard-clojure.healthcheck :refer [healthcheck update-healthcheck]]
            [example.todo :refer [todo-resource todo-deserialiser]])
  (:import  [io.dropwizard.setup Environment] 
            [example.todo Todo]
            [com.fasterxml.jackson.databind.module SimpleModule]
    )
  (:gen-class))


(defn ds-mod []
  "created a simple module"
  (doto (SimpleModule. )
    (.addDeserializer (type (Todo. true "test")) (todo-deserialiser))
    )
  )

(def mock-hc (healthcheck (fn [] [true "I'm a mocked healthcheck"])))

(defapplication todo-app 
  (fn [settings ^Environment env]
    (let [resource (todo-resource) mod (ds-mod)]
      (-> env
          (register-resource resource)
          (register-healthcheck :mocked mock-hc)
          )
      (.registerModule (.getObjectMapper env) mod)
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