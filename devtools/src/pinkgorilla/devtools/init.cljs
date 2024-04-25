(ns pinkgorilla.devtools.init
  (:require 
    [pinkgorilla.devtools.core :refer [set-resolver! set-pages!]]
    [webly.spa.resolve :refer [get-resolver]]))

(defn start-devpages [dev-pages] 
  (println "setting devtools resolver to webly.spa.resolver ..")
  (set-resolver! (get-resolver))
  (println "dev-pages menu has : " (count dev-pages) " pages")
  (set-pages! dev-pages))

