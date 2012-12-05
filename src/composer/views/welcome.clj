(ns composer.views.welcome
  (:require [composer.views.common :as common])
  (:use [noir.core :only [defpage]]
        [hiccup.form]
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
         (if-let [result (consume message)]
           (common/layout result)
           (common/layout "Error")))

