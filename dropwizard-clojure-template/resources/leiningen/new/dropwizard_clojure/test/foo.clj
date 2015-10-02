(ns {{name}}.test.foo
  (:require [clojure.test :refer :all]
  			[eg.foo]
            )
  )

;redef private functions
(def sample #'{{name}}.foo/sample-function)

(deftest sample-adds-four
	"tests the foo/sample-function function via a redef"
  (is (= 5 (sample 1)))
  (is (= 2 (sample -3)))
  )