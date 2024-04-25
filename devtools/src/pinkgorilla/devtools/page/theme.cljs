(ns pinkgorilla.devtools.page.theme
  (:require
   [re-frame.core :as rf]
   [clojure.string :as str]))

;; 3 functions from goldly.sci.eventhandler
;; but since goldly is not included in all builds it is copied here
;; todo: move to some kind of js lib

(defn- edn? [obj]
  (or (number? obj)
      (string? obj)
      (coll? obj)
      (boolean? obj)
      (nil? obj)
      (regexp? obj)
      (symbol? obj)
      (keyword? obj)))


(defn norm-evt [obj]
  (->> obj
       js/Object.getPrototypeOf
       js/Object.getOwnPropertyNames
       (map #(let [norm (-> %
                            (str/replace #"[A-Z]" (fn [r] (str "-" (str/lower-case r))))
                            keyword)]
               [norm (aget obj %)]))
       (filter (comp edn? second))
       (into {})))

(defn eventhandler-fn [fun]
  (fn [e & args]
    (try
      ;(info "eventhandler-fn args: " args) ; fun  - fun displays source
      ;(info "running eventhandler with state: " @state)
      (.preventDefault e)
      (.stopPropagation e)
      (let [t (.-target e)
            v (.-value t)
            e-norm (norm-evt t)
            _   (println "eventhandler v:" v  " e-norm: " e-norm " args: " args)
            fun-args [v e-norm] ; todo: allow additional properties , not only FUN
            fun-args (if (nil? args)
                       fun-args
                       (into [] (concat fun-args args)))
            _ (println "fun-args: " fun-args)]
        (apply fun fun-args))
      (catch :default err
        (println "eventhandler-fn exception: " err)))))

;; css links
(defn show-css-links [css-links]
  (let [css-links (or css-links [])]
    (into [:div.grid.grid-cols-1.md:grid-cols-2]
          (map (fn [n] [:span.m-1 n]) css-links))))

;; css theme by component

(defn comp-select [available k v]
  (let [o (or (keys (k available)) [v])
        on-change (fn [v e]
                      ;(infof "setting component: %s theme: %s" k nv)
                    (rf/dispatch [:css/set-theme-component k v]))]
    ;(error "avail: " o)
    (into [:select {:value  v
                    :on-change (eventhandler-fn on-change)}]
          (map (fn [o]
                 [:option {:value  o}
                  (str o)])
               o))))

(defn show-theme [{:keys [available current] :as theme}]
  [:table
   (into [:tbody
          [:tr
           [:td "component"]
           [:td "theme"]]]
         (map (fn [[k v]]
                [:tr
                 [:td [:span k]]
                 [:td (comp-select available k v)]])
              current))])

(defn theme-info []
  (let [theme (rf/subscribe [:css/theme])
        css-links (rf/subscribe [:css/app-theme-links])]
    (fn []
      [:div
       [:p.mt-5.mb-5.text-purple-600.text-3xl "components"]
       [show-theme @theme]
       [:p.mt-5.mb-5.text-purple-600.text-3xl "loaded css"]
       [show-css-links @css-links]])))

(defn theme-page [{:keys [route-params query-params handler] :as route}]
  [:div.container.mx-auto ; tailwind containers are not centered by default; mx-auto does this
   [theme-info]])


