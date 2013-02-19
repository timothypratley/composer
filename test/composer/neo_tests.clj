(ns composer.neo_tests
  (:use [clojure.test]
        [clojurewerkz.neocons.rest :as neorest]
        [clojurewerkz.neocons.rest.nodes :as nodes]
        [clojurewerkz.neocons.rest.relationships :as relationships]))

(neorest/connect! "http://localhost:7474/db/data/")

(deftest index-tests
         (testing "how indexes work"
                  (let [container-index (nodes/create-index "containers")
                        node {:name "TIMC1234567"}]
                    (nodes/create node [[container-index ["name" (node :name)]]])
                    (nodes/create-unique-in-index "containers" "name" (node :name) node)
                    (nodes/find "containers" "name" "TIMC1234567")))

