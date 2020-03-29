(ns wm.grid.events
  (:require [re-frame.core :as rf]
            [wm.grid.tone.fx :as tone.fx]
            [wm.grid.cells.core :as cells]
            [wm.grid.db :as db]))

(def ^:private default-initial-state
  {:bars 18
   :base-pitch "C4"})

(rf/reg-event-fx
 ::initialize
 (fn [_ [_ initial-state]]
   {:db (-> (or initial-state default-initial-state)
            (as-> db (assoc db :sequence (cells/cells->sequence (:base-pitch db) (:cells db)))))}))

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
     {:db (-> db
              (assoc :playing? false)
              (dissoc :playing-cell-index))
      ::tone.fx/stop nil})))

(rf/reg-event-fx
 ::sequence-note-triggered
 (fn [{:keys [db]} [_ time index]]
   (let [pitch (get-in db [:sequence index])]
     (cond-> {:db (assoc db :playing-cell-index index)}
       (vector? pitch)
       (assoc ::tone.fx/play-chord {:pitches pitch
                                    :length "16n"
                                    :time time})
       (string? pitch)
       (assoc ::tone.fx/play-note {:pitch pitch
                                   :length "16n"
                                   :time time})))))

(rf/reg-event-fx
 ::base-pitch-changed
 (fn [{:keys [db]} [_ base-pitch]]
   {:db (assoc db
               :base-pitch base-pitch
               :sequence (cells/cells->sequence base-pitch (:cells db)))}))

(rf/reg-event-fx
 ::shareable-url-button-clicked
 (fn [{:keys [db]} _]
   {:db (assoc db :shareable-url (db/shareable-url db))}))
