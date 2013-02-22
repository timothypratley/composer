(ns composer.neo_tests
  (:use [clojure.test])
  (:require [clojurewerkz.neocons.rest :as neorest]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [clojurewerkz.neocons.rest.nodes :as nodes]
            [clojurewerkz.neocons.rest.relationships :as relationships]))

(neorest/connect! "http://localhost:7474/db/data/")

(deftest index-tests
         (testing "relations"
                  (let [a (nodes/create {:name "testA"})
                        b (nodes/create {:name "testB"})
                        r (relationships/create a b :friend)
                        m (relationships/maybe-create a b :test {:name "test" :at #{}})
                        n (relationships/update m {:name "test" :at #{1 2}})]
                    (println m)
                    (println n)))

         (testing "how indexes work"
                  (let [container-index (nodes/create-index "container")
                        name "TEST1234567"
                        node {:name name}]
                    ;(nodes/create node [[container-index ["name" (node :name)]]])
                    ;(cy/tquery "CREATE UNIQUE n = {name:'TIMC1234567',weight:1}")
                    (println container-index)
                    (nodes/create-unique-in-index "container" "name" (node :name) node)
                    (is (= (get-in (nodes/find-one "container" "name" name) [:data :name]) name)))))

