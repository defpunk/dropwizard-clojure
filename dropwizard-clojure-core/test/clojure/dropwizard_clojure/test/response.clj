(ns dropwizard-clojure.test.response
  (:require [clojure.test :refer :all]
            [dropwizard-clojure.response :as response]))

(deftest test-ok-response
  (testing "ok with no params"
  	(is (== (.build (javax.ws.rs.core.Response/ok)) (response/ok))
    )))
