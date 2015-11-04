(ns dropwizard-clojure.test.task
  (:require [clojure.test :refer :all]
            [dropwizard-clojure.task])
  (:import [com.google.common.collect ImmutableMultimap]
  			[java.io PrintWriter StringWriter]
  	))

;redef private functions
(def tmm #'dropwizard-clojure.task/transform-multimap)
(def vtw #'dropwizard-clojure.task/values-to-writer)

(defn- build-multimap-with-values []
	(let [mmb (ImmutableMultimap/builder)]
		(.put mmb "cat" "hat")
		(.put mmb "cat" "sat")
		(.put mmb "cat" "mat")
		(.put mmb "dog" "log")
		(.put mmb "dog" "hog")
		(.put mmb "dog" "fog")
		(.build mmb)))

(defn- build-multimap-with-no-values []
	(let [mmb (ImmutableMultimap/builder)]
		(.build mmb)))

(defn- writerOutputAsString [x]
	(let [sw (StringWriter.) pw (PrintWriter. sw) ]
		(vtw pw x)
		(.toString sw))
	)

(deftest buildmap
	"tests the transfomr-multimap function via a redef"
  (is (= {:cat ["hat" "sat" "mat"] :dog ["log" "hog" "fog"]} (tmm (build-multimap-with-values))))
  (is (= {} (tmm (build-multimap-with-no-values))))
  (is (= {} (tmm nil)))
  )

(deftest outputStrings
	"tests the values to writer function via the writerAsString function"
	(is (= "" (writerOutputAsString nil)))
	(is (= "one\n" (writerOutputAsString "one")))
)