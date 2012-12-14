(ns composer.models.extract
  (:use [composer.models.messaging]
        [clojure-csv.core]))

(defn try-parse-int
  [s]
  (try
    (Integer/parseInt s)
    (catch Exception e
      nil)))
  
(defn read-csv
  [file]
  (doseq [m (map second
                 ;(map #(nth % 6)
                 ;(filter (comp try-parse-int #(nth % 4 nil))
                         (parse-csv (slurp file)))]
    (consume m))
  @everything)

