(ns wm.grid.cells.views
  (:require [re-frame.core :as rf]
            [wm.grid.cells.events :as events]
            [wm.grid.cells.subs :as subs]))

(defn- cell [{:keys [index selected? playing? background-color]}]
  (let [display-value (if-not selected?
                        @(rf/subscribe [::subs/cell-display-value {:index index}])
                        @(rf/subscribe [::subs/editing-cell-display-value]))]
    [:div.w-10.h-10.flex.justify-center.items-center.font-mono.border-r.border-b.border-solid.border-gray-200
     {:class [(when selected? "border border-gray-500") (when playing? "bg-gray-200")]
      :on-click #(rf/dispatch [::events/cell-clicked {:index index}])
      :style {:background-color (when-not playing? background-color)}}
     display-value]))

;; TODO: Re-factor this to avoid depdending on the styles of the parent element
(defn cells []
  (let [selected-cell-index @(rf/subscribe [::subs/selected-cell-index])
        playing-cell-index @(rf/subscribe [::subs/playing-cell-index])
        bars @(rf/subscribe [::subs/bars])
        parts @(rf/subscribe [::subs/parts])
        show-parts? @(rf/subscribe [::subs/show-parts?])]
    (into [:<>]
          (for [bar-index (range 0 bars)
                part parts
                :let [first-col-index (* 16 bar-index)]]
            [:<>
             [:div.flex.justify-center.items-center.font-mono.font-bold.border-t.border-r.border-l.border-solid.border-gray-200.px-4.text-sm
              {:style {:background-color (:color part)}}
              (when show-parts? (:name part))]
             [:div.flex
              (for [col-index (range 0 16)
                    :let [cell-index (+ first-col-index col-index)]]
                ^{:key (str (:id part) cell-index)}
                [cell {:index cell-index
                       :selected? (= selected-cell-index cell-index)
                       :playing? (= playing-cell-index cell-index)
                       :background-color (:color part)}])]]))))
