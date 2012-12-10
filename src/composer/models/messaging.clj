(ns composer.models.messaging
  (:use [slingshot.slingshot :only [try+ throw+]])
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
  (let [start (.indexOf x "<")]
    (if (>= start 0)
      (let [x (.substring x start)]
        (try
          (-> (java.io.ByteArrayInputStream. (.getBytes x "UTF8"))
            xml/parse
            collapse)
          (catch Exception e
            (println x)
            (throw e))))
      (println "NO XML:" x))))

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
  (fn [event] (key (first event))))

(defmethod apply-event nil
  [message]
  (apply-entity-event message))

(defmethod apply-event 7012
  [message]
  (apply-entity-event message))

(defmethod apply-event :EquipmentHistory
  [message]
  ; none in this data set
  (if-let [eq (or (get-in message [:EquipmentHistory :Destination :Equipment])
                  (get-in message [:EquipmentHistory :Source :Equipment]))]
    (dosync
      (alter equipment update-in [(eq :Type) (eq :Number) :count]
             (fnil inc 0)))))

(defmethod apply-event :EquipmentUpdate
  [message]
  )

(defmethod apply-event :MoveComplete
  [message]
  )

(defmethod apply-event :Move
  [message]
  )

(defmethod apply-event :Railcar
  [message]
  )

(defmethod apply-event :RailSchedule
  [message]
  )

(defmethod apply-event :Mission
  [message]
  )

(defmethod apply-event :MissionTaskUpdate
  [message]
  )

(defmethod apply-event :TransferLocation
  [message]
  )

(defmethod apply-event :Chassis
  [message]
  )

(defmethod apply-event :RailTrack
  [message]
  )


(defn consume
  [message]
  (let [x (xml-collapse message)
        m (get-in x [:TideworksDataExchange :Msg :MsgData])]
    (if m
      (try+
        (apply-event m)
        (catch map? e
          (throw+ x))
        (catch Exception ex
          (println x)
          (throw ex))))))

