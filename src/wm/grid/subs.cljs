(ns wm.grid.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::base-pitch
 (fn [db _]
   (:base-pitch db)))

(rf/reg-sub
 ::shareable-url
 (fn [db _]
   (:shareable-url db)))
