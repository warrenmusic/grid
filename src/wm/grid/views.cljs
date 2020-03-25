(ns wm.grid.views
  (:require [clojure.string :as string]
            [re-frame.core :as rf]
            [wm.grid.cells.views :as cells.views]
            [wm.grid.events :as events]
            [wm.grid.subs :as subs]))

(def ^:private meter-headers
  ["1" "e" "&" "a" "2" "e" "&" "a" "3" "e" "&" "a" "4" "e" "&" "a"])

(defn- meter-row []
  [:div.flex
   (for [[i text] (zipmap (range 0 (count meter-headers)) meter-headers)]
     ^{:key i}
     [:div.w-10.h-10.bg-gray-300.font-mono.flex.items-center.justify-center.border-gray-400.border-r.border-solid.font-bold
      text])])

(defn root []
  [:div.absolute.w-full.h-full.flex.justify-center.items-center
   [:div
    [meter-row]
    [cells.views/cells]
    [:div.mt-4
     [:button.bg-gray-200.py-1.px-4.ml-4
      {:type "button"
       :on-click #(rf/dispatch [::events/play-button-clicked])}
      "Play"]
     [:button.bg-gray-200.py-1.px-4.ml-4
      {:type "button"
       :on-click #(rf/dispatch [::events/stop-button-clicked])}
      "Stop"]]]])
