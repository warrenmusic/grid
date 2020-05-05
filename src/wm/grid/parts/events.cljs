(ns wm.grid.parts.events
  (:require [re-frame.core :as rf]))

(defonce ^:private part-name-counter (atom 0))

(defn- new-part [db]
  {:id (random-uuid)
   :name (str "Part " (swap! part-name-counter inc))
   :color "#f7fafc"})

(rf/reg-event-fx
 ::initialize
 (fn [{:keys [db]} _]
   (let [part (new-part db)]
     {:db (assoc-in db [:parts (:id part)] part)})))

(rf/reg-event-fx
 ::create-button-clicked
 (fn [{:keys [db]} _]
   (let [part (new-part db)]
     {:db (assoc-in db [:parts (:id part)] part)})))

(rf/reg-event-fx
 ::delete-button-clicked
 (fn [{:keys [db]} [_ {:keys [id]}]]
   {:db (update db :parts dissoc id)}))

(rf/reg-event-fx
 ::name-updated
 (fn [{:keys [db]} [_ {:keys [id name]}]]
   {:db (assoc-in db [:parts id :name] name)}))

(rf/reg-event-fx
 ::color-updated
 (fn [{:keys [db]} [_ {:keys [id color]}]]
   {:db (assoc-in db [:parts id :color] color)}))
