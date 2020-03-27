(ns wm.grid.main
  (:require [reagent.dom]
            [re-frame.core :as rf]
            [wm.grid.cells.events :as cells.events]
            [wm.grid.events :as events]
            [wm.grid.views :as views]))

(defn- ^:dev/after-load mount-root! []
  (reagent.dom/render [views/root] (js/document.getElementById "app-root")))

(defn- add-global-event-listeners! []
  (js/window.addEventListener "keyup" #(rf/dispatch [::cells.events/global-keyup-triggered %])))

(defn main! []
  (add-global-event-listeners!)
  (rf/dispatch-sync [::events/initialize])
  (mount-root!))
