(ns pinkgorilla.devtools.core
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [promesa.core :as p]
   [ui.site.template]
   [ui.site.layout]))

(def resolver (atom nil))

(defn set-resolver! [r] (reset! resolver r))

(defonce pages (atom {}))

(defn set-pages! [p] (reset! pages p))

(defn available-pages []
  (-> @pages keys))

(defn get-page [page-name]
  (get @pages page-name))

;; site layout

(defn page-menu [page-name]
  {:text page-name
   :dispatch [:bidi/goto 'pinkgorilla.devtools.core/devtools-page :name page-name]})

(defn devtools-header []
  (let [pages (available-pages)]
    (println "devtools pages:" pages)
    [ui.site.template/header-menu
     {:brand "Application"
      :brand-link "/"
      :items (into [{:text "feedback" :link "https://github.com/pink-gorilla/goldly/issues" :special? true}]
                   (map page-menu pages))}]))

(defn message [m]
  (fn [_route]
    [:div m]))


(defn handler-promise-view [hp]
  (let [view (r/atom (message "loading page.."))]
    (p/then hp (fn [result]
                 (println "devtools page-promise has been resolved!")
                 (println "result: " result)
                 (reset! view result)))
    (p/catch hp (fn [err]
                  (println "error in resolving devtools page-promise: " err)
                  (reset! view (message [:div.text-xl.text-bold.text-blue-500.bg-red-200
                                         [:p "devtools page load error!"]
                                         [:p.text-red-500
                                          (str err)]]))))
    view))

(defn resolve-page [page-s]
  (println "devtools resolving-page: " page-s)
  (let [resolve-fn @resolver]
    (resolve-fn page-s)))

(defn view-page [{:keys [route-params query-params handler] :as route}]
  (let [page-name (or (:name route-params) "pinkgorilla")
        page-name (if (str/starts-with? page-name "/")
                    (subs page-name 1)   
                    page-name)
        page-fn (get-page page-name)
        _ (println "page name: " page-name " page-fn: " page-fn "route: " route)
        h (resolve-page page-fn)
        view (if (p/promise? h)
               (handler-promise-view h)
               (r/atom h))]
    (fn [route]
      [@view route])))


(defn devtools-page [route]
  [ui.site.layout/header-main
   [devtools-header]
   [view-page route]])
