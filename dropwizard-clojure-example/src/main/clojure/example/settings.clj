(ns example.settings
  (:require [clojure.tools.logging :as log])
  (:import [com.codahale.metrics.annotation Timed]
           [javax.validation Valid]
           [javax.ws.rs GET Path Consumes Produces PathParam]))


(defprotocol FindMaxValue 
  (getById [this id]))

(deftype ^{Path "/settings"
           Consumes ["application/json"]
           Produces ["application/json"]}
    MaxSizeResource [maxSize]
    FindMaxValue
    (^{GET true Timed true} getById [this id] {"max-size" maxSize}))

(defn build-settings-resource [m]
  (MaxSizeResource. (get m "max-size" m)))