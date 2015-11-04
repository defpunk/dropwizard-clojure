(ns {{name}}.foo
  (:require [dropwizard-clojure.healthcheck :refer [healthcheck update-healthcheck]]
  			[dropwizard-clojure.task :as task])
  (:import [com.codahale.metrics.annotation Timed]
           [javax.validation Valid]
           [javax.ws.rs GET Path Consumes Produces PathParam]))


(defprotocol Foo 
  (bar[this ^Long id]))

(deftype ^{Path "/foo"
           Consumes ["application/json"]
           Produces ["application/json"]}
    FooResource []
    Foo
    (^{GET true Timed true} bar [this id] {"test" "bar"}))

(defn build-foo-resource [m]
  "Builds a FooResource - NB setttings are ignored."
  (FooResource.))

(def test-healthcheck (healthcheck (fn [] [true "foos are fantastic"])))


(defn bar-task [s]
  "creates a dummy task returns the name value from settings"
  (task/task "name" (fn [_] ((get s "name")))))

(defn- sample-function [x]
  "Sample function mainly to show how tests work"
  (+ 4 x))