(ns wm.grid.tone.fx
  (:require [re-frame.core :as rf]
            [wm.grid.tone.core :as tone]))

(rf/reg-fx
 ::play
 (fn [{:keys [pitches]}]
   (tone/play! pitches)))

(rf/reg-fx
 ::stop
 (fn [_]
   (tone/stop!)))
