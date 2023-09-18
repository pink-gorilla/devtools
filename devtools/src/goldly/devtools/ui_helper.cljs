(ns goldly.devtools.ui-helper
  (:require
   [clojure.string :refer [split join]]
   [re-frame.core :as rf]
   [site]
   [layout]))

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

;; site layout

(defn devtools-header []
  [site/header-menu
   {:brand "Application"
    :brand-link "/"
    :items [{:text "scratchpad"  :dispatch [:bidi/goto 'scratchpad.page.scratchpad/scratchpad]}  ; :link "/devtools/scratchpad"
            {:text "repl"  :dispatch [:bidi/goto 'reval.goldly.page.repl/repl]}
            {:text "notebooks" :dispatch [:bidi/goto 'reval.goldly.page.notebook-viewer/viewer-page :query-params {}]} ;  :link "/devtools/viewer"
            {:text "theme"  :dispatch [:bidi/goto 'goldly.devtools.page.theme/theme-page]} ;  :link "/devtools/theme"
            {:text "build"  :dispatch  [:bidi/goto 'goldly.devtools.page.build/build-page]}
            {:text "runtime"  :dispatch  [:bidi/goto 'goldly.devtools.page.runtime/runtime-page]}
            {:text "pages"  :dispatch [:bidi/goto 'goldly.devtools.page.page-list/page-list-page]}  ; :link "/devtools/pages"
            {:text "help"  :dispatch [:bidi/goto 'goldly.devtools.page.help/devtools-page]}
            {:text "feedback" :link "https://github.com/pink-gorilla/goldly/issues" :special? true}]}])

(defn wrap-template [page]
  (fn [route]
    [layout/header-main  ; .w-screen.h-screen
     [devtools-header]
     [page route]]))

;; styling

(defn h1 [t]
  [:h1.text-xl.text-red-900.mt-5 t])

(rf/dispatch [:css/set-theme-component :tailwind-full "light"])
(rf/dispatch [:css/set-theme-component :tailwind-girouette false])