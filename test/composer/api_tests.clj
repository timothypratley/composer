(ns composer.api-tests
  (:use [clojure.test]
        [composer.models.store]))

(pick {:Number "WSC1"} {:Number "CBHU1234567"} {:Block "TTR"})
(place {:Number "WSC1"} {:Number "CBHU1234567"} {:Railcar "TTXX2233"})

