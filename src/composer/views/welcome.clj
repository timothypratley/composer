(ns composer.views.welcome
  (:require [composer.views.common :as common])
  (:use [noir.core :only [defpage]]
        [hiccup.form]
        [slingshot.slingshot :only [try+]]
        [composer.models.messaging :only [consume]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to composer"]))

(defpage "/message" []
         (common/layout
           (form-to [:post "/m"]
                    (text-area "message")
                    (submit-button "submit"))))

(defpage [:post "/m"] {:keys [message]}
         (try+
           (let [result (consume message)]
             (common/layout result))
           (catch map? m
             (common/layout "Error:" m))))

