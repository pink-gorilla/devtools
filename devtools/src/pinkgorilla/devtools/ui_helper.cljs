(ns pinkgorilla.devtools.ui-helper
  (:require
   [clojure.string :refer [split join]]
   [re-frame.core :as rf]))

;; comes from pinkie
;; but we need more customization!

(defn line-with-br [t]
  [:div
   [:span.font-mono.whitespace-pre t]
   [:br]])

(defn text2
  "Render text (as string) to html
   works with \\n (newlines)
   Needed because \\n is meaningless in html"
  ([t]
   (text2 {} t))
  ([opts t]
   (let [lines (split t #"\n")]
     (into
      [:div (merge {:class "textbox text-lg"} opts)]
      (map line-with-br lines)))))

;; block

(defn block [& children]
  (into [:div.bg-blue-400.m-5.inline-block {:class "w-1/4"}]
        children))

;; grid of cols

(defn s-cols [nr]
  (->> (take nr (repeatedly (fn [] "1fr ")))
       (join "")))

(defn grid [{:keys [cols background-color]
             :or {cols 2
                  background-color "orange"}} & children]
  (into ^:R [:div {:style {:display :grid
                           :grid-template-columns  (s-cols cols) ; "400px 400px 400px 400px" 
                           :background-color background-color}}]
        children))

;; rdocs

(defn rdoc-link [ns name]
  (str "/api/rdocument/file/" ns "/" name))

;; links

(defn link-fn [fun text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

;; styling

(defn h1 [t]
  [:h1.text-xl.text-red-900.mt-5 t])
