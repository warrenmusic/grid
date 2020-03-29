(ns wm.grid.views
  (:require [clojure.string :as string]
            [re-frame.core :as rf]
            [wm.grid.pitches :as pitches]
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

(defn- play-controls []
  [:div.bg-gray-200.p-2.w-64.flex
   [:button.bg-gray-300.py-1.px-4.font-medium.w-full
    {:type "button"
     :on-click #(rf/dispatch [::events/play-button-clicked])}
    "Play"]
   [:button.bg-gray-300.py-1.px-4.ml-2.font-medium.w-full
    {:type "button"
     :on-click #(rf/dispatch [::events/stop-button-clicked])}
    "Stop"]])

(def ^:private base-pitch-options
  ["A3" "A#3" "B3" "C4" "C#4" "D4" "D#4" "E4" "F4" "F#4" "G4" "G#4"])

(defn- tonic-select []
  (let [selected-base-pitch @(rf/subscribe [::subs/base-pitch])]
    [:div.bg-gray-200.p-2.w-64.mt-4
     [:select.w-full
      {:value selected-base-pitch
       :on-change #(rf/dispatch [::events/base-pitch-changed (.-target.value %)])}
      (for [base-pitch base-pitch-options]
        ^{:key base-pitch}
        [:option
         {:value base-pitch}
         "Key of " (:class (pitches/pitch base-pitch)) " Major"])]]))

(defn- shareable-url []
  (let [shareable-url @(rf/subscribe [::subs/shareable-url])]
    [:div.bg-gray-200.p-2.w-64.mt-4
     [:button.bg-gray-300.py-1.px-4.font-medium.w-full
      {:type "button"
       :on-click #(rf/dispatch [::events/shareable-url-button-clicked])}
      "Get shareable URL"]
     (when shareable-url
       [:input.w-full.mt-2
        {:read-only true
         :value shareable-url}])]))

(defn root []
  [:div.absolute.w-full.min-h-screen.p-8
   [:div
    [meter-row]
    [cells.views/cells]]
   [:div.absolute.top-0.right-0.mt-8.mr-8
    [play-controls]
    [tonic-select]
    [shareable-url]]])
