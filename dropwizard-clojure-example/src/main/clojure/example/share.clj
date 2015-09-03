(ns example.share
  (:require [clojure.tools.logging :as log]
  			[clj-http.client :as client]
  			[cheshire.core :as cc])
  (:refer-clojure)
  (:import [com.codahale.metrics.annotation Timed]
           [javax.validation Valid]
           [javax.ws.rs GET POST Path Consumes Produces PathParam]))

;This will come from settings in the end

(def tickers [["GSK.L" "100"] ["BATS.L" "100"] ["RB.L" "100"] ["BT.L" "100"]])

(defn- clean-result [s]
	"Cleasn up the result string removing escaped characters and adding the real replacements back"
  (let [updates [["\\x2F" "/"]["\\x26nbsp\\x3B" " "]["\\x27" "'"]["\\x26" "&"]]]
	(reduce #(clojure.string/replace %1 (first %2) (last %2)) s updates))
  )

(defn get-share-data [x]
	(let [url (str "http://www.google.com/finance?&q=" (first x) "&output=json")
		  content (clean-result (subs (:body (client/get url)) 3))
		  json (first (cc/parse-string content true))
		]
		(select-keys json [:name :dy :l :lo52 :hi52]) 
	))

(defn post-data [x]
  (client/post "http://localhost:8080/shares"
  {:body (cc/generate-string x)
   :content-type :json
   :accept :json}))

(defn reset-tickers [x]
	(let [data (map get-share-data x)]
		(doseq [s data] (post-data s))
	))

(defprotocol AddShare 
  (save [this ^Long id]))

(deftype ^{Path "/shares"
           Consumes ["application/json"]
           Produces ["application/json"]}
    ShareResource []
    AddShare
    (^{POST true Timed true} save [this id] (println "mocked post")))

(defn build-shares-resource [m]
  (ShareResource. ))