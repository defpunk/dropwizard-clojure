(ns example.todo
  (:require [clojure.tools.logging :as log])
  (:import [com.codahale.metrics.annotation Timed]
           [javax.validation Valid]
           [javax.ws.rs GET POST DELETE Path Consumes Produces PathParam]
           [com.fasterxml.jackson.databind JsonDeserializer]
           ))

(defrecord Todo [^Boolean complete ^String description])

(definterface ITodo
  (get [])
  (delete [])
  (add [^Long id ^example.todo.Todo item])
  (get [^Long id])
  (toggle [^Long id])
  (delete [^Long id]))


(defn todo-deserialiser []
  (proxy [JsonDeserializer] []
    (^example.todo.Todo deserialize [^com.fasterxml.jackson.core.JsonParser jp 
                                      ^com.fasterxml.jackson.databind.DeserializationContext ctxt]
      (let [n (.readTree (.getCodec jp) jp)]
        (Todo. (.get n "complete") (.get n "description"))
      )                                
    ) 
  )) 

(deftype ^{Path "/todo"
           Consumes ["application/json"]
           Produces ["application/json"]}
    TodoResource [state]
    ITodo
    (^{GET true Timed true} get [this] @state)

    (^{DELETE true Timed true} delete [this]
     (reset! state {})
     {})

    (^{Path "{id}" POST true Timed true}
     add [this ^{PathParam "id"} id ^{Valid true} item]
     (println item)
     (swap! state assoc id item))
    
    (^{Path "{id}" GET true Timed true}
     get [this ^{PathParam "id"} id]
     (get @state id))
    
    (^{Path "{id}/toggle" POST true Timed true}
     toggle [this ^{PathParam "id"} id]
     (swap! state update-in [id]
       #(Todo. (not (.getComplete %)) (.getDescription %))))

    (^{Path "{id}" DELETE true Timed true}
     delete [this ^{PathParam "id"} id]
     (swap! state dissoc id)))

(defn todo-resource []
  (TodoResource. (atom {})))

(defn todo-size [max-size ^TodoResource resource]
  (if (<= (count (.get resource)) max-size)
    [true (str max-size)]
    [false "too many todos"]))