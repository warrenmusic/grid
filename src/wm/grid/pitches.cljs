(ns wm.grid.pitches
  (:require [wm.grid.utils :refer [positions]]))

(defn pitch [s]
  (when-let [[_ class octave] (re-matches #"([A-G][b#]?)([0-9])" s)]
    {:class class :octave (some-> octave (js/Number.parseInt))}))

(def ^:private pitch-classes
  ["C" "C#" "D" "D#" "E" "F" "F#" "G" "G#" "A" "A#" "B"])

(defn transpose [p semitones]
  (let [{:keys [class octave]} (pitch p)
        pitch-class-index (first (positions #{class} pitch-classes))
        delta-semitones (if (pos? semitones) (mod semitones 12) (- 0 (mod (js/Math.abs semitones) 12)))
        new-pitch-class-index (+ pitch-class-index delta-semitones)
        delta-octaves (cond-> ((if (pos? semitones) js/Math.floor js/Math.ceil) (/ semitones 12))
                        (> new-pitch-class-index 11) (inc)
                        (< new-pitch-class-index 0) (dec))]
    (str (get pitch-classes (mod new-pitch-class-index 12))
         (+ octave delta-octaves))))

(def ^:private chord-semitones
  {:major [0 4 7]
   :minor [0 3 7]})

(defn triad-chord [root-pitch quality]
  (mapv #(transpose root-pitch %) (chord-semitones quality)))
