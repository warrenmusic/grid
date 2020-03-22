(ns wm.grid.events
  (:require [re-frame.core :as rf]
            [wm.grid.tone.fx :as tone.fx]))

(rf/reg-event-fx
 ::cell-changed
 (fn [{:keys [db]} [_ {:keys [row column value]}]]
   (prn "db" db)
   {:db (if (= "" value)
          (update-in db [:cells row] dissoc column)
          (assoc-in db [:cells row column] (js/Number.parseInt value)))}))

(def scale-degree->pitch
  {1 "C4"
   2 "D4"
   3 "E4"
   4 "F4"
   5 "G4"
   6 "A4"
   7 "B4"
   8 "C5"})

(defn- flatten-cells [cells]
  (->> (repeat 4 (range 0 16))
       (map-indexed (fn [i js] (map #(get-in cells [i %]) js)))
       (flatten)))

(defn- cells->pitches [cells]
  (->> (flatten-cells cells)
       (map scale-degree->pitch)))

(rf/reg-event-fx
 ::play-button-clicked
 (fn [{:keys [db]} _]
   (prn "db" db)
   {::tone.fx/play {:pitches (cells->pitches (:cells db))}}))

(rf/reg-event-fx
 ::stop-button-clicked
 (fn [_ _]
   {::tone.fx/stop nil}))
