(ns com.example.todo.resources.todo
  (:require [clojure.tools.logging :as log])
  (:import [com.example.todo.representations Todo]
           [com.codahale.metrics.annotation Timed]
           [javax.validation Valid]
           [javax.ws.rs GET POST DELETE Path Consumes Produces PathParam]))

(definterface ITodo
  (get [])
  (delete [])
  (add [^Long id ^com.example.todo.representations.Todo item])
  (get [^Long id])
  (toggle [^Long id])
  (delete [^Long id]))

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