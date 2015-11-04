(ns {{name}}.test.foo
  (:require [clojure.test :refer :all]
  			[{{name}}.foo]
            )
  )

;redef private functions
(def sample #'{{name}}.foo/sample-function)

;This test is designed to fail to spotlight the teting approach
(deftest sample-adds-four
	"tests the foo/sample-function function via a redef"
  (is (= 5 (sample 1)))
  (is (= 2 (sample -3)))
  )