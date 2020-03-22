(ns wm.grid.main
  (:require [reagent.dom]
            [wm.grid.views :as views]))

(defn- ^:dev/after-load mount-root! []
  (reagent.dom/render [views/root] (js/document.getElementById "app-root")))

(defn main! []
  (mount-root!))
