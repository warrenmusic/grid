(ns wm.grid.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::base-pitch
 (fn [db]
   (:base-pitch db)))
