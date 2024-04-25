(ns pinkgorilla.devtools.core
  (:require
   [extension :refer [get-extensions]]))

(defn get-dev-config [exts]
  (->> (get-extensions exts {:dev-page {}})
       (map :dev-page)
       (apply merge)))

(defn config-dev [_module-name _config exts _default-config]
  (get-dev-config exts))

