(ns composer.api-tests
  (:use [clojure.test]
        [composer.models.store]))

(deftest store-tests
         (testing "store api"
                  (let [now (.getTime (java.util.Date.))]
                    (pick "WSC1" "CBHU1234567" {:Equipment "UTR1"} now)
                    (place "WSC1" "CBHU1234567" {:Location "TTXX2233"} now))))

