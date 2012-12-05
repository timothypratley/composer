(ns composer.models.messaging
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
    (map? x) x
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

(def primary {"Railcar" "No"
              "EquipmentUpdate" "Number"
              "Move" "Container"
              "container" "containerNo"})

(defn consume
  [message]
  (let [m (xml-collapse message nil)
        type (m :tag)
        entity (m :attrs)
        p (entity (primary type))
        now (java.util.Date.)]
    (dosync
      (alter model update-in [type p now] conj entity))))

