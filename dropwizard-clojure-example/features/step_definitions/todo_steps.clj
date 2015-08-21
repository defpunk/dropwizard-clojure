(use 'clojure.test)
(require '[clj-http.client :as client])
(require '[cheshire.core :refer :all])

(Given #"The todo list is reset" []
       (client/delete "http://localhost:8080/todo"))

(When #"A todo is created" []
      (client/post "http://localhost:8080/todo/1"
  {:body "{\"complete\": false, \"description\": \"description\"}"
   :content-type :json
   :accept :json}))

(Then #"^The todo list contains (\d+) entries$" [n]
	(let [y (count (parse-string (:body (client/get "http://localhost:8080/todo"))))]	
	  (assert (= (read-string n) y))))