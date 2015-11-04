(ns {{name}}.app
  (:require [dropwizard-clojure.core
             :refer [application defmain register-resource register-jackson-module
                     register-healthcheck add-healthcheck remove-healthcheck ApplicationSetup add-task]]
            [{{name}}.foo :as foo])
  (:gen-class))


(defn- default-initilize[] 
  "default initialization the dropwizard application - does nothing."
  (println "default initialize running"))

(defn- configure-app [settings env]
  "Configures the dropwizard environment, typically registers resources, healthchecks and tasks."
  (let [fr (foo/build-foo-resource settings)]
      (-> env
          (register-resource fr)
          (register-healthcheck :foobar foo/test-healthcheck)
          (add-task (foo/bar-task settings)))))

(defn- get-application-setup[]
  (reify ApplicationSetup
    (initialize [this bootstrap] (default-initilize))
    (configure [this settings env] (configure-app settings env))))

(defn- new-application [] (def {{name}}-app (application (get-application-setup))))

;create an initial version of the application for def main and dev-reload functions to use
(new-application)

(defmain {{name}}-app)

(defn- dev-run [] 
  "utility function to fire up application in dev mode"
  (.run {{name}}-app (into-array ["server" "resources/config.yml"])))

(defn- rebuild []
  "Utility method to stop a running application and restart with the latest setup"
  (.stop {{name}}-app)
  (new-application)
  (dev-run))

(defn- dev-reload [col]
  "utility function to reload application with the resources passed in the collection registered"
  (.reload {{name}}-app col))

(defn- dev-app-stop [] 
  "utility function to stop the application"
  (.stop {{name}}-app))