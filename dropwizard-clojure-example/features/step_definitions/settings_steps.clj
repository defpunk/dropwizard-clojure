(use 'clojure.test)
(require '[clj-http.client :as client])
(require '[cheshire.core :refer :all])

(Then #"^The max-size setting is (\d+)$" [n]
	(let [y (:max-size (parse-string (:body (client/get "http://localhost:8080/settings")) true))]	
	  (assert (= (read-string n) y))))