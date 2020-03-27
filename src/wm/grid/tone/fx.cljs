(ns wm.grid.tone.fx
  (:require [re-frame.core :as rf]
            [wm.grid.tone.core :as tone]))

(rf/reg-fx
 ::play
 (fn [{:keys [length on-trigger]}]
   (tone/play! {:length length
                :on-trigger #(rf/dispatch (conj on-trigger %1 %2))})))

(rf/reg-fx
 ::stop
 (fn [_]
   (tone/stop!)))

(rf/reg-fx
 ::play-note
 (fn [{:keys [pitch length time]}]
   (tone/play-note! pitch length time)))
