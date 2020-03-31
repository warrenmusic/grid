(ns wm.grid.cells.events
  (:require [re-frame.core :as rf]
            [wm.grid.cells.db :as db]))

(rf/reg-event-fx
 ::global-keyup-triggered
 (fn [_ [_ event]]
   (println "key pressed:" (.-key event))
   {:dispatch [::cell-key-pressed {:key (.-key event)}]}))

(rf/reg-event-fx
 ::cell-key-pressed
 (fn [{:keys [db]} [_ {:keys [key]}]]
   (when (:selected-cell-index db)
     (let [updated-cell (db/update-cell (:editing-cell db) key)]
       {:db (-> db
                (assoc-in [:cells (:selected-cell-index db)] (db/finalize-cell updated-cell))
                (assoc :editing-cell updated-cell)
                (as-> db (assoc db :sequence (db/cells->sequence (:base-pitch db) (:cells db)))))}))))

(rf/reg-event-fx
 ::cell-clicked
 (fn [{:keys [db]} [_ {:keys [index]}]]
   {:db (assoc db
               :selected-cell-index index
               :editing-cell (get-in db [:cells index]))}))
