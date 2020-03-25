(ns wm.grid.cells.pitches)

(defn pitch [s]
  (when-let [[_ class octave] (re-matches #"([A-G][b#]?)([0-9])" s)]
    {:class class :octave (some-> octave (js/Number.parseInt))}))

(def ^:private pitch-classes
  ["C" "C#" "D" "D#" "E" "F" "F#" "G" "G#" "A" "A#" "B"])

(defn- positions [pred coll] (keep-indexed (fn [idx x] (when (pred x) idx)) coll))

(defn transpose [p semitones]
  (let [{:keys [class octave]} (pitch p)
        delta-octaves ((if (pos? semitones) js/Math.floor js/Math.ceil) (/ semitones 12))
        delta-semitones (mod semitones 12)]
    (str (get pitch-classes (+ (first (positions #{class} pitch-classes)) delta-semitones))
         (+ octave delta-octaves))))
