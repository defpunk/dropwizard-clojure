(ns com.example.todo.test.core
  (:use [clojure.test]))

(deftest run-cukes
  (. cucumber.cli.Main (main (into-array ["--format" "pretty" "--glue" "features/step_definitions" "features"]))))