(ns dropwizard-clojure.response
  (:import (javax.ws.rs.core Response)))


(defn ok []
  (.build (Response/ok)))

(defn server-error []
  (.build (Response/serverError)))

(defn not-found []
  (.build (.type (Response/status 404) javax.ws.rs.core.MediaType/APPLICATION_JSON_TYPE)))

(defn accepted []
  (.build (Response/accepted)))