(ns composer.views.welcome
  (:use [clojure.pprint :only [pprint]]
        [noir.core :only [defpage]]
        [hiccup.element]
        [hiccup.form]
        [hiccup.page]
        [slingshot.slingshot :only [try+]]
        [hiccup.core]
        [composer.models.extract]
        [composer.models.messaging]))

(def header
  [:header.navbar.navbar-fixed-top {:ng-controller "TopCtrl"}
   [:div.navbar-inner
    [:a.brand {:href "/#/"}
     [:img {:src "/img/favicon.ico" :width "20" :height "20"}]
     [:strong "oneup"]]
    [:ul.nav
     [:li.divider-vertical]
     [:li (link-to "/#/about" "About")]
     [:li.divider-vertical]
     [:li (link-to "/#/schedule" "Schedule")]
     [:li.divider-vertical]
     [:li (link-to "/#/message" "Message")]
     [:li.divider-vertical]]
    [:div.login.ng-cloak.pull-right {:ng-show "!user.username"}
     (link-to "/#/register" "Register")
     (submit-button {:ng-click "login()"} "Login")]
    [:div.logout.ng-cloak.pull-right {:ng-show "user.username"}
     [:span "{{user.username}}"]
     (submit-button {:ng-click "logout()"} "logout")]]])

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
            header
            [:div.ng-view "Loading..."]

            (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js")
            (include-js "//netdna.bootstrapcdn.com/twitter-bootstrap/2.2.1/js/bootstrap.min.js")
            (include-js "//ajax.googleapis.com/ajax/libs/angularjs/1.0.2/angular.min.js")
            (include-js "https://www.google.com/jsapi")
            (include-js "/js/charts.js")
            (include-js "/js/controllers.js")
            (include-js "/js/composer.js")]))

(defpage "/partial/about" []
         (html
           [:p "Welcome to composer"]))

(defpage "/partial/schedule" []
         (html
           [:p "hi"]
           [:div {:chart "schedule"}]))

(defpage "/partial/message" []
         (html
           (form-to [:post "/m"]
                    (text-area "message")
                    (submit-button "submit"))
           (form-to {:enctype "multipart/form-data"} [:post "/upload/file"]
                    (file-upload "file")
                    (submit-button "submit"))))

(defpage [:post "/m"] {:keys [message]}
         (try+
           (let [result (consume message)]
             (html result))
           (catch map? m
             (html "Error:" m))))

(defn pp-str [m] 
  (let [w (java.io.StringWriter.)]
    (pprint m w)
    (.toString w)))

(defpage [:post "/upload/file"] {:keys [file]}
         (try+
           (html [:pre (pp-str (read-csv (:tempfile file)))])
           (catch map? m
             (html [:pre (pp-str m)]))))


