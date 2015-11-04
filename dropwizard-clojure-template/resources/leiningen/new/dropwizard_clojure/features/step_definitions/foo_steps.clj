(use 'clojure.test)
(require '[clj-http.client :as client])
(require '[cheshire.core :refer :all])

(def result)

(When #"the foo method is invoked" []
	(def result (parse-string (:body (client/get "http://localhost:8080/foo")) true)))

(Then #"^the test value is (.+)" [x]
	(let [y (:test result)]
	  (assert (= x y))))