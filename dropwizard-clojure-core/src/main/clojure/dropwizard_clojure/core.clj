(ns dropwizard-clojure.core
  (:require [dropwizard-clojure.healthcheck :refer [healthcheck]])
  (:import [com.qwickr.dropwizard ClojureDropwizardApplication]
           [io.dropwizard Application]
           [io.dropwizard.setup Environment]
           [io.dropwizard.jersey.setup JerseyEnvironment]
           [com.codahale.metrics.health HealthCheckRegistry]))

(defn- application
  ([app-name run-fn]
   (application app-name '(constantly nil) run-fn))
  ([app-name init-fn run-fn]
   `(def ~app-name
      (proxy [ClojureDropwizardApplication] []
        (initialize [bootstrap#] (~init-fn bootstrap#))
        (runWithSettings [settings# environment#]
          (~run-fn settings# environment#))))))

(defmacro defapplication
  [& args]
  (apply application args))

(defmacro defmain [app]
  `(def ~'-main
     (fn [& args#] (.run ^Application ~app (into-array String args#)))))

(defn ^Environment env-from-app [app]
  (.getEnvironment app))

(defn register-resources
  [^Environment env resources]
  (let [^JerseyEnvironment jersey (.jersey env)]
    (dorun (map #(.register jersey %) resources))
    env))

(defn register-resource [app resource]
  (register-resources (env-from-app app) [resource]))

(defn register-resource [^Environment env resource]
  (register-resources env [resource]))

(defn add-healthcheck-to-registry [^HealthCheckRegistry registry hc-name hc]
  (.register registry (name hc-name) hc))

(defn register-healthcheck-functions [^Environment env healthchecks]
  (let [^HealthCheckRegistry hc (.healthChecks env)]
    (dorun (map (fn [[hc-name hc-fn]]
                  (add-healthcheck-to-registry hc (name hc-name) (healthcheck hc-fn)))
                healthchecks))
    env))

(defn register-healthcheck-function [^Environment env hc-name hc-fn]
  (register-healthcheck-functions env {hc-name hc-fn}))

(defn register-healthcheck [^Environment env hc-name hc]
  (add-healthcheck-to-registry (.healthChecks env) hc-name hc) env)

;;only works on a running application
(defn add-healthcheck [app hc-name hc-fn]
  (let [^HealthCheckRegistry hc (.healthChecks (env-from-app app))]
    (.register hc (name hc-name) hc-fn)))
;;only works on a running application
(defn remove-healthcheck [app hc-name]
  (let [^HealthCheckRegistry hc (.healthChecks (env-from-app app))]
    (.unregister hc (name hc-name))))