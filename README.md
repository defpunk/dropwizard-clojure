# dropwizard-clojure

The aim of this project is to provide the infrastructure for creating clojure applications on top of the dropwizard framework.  To find out more about the framework check out [dropwizard.io](http://dropwizard.io/).

The dropwizard-clojure project has two subprojects.

1. dropwizard-clojure-core - A wrapper over the standard dropwizard designed to make the implementation of clojure dropwizard apps easier.
2. dropwizard-clojure-example - A dropwiazrd project implemented in clojure to showcase the features of the dropwizard-clojure-core  module. 

## Table of Contents

1. [Leiningen](#leiningen)
2. [Configuration](#configuration)
3. [Creating an Application](#creating-an-application)
4. [Creating a Representation class](#creating-a-representation-class)
5. [Creating a Resource class](#creating-a-resource-class)
6. [Creating a HealthCheck](#creating-a-healthcheck)
7. [Building fat JARs](#building-fat-jars)
8. [Running your Application](#running-your-application)

## Leiningen

NB. This fork of dropwizard-clojure has not been uploaded to clojars so you will need to clone the repo and install it locally.

```clojure
[dropwizard-clojure/dropwizard-clojure "0.1.2-SNAPSHOT"]
```

## Configuration

Typically each Java dropwizard application will have its own unique extention of the io.dropwizard.Configuration class.  An example of such a class is shown below [TodoConfiguration.java](#TodoConfiguration.java).  Dropwizard will serialize a yml file into your configuration class before starting the application.  The yaml file contains a mixture of the application specific settings and general dropwizard settings that are used to configure logging, ports, connections etc.

However to mimimize the java required in dropwizard-clojure applications the libary will read values from a settings node from the yaml file provided see [dev-todo.yml](#dev-todo.yml) below.  Rather than creating a custom application and overriding the initialize and configure methods dropwizard-clojure applications will be configured by injecting an implementation of the ApplicationSetup protocol.  

ApplicationSetup Protocol
```clojure
(defprotocol ApplicationSetup
  (initialize [this bootstrap])
  (configure [this settings ^Environment env]))
```

[dev-todo.yml](dropwizard-clojure-example/resources/dev-todo.yml)

```yml
settings:
  maxSize: 4
```

[TodoConfiguration.java](dropwizard-clojure-example/src/main/java/com/example/todo/TodoConfiguration.java)

```java
package com.example.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import javax.validation.constraints.NotNull;

public class TodoConfiguration extends Configuration {
    @NotNull
    private int maxSize;

    @JsonProperty
    public int getMaxSize() {
        return maxSize;
    }

    @JsonProperty
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
```


## Creating an Application

To create an application call the application function with an implementation of the protocol.

```clojure
(def todo-app (application (MyAppSetup.)))
```

The `defmain` macro is provided to conveniently generate a `main` method that will start your application correctly.

The example below shows an example application.  Most of the interesting work is contained in the creation of the TodoSetup type which implements the ApplicationSetup protocol.  The configure method registers the resource and jackson modules created via functions imported from the example.todo namespace and the healthcheck created using the mock-hc function.    

[core.clj](dropwizard-clojure-example/src/main/clojure/com/example/todo/core.clj)

```clojure
(ns example.app
  (:require [dropwizard-clojure.core
             :refer [application defmain register-resource register-jackson-module
                     register-healthcheck add-healthcheck remove-healthcheck ApplicationSetup]]
            [dropwizard-clojure.healthcheck :refer [healthcheck update-healthcheck]]
            [example.todo :refer [todo-resource todo-module]]
            [example.settings :refer [build-settings-resource]])
  (:import  [io.dropwizard.setup Environment])
  (:gen-class))

(def mock-hc (healthcheck (fn [] [true "I'm a mocked healthcheck"])))

(deftype TodoSetup []
  ApplicationSetup
  (initialize [this bootstrap] (println "initialize running"))
  (configure [this settings env]
    (let [resource (todo-resource) mod (todo-module)]
      (-> env
          (register-resource resource)
          (register-healthcheck :mocked mock-hc)
          (register-jackson-module mod)
          )
      )))
  

(def todo-app (application (TodoSetup.)))

(defmain todo-app)

```

## Creating a Representation

Clojure records can be used in place of Java classes as the representations.

```clojure
(defrecord Todo [^Boolean complete ^String description])
```

There is a small problem with this approach, Clojure records are immutable however the Jackson libary that is 
used to deserialize the input typically mutates objects that can be created from a base class.  To get round this problem a JsonDeserializer can be created as shown below.  This approach works in the example application but could run into problems with larger object graphs.  Future iterations will explore alternatives. 

```clojure
(defn- todo-deserialiser []
  (proxy [JsonDeserializer] []
    (^example.todo.Todo deserialize [^com.fasterxml.jackson.core.JsonParser jp 
                                      ^com.fasterxml.jackson.databind.DeserializationContext ctxt]
      (let [n (.readTree (.getCodec jp) jp)]
        (Todo. (.get n "complete") (.get n "description"))
      )                                
    ) 
  )) 
```

## Creating a Resource

The path of least resistance is to define an interface and create a type that implements it.  The interface is used to bridge the gap between the Clojure and Java worlds.  Without an interface the jersey underpinnings will see input types and output types as java Objects, this means that extra work will be needed in the marshalling of types.  The convenience of avoiding the marshalling issues worth the limitations of preferring interfaces over protocols here.

[core.clj](dropwizard-clojure-example/src/main/clojure/com/example/todo/resources/todo.clj)

```clojure
(ns com.example.todo.resources.todo
  (:require [clojure.tools.logging :as log])
  (:import [com.example.todo.representations Todo]
           [com.codahale.metrics.annotation Timed]
           [javax.validation Valid]
           [javax.ws.rs GET POST DELETE Path Consumes Produces PathParam]))

(definterface ITodo
  (get [])
  (delete [])
  (add [^Long id ^com.example.todo.representations.Todo todo])
  (get [^Long id])
  (toggle [^Long id])
  (delete [^Long id]))

(deftype ^{Path "/todo"
           Consumes ["application/json"]
           Produces ["application/json"]}
    TodoResource [state]
    ITodo
    (^{GET true Timed true} get [this] @state)

    (^{DELETE true Timed true} delete [this]
     (reset! state (atom {}))
     {})
    
    (^{Path "{id}" POST true Timed true}
     add [this ^{PathParam "id"} id ^{Valid true} todo]
     (swap! state assoc id todo))
    
    (^{Path "{id}" GET true Timed true}
     get [this ^{PathParam "id"} id]
     (get @state id))
    
    (^{Path "{id}/toggle" POST true Timed true}
     toggle [this ^{PathParam "id"} id]
     (swap! state update-in [id]
       #(Todo. (not (.getComplete %)) (.getDescription %))))

    (^{Path "{id}" DELETE true Timed true}
     delete [this ^{PathParam "id"} id]
     (swap! state dissoc id)))

(defn todo-resource []
  (TodoResource. (atom {})))
```

### Registering a Resource

As demonstrated in the configuration section above registering a resource is as simple as calling the register-resource function.  The `register-resource` function accepts a resource and returns the environment to allow threading. 

```clojure
 (-> env
          (register-resource resource)
```


## Creating a HealthCheck

Healthchecks can be created using the healthcheck function as shown below.  The healthcheck function takes another function as its parameter.  This function can be used to check the health of your application.  The value returned from the function may optionally include a message or Throwable. If the value returned is a sequence, vector, or list, the first element is checked for truthiness to represent the health and the second element is the message or Throwable. Otherwise, the value returned is simply checked for truthiness to represent the health.

[todo_size.clj](dropwizard-clojure-example/src/main/clojure/com/example/todo/health/todo_size.clj)

```clojure
(def mock-hc (healthcheck (fn [] [true "I'm a mocked healthcheck"])))
```

### Registering a HealthCheck

As demonstrated in the configuration section healthchecks can be added by calling the register-healthcheck function.  This function expects a name for the healthcheck - supplied here as a keyword and the healthcheck itself.  As well as the function shown below that uses the environment to register the  healthcheck and returns the environment to allow threading.  Versions of the function exist to register and unregister healthchecks that take the application instance to enable repl based work flows.

```clojure
(-> env
          (register-healthcheck :mocked mock-hc)
```



## Building fat JARs

```sh
lein uberjar
```

## Running your Application

```sh
lein run server resources/dev-todo.yml
```

or

```sh
java -jar target/dropwizard-clojure-example-0.1.0-SNAPSHOT-standalone.jar server resources/dev-todo.yml
```
