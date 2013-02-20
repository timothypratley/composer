(ns composer.neo_tests
  (:use [clojure.test])
  (:require [clojurewerkz.neocons.rest :as neorest]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [clojurewerkz.neocons.rest.nodes :as nodes]
            [clojurewerkz.neocons.rest.relationships :as relationships]))

(neorest/connect! "http://localhost:7474/db/data/")

(deftest index-tests
         (testing "how indexes work"
                  (let [container-index (nodes/create-index "containers")
                        name "TIMC1234567"
                        node {:name name}]
                    ;(nodes/create node [[container-index ["name" (node :name)]]])
                    ;(cy/tquery "CREATE UNIQUE n = {name:'TIMC1234567',weight:1}")
                    (println container-index)
                    (nodes/create-unique-in-index "containers" "name" (node :name) node)
                    (is (= (get-in (nodes/find-one "containers" "name" name) [:data :name]) name)))))

