; Questions I want to answer:
; tell me everything that happened to container X
; tell me everything that happened to equipment Y
; tell me everything that went through spot Z
; tell me everything that operator W did
; who changed the sizetype of container V
; 
; These can be answered by filtering facts that have those entities in them.
; Facts can be represented as tripples with dates and sources
; Entity1 predicate (Entity2|value) @Date source
;
; But instead I use a graph with list of dates in the predicate relation
; E1 R[@D] E2
; which stores the same information but fits nicely in neo4j
; to capture updates, we need a relation from users to entities with the update contents
; the entities themselves can contain the current values of properties
; but is ugly for real number values of E2


(ns composer.models.store
  (:require [clojurewerkz.neocons.rest               :as neorest]
            [clojurewerkz.neocons.rest.nodes         :as nodes]
            [clojurewerkz.neocons.rest.relationships :as relationships]))

(neorest/connect! "http://localhost:7474/db/data/")

(defn container
  [name properties]
  (nodes/find "container" name))

(defn pick
  [eq container destination]
  (let [from (or (nodes/find "Equipment" "Number" eq) (nodes/create eq ["Equipment"]))
        ;to (or (nodes/find "Equipment" "Number" dest) (nodes/create dest ["Container"]))
        c (or (nodes/find "Container" "Number" container) (nodes/create container ["Container"]))]
    (relationships/create from to :links)))

(defn place
  [eq container]
  (let [from (nodes/create eq)
        to (nodes/create {:no "TIM12345"})]
    (relationships/create from to :links)))

