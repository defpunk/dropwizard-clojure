(ns leiningen.new.dropwizard-clojure
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "dropwizard-clojure"))

(defn dropwizard-clojure
  "Creates a new dropwizard clojure app project"
  [name]
  (let [data {:name name
              :sanitized (name-to-path name)}]
    (main/info "Generating fresh 'lein new' dropwizard-clojure project.")
    (->files data
    		 ["resources/config.yml" (render "resources/config.yml" data)]
    		 ["README.md" (render "README.md" data)]
         ["project.clj" (render "project.clj" data)]
         ["features/foo.feature" (render "features/foo.feature" data)]
         ["features/step_definitions/foo_steps.clj" (render "features/step_definitions/foo_steps.clj" data)]
         ["test/{{sanitized}}/test/foo.clj" (render "test/foo.clj" data)]
    		 ["src/{{sanitized}}/app.clj" (render "src/app.clj" data)]
         ["src/{{sanitized}}/foo.clj" (render "src/foo.clj" data)])))
