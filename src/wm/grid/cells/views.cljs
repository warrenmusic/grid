(ns wm.grid.cells.views
  (:require [re-frame.core :as rf]
            [wm.grid.cells.events :as events]
            [wm.grid.cells.subs :as subs]))

(defn- cell [{:keys [index selected?]}]
  (let [display-value (if-not selected?
                        @(rf/subscribe [::subs/cell-display-value {:index index}])
                        @(rf/subscribe [::subs/editing-cell-display-value]))]
    [:div.w-10.h-10.bg-gray-100.flex.justify-center.items-center.font-mono.border-r.border-b.border-solid.border-gray-200
     {:class [(when selected? "border border-gray-500")]
      :on-click #(rf/dispatch [::events/cell-clicked {:index index}])}
     display-value]))

(defn cells []
  (let [selected-cell-index @(rf/subscribe [::subs/selected-cell-index])
        bars @(rf/subscribe [::subs/bars])]
    [:div
     (for [i (range 0 bars)
           :let [first-col-index (* 16 i)]]
       ^{:key i}
       [:div.flex
        (for [j (range 0 16)
              :let [index (+ first-col-index j)]]
          ^{:key j}
          [cell {:index index
                 :selected? (= selected-cell-index index)}])])]))
