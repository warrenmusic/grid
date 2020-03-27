(ns wm.grid.events
  (:require [re-frame.core :as rf]
            [wm.grid.tone.fx :as tone.fx]
            [wm.grid.cells.core :as cells]))

(rf/reg-event-fx
 ::initialize
 (fn [_ _]
   {:db {:bars 18}}))

(rf/reg-event-fx
 ::play-button-clicked
 (fn [{:keys [db]} _]
   (prn (:sequence db))
   (when-not (:playing? db)
     {:db (assoc db :playing? true)
      ::tone.fx/play {:length (* 16 (:bars db))
                      :on-trigger [::sequence-note-triggered]}})))

(rf/reg-event-fx
 ::stop-button-clicked
 (fn [{:keys [db]} _]
   (when (:playing? db)
     {:db (assoc db :playing? false)
      ::tone.fx/stop nil})))

(rf/reg-event-fx
 ::sequence-note-triggered
 (fn [{:keys [db]} [_ time index]]
   (when-let [pitch (get-in db [:sequence index])]
     (if (vector? pitch)
       {::tone.fx/play-chord {:pitches pitch
                              :length "16n"
                              :time time}}
       {::tone.fx/play-note {:pitch pitch
                             :length "16n"
                             :time time}}))))
