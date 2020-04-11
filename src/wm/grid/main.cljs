(ns wm.grid.main
  (:require [clojure.string :as string]
            [cljs.reader :as reader]
            [reagent.dom]
            [re-frame.core :as rf]
            [wm.grid.cells.events :as cells.events]
            [wm.grid.events :as events]
            [wm.grid.views :as views]))

(defn- ^:dev/after-load mount-root! []
  (reagent.dom/render [views/root] (js/document.getElementById "app-root")))

(defn- add-global-event-listeners! []
  (js/window.addEventListener "keyup" #(rf/dispatch [::cells.events/global-keyup-triggered %])))

(defn- initial-state-from-url []
  (when-not (string/blank? js/location.hash)
    (reader/read-string (js/atob (subs js/location.hash 1)))))

(defn main! []
  (add-global-event-listeners!)
  (rf/dispatch-sync [::events/initialize (initial-state-from-url)])
  #_(set! (.-hash js/location) "")
  (mount-root!))
