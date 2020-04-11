(ns wm.grid.parts.views
  (:require [re-frame.core :as rf]
            [wm.grid.parts.events :as events]
            [wm.grid.parts.subs :as subs]))

(defn- create-button []
  [:button.bg-gray-300.py-1.px-4.font-medium.w-full
   {:on-click #(rf/dispatch [::events/create-button-clicked])}
   "Add part"])

(defn- edit-part [{:keys [id name]}]
  [:div.mb-2.flex
   [:div
    [:input.p-1.w-full
     {:type "text"
      :default-value name
      :on-change #(rf/dispatch [::events/name-updated {:id id :name (.-target.value %)}])}]]
   [:button.bg-gray-300.px-3.ml-2.font-medium
    {:on-click #(rf/dispatch [::events/delete-button-clicked {:id id}])}
    "X"]])

(defn- list-parts []
  (let [parts @(rf/subscribe [::subs/parts])]
    [:div
     (for [part parts] ^{:key (:id part)} [edit-part part])]))

(defn edit-parts-panel []
  (let [parts @(rf/subscribe [::subs/parts])]
    [:div.bg-gray-200.p-2.w-64.mt-4
     (when (< 1 (count parts)) [list-parts])
     [create-button]]))
