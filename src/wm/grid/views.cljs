(ns wm.grid.views
  (:require [clojure.string :as string]
            [re-frame.core :as rf]
            [wm.grid.events :as events]))

(defn- editable-cell [{:keys [row column]}]
  [:input.w-8.h-8.bg-gray-200.text-center.font-mono.border-r.border-b.border-solid.border-gray-300
   {:on-change #(rf/dispatch [::events/cell-changed {:row row
                                                     :column column
                                                     :value (.-target.value %)}])}])

(def ^:private meter-headers
  ["1" "e" "&" "a" "2" "e" "&" "a" "3" "e" "&" "a" "4" "e" "&" "a"])

(defn- meter-row []
  [:div.flex
   (for [[i text] (zipmap (range 0 (count meter-headers)) meter-headers)]
     ^{:key i}
     [:div.w-8.h-8.bg-gray-500.font-mono.flex.items-center.justify-center.border-gray-600.border-r.border-solid.font-bold
      text])])

(defn root []
  [:div
   [meter-row]
   (for [i (range 0 4)]
     ^{:key i}
     [:div
      (for [j (range 0 16)]
        ^{:key j}
        [editable-cell {:row i :column j}])])
   [:div.mt-4
    [:button.bg-gray-200.py-1.px-4.ml-4
     {:type "button"}
     "Play"]]])
