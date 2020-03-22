(ns wm.grid.tone
  (:require [tone :as tone]))

;; TODO: Schedule audio events after tone/start

(defonce ^:private synth (.toMaster (tone/Synth.)))

(defn play-sequence! [notes]
  (let [sequence (tone/Sequence. (fn [time pitch] (.triggerAttackRelease synth pitch "16n" time)) notes "16n")]
    (.start sequence "1m")
    (.stop sequence "4m")
    (.start tone/Transport)))

(comment
  (play-sequence! ["C4" "E4" "G4"])

  (let [seq (tone/Sequence.
             (fn [time pitch]
               (.triggerAttackRelease synth pitch "4n" time))
             #js ["C4" "E4" "G4" "E4"]
             "4n")]
    (.start seq 0)

    (.start tone/Transport))

  (.stop tone/Transport)

  )
