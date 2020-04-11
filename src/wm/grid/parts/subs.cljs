(ns wm.grid.parts.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::parts
 (fn [db _]
   (vals (:parts db))))
