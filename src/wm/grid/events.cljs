(ns wm.grid.events
  (:require [re-frame.core :as rf]
            [wm.grid.tone.fx :as tone.fx]
            [wm.grid.cells.core :as cells]))

(rf/reg-event-fx
 ::play-button-clicked
 (fn [{:keys [db]} _]
   {::tone.fx/play {:pitches (cells/pitches "C4" (:cells db))}}))

(rf/reg-event-fx
 ::stop-button-clicked
 (fn [_ _]
   {::tone.fx/stop nil}))
