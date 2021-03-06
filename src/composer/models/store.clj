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
; instead have change relations to users
;
; Does it matter if I have multiple relations instead of a set of times per relation?
; multiple relations seems easier to query
; but a set is easier to make idempotent


(ns composer.models.store
  (:require [clojurewerkz.neocons.rest               :as neorest]
            [clojurewerkz.neocons.rest.nodes         :as nodes]
            [clojurewerkz.neocons.rest.relationships :as relationships]))

(neorest/connect! "http://localhost:7474/db/data/")

(defn- neoupdate
  [idx keyname keyvalue]
  (nodes/create-unique-in-index idx keyname keyvalue {:name keyvalue}))

(defn- container
  [name]
  (neoupdate "container" "name" name))

(defn- equipment
  [name]
  (neoupdate "equipment" "name" name))

(defn- location
  [name]
  (neoupdate "location" "name" name))

;TODO why is set not supported?
(defn- relation
  [a b label]
  (relationships/maybe-create a b label))

;TODO: using a set makes an assumption that two events for the same relation will not occur in the same timestamp
;this is useful for idempotency, but is this a safe assumption?
(defn- update
  [rel t]
  (relationships/update rel (update-in (:data rel) [:at] (fnil conj #{}) t)))

; TODO: this should be in a transaction
(defn- container-update
  [eq c source t action passive where]
  (let [e (equipment (eq :Number))
        c (container c)
        n (cond
            (source :Equipment) (equipment (get-in source [:Equipment :Number]))
            (source :Location) (location (str (source :Location))))]
    (update (relation e c action) t)
    (update (relation n c passive) t)
    (update (relation e n where) t)))

(defn pick
  [eq container source t]
  (container-update eq container source t :picked :gave :picked-from))

(defn place
  [eq container destination t]
  (container-update eq container destination t :placed :received :placed-at))

