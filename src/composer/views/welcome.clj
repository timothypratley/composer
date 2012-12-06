(ns composer.views.welcome
  (:use [noir.core :only [defpage]]
        [hiccup.form]
        [hiccup.page]
        [hiccup.core]
        [composer.models.messaging :only [consume]]))

(defpage "/" []
         (html5
           [:head
             [:title "composer"]
             [:meta {:name "viewport"
                     :content "width=device-width"
                     :initial-scale "1.0"}]
             [:link {:rel "icon"
                     :href "/img/favicon.ico"
                     :type "image/x-icon"}]
             [:link {:rel "shortcut"
                     :href "/img/favicon.ico"
                     :type "image/x-icon"}]
             (include-css "//netdna.bootstrapcdn.com/twitter-bootstrap/2.2.1/css/bootstrap-combined.min.css")
             (include-css "/css/composer.css")]
           [:body
             [:div.ng-view "Loading..."]

             (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js")
             (include-js "//netdna.bootstrapcdn.com/twitter-bootstrap/2.2.1/js/bootstrap.min.js")
             (include-js "//ajax.googleapis.com/ajax/libs/angularjs/1.0.2/angular.min.js")
             (include-js "/js/controllers.js")
             (include-js "/js/composer.js")]))

(defpage "/about" []
         (html
           [:p "Welcome to composer"]))

(defpage "/message" []
         (html
           (form-to [:post "/m"]
                    (text-area "message")
                    (submit-button "submit"))))

(defpage [:post "/m"] {:keys [message]}
         (if-let [result (consume message)]
           (html result)
           (html "Error")))

