(ns goldly.devtools.websocket
  (:require
   [re-frame.core :as rf]))

;; websocket helper

(defn print-result [_t]
  (fn [r]
    (println "ws-send callback result: " r)))

(defn send-msg [{:keys [type args fn-callback timeout]
                 :or {args []
                      fn-callback (print-result type)
                      timeout 5000}}]
  (rf/dispatch [:ws/send [type args] fn-callback timeout]))

;; websocket status

(def connected-a
  (rf/subscribe [:ws/connected?]))

(defn ws-status []
  (fn []
    [:span.bg-blue-300 "ws connected: " (pr-str @connected-a)]))