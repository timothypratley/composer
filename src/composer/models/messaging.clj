(ns composer.models.messaging
  (:use [slingshot.slingshot :only [try+ throw+]]
        [composer.models.store])
  (:require [clojure.xml :as xml]))

(def model (ref {}))
(def equipment (ref {}))
(def everything (ref {}))

; TODO: I'd like to make this reverse compatible with hiccup, just because
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

(defn merge-with-no-default
  "Just like merge-with, but calls f even when the key is not present"
  [f & maps]
  (when (some identity maps)
    (let [merge-entry (fn [m e]
			(let [k (key e) v (val e)]
              (assoc m k (f (get m k) v))))
          merge2 (fn [m1 m2]
		    (reduce merge-entry (or m1 {}) (seq m2)))]
      (reduce merge2 maps))))

(def aggregate (fn self [a b]
  (if (map? b)
    (merge-with-no-default self a b)
    ((fnil conj #{}) a b))))

(defn every-event
  [m]
  (dosync
    (alter everything aggregate m)))

(defmulti apply-event (comp key first))

(def primary {:Railcar :No
              :EquipmentUpdate :Number
              :Move :Container
              :container :containerNo})

(defn entity-event
  [m]
  (let [t (first (keys m))
        entity (first (vals m))
        p (entity (primary t))
        now (java.util.Date.)]
    (if p
      (dosync
        (alter model update-in [t p now] conj entity))
      (throw+ m))))

(defmethod apply-event :default
  [message]
  (entity-event message))

;; These are handled by the default entity-event
#_(defmethod apply-event :Railcar [message])
#_(defmethod apply-event :EquipmentUpdate [message])
#_(defmethod apply-event :Move [message])


(defmethod apply-event :EquipmentHistory
  [message]
  ;(dosync
    (let [t (get-in message [:EquipmentHistory :TransitionTime])
          container (get-in message [:EquipmentHistory :Container :Number])
          source (get-in message [:EquipmentHistory :Source])
          destination (get-in message [:EquipmentHistory :Destination])
          update (fn [eq action at]
              (alter equipment update-in [(eq :Type) (eq :Number) :history]
                     conj [t action at]))]
      (if-let [eq (source :Equipment)]
        ;(update eq :place destination)
        (place eq destination))
      (if-let [eq (destination :Equipment)]
        ;(update eq :pick source)
        (pick eq source))))

(defmethod apply-event :Chassis [message])
(defmethod apply-event :RailTrack [message])
(defmethod apply-event :Mission [message])


(defmethod apply-event :MoveComplete
  [message]
  )



(defmethod apply-event :RailSchedule
  [message]
  )


(defmethod apply-event :MissionTaskUpdate
  [message]
  )

(defmethod apply-event :TransferLocation
  [message]
  )


(defn consume
  [message]
  (let [x (xml-collapse message)
        m (get-in x [:TideworksDataExchange :Msg :MsgData])]
    (if m
      (try+
        (every-event m)
        (apply-event m)
        ;(catch map? e
          ;(throw+ x))
        (catch Exception ex
          (println x)
          (throw ex))))))

