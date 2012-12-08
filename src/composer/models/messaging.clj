(ns composer.models.messaging
  (:use [slingshot.slingshot :only [throw+]])
  (:require [clojure.xml :as xml]))

(def model (ref {}))
(def equipment (ref {}))

(defn collapse
  "Converts xml into a more condensed structure"
  [x]
  (let [k (:tag x)
        a (:attrs x)
        c (:content x)]
  (hash-map k (apply merge a (map collapse c)))))

(defn xml-collapse
  "Converts XML into a map"
  [x]
  (try
    (-> (java.io.ByteArrayInputStream. (.getBytes x "UTF8"))
      xml/parse
      collapse)
    (catch Exception e
      (println x)
      (throw e))))

(def assoc-sorted (fnil assoc (sorted-map)))

(def primary {:Railcar :No
              :EquipmentUpdate :Number
              :Move :Container
              :container :containerNo})

(defn apply-entity-event
  [m]
  (let [t (first (keys m))
        entity (first (vals m))
        p (entity (primary t))
        now (java.util.Date.)]
    (if p
      (dosync
        (alter model update-in [t p now] conj entity))
      (throw+ m))))

(defmulti apply-event
  (fn [event] (event :type)))

(defmethod apply-event nil
  [message]
  (apply-entity-event message))

(defmethod apply-event 7012
  [message]
  (apply-entity-event message))

(defmethod apply-event 8020
  [message]
  (let [e (message :Equipment)]
    (dosync
      (alter equipment update-in [(e :Type) (e :Number)]
             (fnil inc 0)))))

(defn consume
  [message]
  (let [m (get-in (xml-collapse message) [:TideworksDataExchange :Msg :MsgData])]
    (apply-event m)))

