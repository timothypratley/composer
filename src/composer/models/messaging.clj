(ns composer.models.messaging
  (:use [slingshot.slingshot :only [throw+]])
  (:require [clojure.xml :as xml]))

(def model (ref {}))

(defn collapse
  "Converts xml into a more condensed structure"
  [x not-found]
  (cond
    (and (vector? x) (string? (first x))) (first x)
    (vector? x) (let [s (map #(collapse % not-found) x)]
                  (if (vector? (first s))   ;key-value pairs
                    (into {} s)
                    s))
    (map? x) (if-let [c (x :content)]
               (merge x (collapse c))
               x)
    :else x))

(defn xml-collapse
  "Converts XML into a map"
  [x not-found]
  (try
    (-> (java.io.ByteArrayInputStream. (.getBytes x "UTF8"))
      xml/parse
      (collapse not-found))
    (catch Exception e
      (println x)
      (throw e))))

(def assoc-sorted (fnil assoc (sorted-map)))

(def primary {:Railcar :No
              :EquipmentUpdate :Number
              :Move :Container
              :container :containerNo})

(defn apply-entity-event
  [message]
  (let [m (xml-collapse message nil)
        type (m :tag)
        entity (m :attrs)
        content (m :content)
        p (entity (primary type))
        now (java.util.Date.)]
    (if p
      (dosync
        (alter model update-in [type p now] conj entity))
      (throw+ m))))

(defmulti apply-event
  (fn [domain event] (event :type)))

(defmethod apply-event 7012
  [message]
  (apply-entity-event message))

(defmethod apply-event 8020
  [message]
  (let [m (xml-collapse message nil)
        e (m :Equipment)]
    (dosync
      (alter equipment update-in [(e :Type) (e :Number)]))))

