(ns wm.grid.cells.pitches)

(defn pitch [s]
  (when-let [[_ class octave] (re-matches #"([A-G][b#]?)([0-9])" s)]
    {:class class :octave (some-> octave (js/Number.parseInt))}))

(def ^:private pitch-classes
  ["C" "C#" "D" "D#" "E" "F" "F#" "G" "G#" "A" "A#" "B"])

(defn- positions [pred coll] (keep-indexed (fn [idx x] (when (pred x) idx)) coll))

(defn transpose [p semitones]
  (let [{:keys [class octave]} (pitch p)
        current-octave-index (first (positions #{class} pitch-classes))
        delta-semitones (mod semitones 12)
        delta-octaves (cond-> ((if (pos? semitones) js/Math.floor js/Math.ceil) (/ semitones 12))
                        (> (+ current-octave-index delta-semitones) 11) (inc))]
    (str (get pitch-classes (mod (+ current-octave-index delta-semitones) 12))
         (+ octave delta-octaves))))

(def ^:private chord-semitones
  {:major [0 4 7]
   :minor [0 3 7]})

(defn triad-chord [root quality]
  (mapv #(transpose root %) (chord-semitones quality)))
