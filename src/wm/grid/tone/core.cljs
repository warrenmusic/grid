(ns wm.grid.tone.core
  (:require [tone :as tone]))

;; TODO: Schedule audio events after tone/start

(defonce ^:private synth (.toMaster (tone/Synth.)))

(defn play! [pitches]
  (prn "pitches" pitches)
  (let [pitches (clj->js pitches)
        sequence (tone/Sequence.
                  (fn [time position]
                    (prn "position" position)
                    (when-let [pitch (get pitches position)]
                      (.triggerAttackRelease synth pitch "16n" time)))
                  (clj->js (range 0 (count pitches)))
                  "16n")]
    (.start sequence 0)
    (.start tone/Transport)))

(defn stop! []
  (.stop tone/Transport)
  (.cancel tone/Transport))
