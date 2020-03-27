(ns wm.grid.tone.core
  (:require [tone :as tone]))

;; TODO: Schedule audio events after tone/start

(defonce ^:private synth (.toMaster (tone/Synth.)))

(defn play! [{:keys [length on-trigger]}]
  (let [sequence (tone/Sequence. on-trigger (clj->js (range 0 length)) "16n")]
    (.start sequence 0)
    (.start tone/Transport)))

(defn stop! []
  (.stop tone/Transport)
  (.cancel tone/Transport))

(defn play-note! [pitch length time]
  (.triggerAttackRelease synth pitch length time))
