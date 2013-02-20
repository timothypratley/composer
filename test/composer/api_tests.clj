(ns composer.api-tests
  (:use [clojure.test]
        [composer.models.store]))

(deftest store-tests
         (testing "store api"
                  (pick "WSC1" "CBHU1234567" :equipment "UTR1")
                  (place "WSC1" "CBHU1234567" :railcar "TTXX2233")))

