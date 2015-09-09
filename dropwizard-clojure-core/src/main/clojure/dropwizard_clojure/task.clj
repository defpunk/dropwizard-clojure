(ns dropwizard-clojure.task
  (:require [clojure.walk])
  (:import [io.dropwizard.servlets.tasks Task]
  		   [com.google.common.collect ImmutableMultimap]
  		   [java.io PrintWriter]))

(defn- transform [imm]
  "takes a multimap and creates a map"
  (if (.isEmpty imm) {} 
    (zipmap (map keyword (keys (.asMap imm))) (vals (.asMap imm)))))

(defn- transform-multimap [imm]
  (println (str "incoming map is " imm))
  (if imm (transform imm) {}))

(defn- values-to-writer [^PrintWriter pw x]
	(println "entered values to writer")
  (println (str "param is " x))
	(cond
		(or (seq? x) (vector? x) (list? x)) (doseq [y x] (.println pw y))
		:else (if x (.println pw x))))

(defn task [^String n f] "creates a task based on the supplied function"
  (proxy [Task] [n]
    (execute [^ImmutableMultimap p ^PrintWriter pw] 
    	(println "executing task")
    	(values-to-writer pw (f (transform-multimap p)))

    	)))

(defn update-task [t f] "updates the function used when execising the supplied task"
  (update-proxy t {"execute" (fn [this ^ImmutableMultimap p ^PrintWriter pw] (values-to-writer pw (f (transform-multimap p))))}))