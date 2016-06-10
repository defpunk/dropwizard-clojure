(ns dropwizard-clojure.response)


(defn ok []
  (.build (javax.ws.rs.core.Response/ok)))


(defn notFound []
  (.build (.type (javax.ws.rs.core.Response/status 404) javax.ws.rs.core.MediaType/APPLICATION_JSON_TYPE)))
