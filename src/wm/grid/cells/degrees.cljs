(ns wm.grid.cells.degrees
  (:require [clojure.spec.alpha :as spec]))

;; TODO: A scale degree can also have an accidental, but we handle that elsewhere
(spec/def ::scale-degree (spec/and int? pos?))

(def ^:private semitones
  {1 0
   2 2
   3 4
   4 5
   5 7
   6 9
   7 11
   8 12})

(defn degree->semitones
  [n]
  {:pre [(spec/valid? ::scale-degree n)]}
  (let [octaves (js/Math.floor (/ n 8))]
    (+ (* 12 octaves) (get semitones (cond-> (mod n 8) (> n 8) (inc))))))
