(ns wm.grid.cells.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::selected-cell-index
 (fn [db _]
   (:selected-cell-index db)))

(rf/reg-sub
 ::cell-display-value
 (fn [db [_ {:keys [index]}]]
   (get-in db [:cells index :display-value])))

(rf/reg-sub
 ::editing-cell-display-value
 (fn [db _]
   (get-in db [:editing-cell :display-value])))

(rf/reg-sub
 ::bars
 (fn [db _]
   (:bars db)))
