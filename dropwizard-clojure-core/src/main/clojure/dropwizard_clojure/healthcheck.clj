(ns dropwizard-clojure.healthcheck
  (:import [com.codahale.metrics.health HealthCheck]
           [com.codahale.metrics.health HealthCheck$Result]))

(def ^{:private true} default-unhealthy-message
  "unhealthy")

(defn- marshall-clj-result [x]
  (cond
    (or (seq? x) (vector? x) (list? x)) (take 2 x)
    :else (if x [true] [false])))

(defn- to-result [[healthy? arg]]
  (if healthy?
    (HealthCheck$Result/healthy arg)
    (HealthCheck$Result/unhealthy (if (nil? arg)
                                    default-unhealthy-message
                                    arg))))

(def ^{:private true} result 
  (comp to-result marshall-clj-result))

(defn healthcheck [f] "creates a healthcheck based on the supplied function"
  (proxy [HealthCheck] []
    (check [] (result (f)))))

(defn update-healthcheck [h f] "updates the unction used when execising the supplied healthcheck"
  (update-proxy h {"check" (fn [this] (result (f)))}))