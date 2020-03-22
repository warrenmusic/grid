(ns wm.grid.events
  (:require [re-frame.core :as rf]
            [wm.grid.tone :as tone]))

(rf/reg-event-fx
 ::cell-changed
 (fn [{:keys [db]} {:keys [row column value]}]
   {:db (assoc-in db [:cells row column] value)}))
