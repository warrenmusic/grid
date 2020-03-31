(ns wm.grid.cells.db
  (:require [clojure.string :as string]
            [wm.grid.utils :refer [non-blank non-zero lower-case?]]
            [wm.grid.pitches :as pitches]))

(defn- numeral->integer [s]
  (loop [i 0 s (string/upper-case s)]
    (if (> (count s) 0)
      (cond
        (string/starts-with? s "X") (recur (+ i 10) (subs s 1))
        (string/starts-with? s "IX") (recur (+ i 9) (subs s 2))
        (string/starts-with? s "V") (recur (+ i 5) (subs s 1))
        (string/starts-with? s "IV") (recur (+ i 4) (subs s 2))
        (string/starts-with? s "I") (recur (+ i 1) (subs s 1)))
      i)))

(defn- integer->numeral [i]
  (loop [i i s nil]
    (if (> i 0)
      (cond
        (>= i 10) (recur (- i 10) (str s "X"))
        (>= i 9) (recur (- i 9) (str s "IX"))
        (>= i 5) (recur (- i 5) (str s "V"))
        (>= i 4) (recur (- i 4) (str s "IV"))
        (>= i 1) (recur (- i 1) (str s "I")))
      s)))

;; Cell Input Handlers

(defn- handle-degree [cell s]
  (-> cell
      (update :degree #(some-> (str % s) (js/Number.parseInt)))
      (dissoc :chord-quality :chord-root)))

(defn- handle-transpose [cell s]
  (-> cell
      (update :transpose (comp non-zero (if (= "+" s) inc dec)))))

(defn- handle-chord [cell s]
  (let [quality (if (lower-case? s) :minor :major)
        numeral (cond-> (str (:chord-numeral cell) s)
                  :always (-> (numeral->integer) (integer->numeral))
                  (= :minor quality) (string/lower-case)
                  (= :major quality) (string/upper-case))]
    (-> cell
        (dissoc :degree)
        (assoc :chord-quality quality
               :chord-root (numeral->integer numeral)
               :chord-numeral numeral))))

(defn- handle-accidental [cell s]
  (assoc cell :accidental (if (= "b" s) :flat :sharp)))

(defn- handle-backspace [cell]
  (cond
    (:degree cell)
    (update cell :degree #(some-> %
                                  (str)
                                  (subs 0 (dec (count (str %))))
                                  (non-blank)
                                  (js/Number.parseInt)))
    (:accidental cell)
    (dissoc cell :accidental)
    (:transpose cell)
    (update cell :transpose #(non-zero ((if (pos? %) dec inc) %)))
    (:chord-numeral cell)
    (let [numeral (subs (:chord-numeral cell) 0 (dec (count (:chord-numeral cell))))]
      (assoc cell
             :chord-numeral (non-blank numeral)
             :chord-root (non-zero (numeral->integer numeral))))
    :else cell))

(defn- transpose-str [cell]
  (when-let [n (:transpose cell)]
    (string/join (repeat (js/Math.abs n) (if (pos? n) "+" "-")))))

(defn- accidental-str [cell]
  ({:flat "b" :sharp "#"} (:accidental cell)))

(defn- update-cell-display-value [cell]
  (assoc cell :display-value (str (transpose-str cell)
                                  (accidental-str cell)
                                  (:degree cell)
                                  (:chord-numeral cell))))

(defn update-cell [cell s]
  (-> (cond
        (some? (re-matches #"[0-9]" s)) (handle-degree cell s)
        (some? (re-matches #"[\+\-]" s)) (handle-transpose cell s)
        (some? (re-matches #"[iIvVxX]" s)) (handle-chord cell s)
        (some? (re-matches #"[b#]" s)) (handle-accidental cell s)
        (= "Backspace" s) (handle-backspace cell)
        :else cell)
      (update-cell-display-value)))

(defn finalize-cell [cell]
  (cond-> cell
    (and (some? (:transpose cell)) (nil? (:chord-root cell)) (nil? (:degree cell)))
    (dissoc :transpose)
    (and (some? (:accidental cell)) (nil? (:chord-root cell)) (nil? (:degree cell)))
    (dissoc :accidental)
    :always (update-cell-display-value)))

;; Pitch Generation

(def ^:private degree-semitones
  {1 0
   2 2
   3 4
   4 5
   5 7
   6 9
   7 11
   8 12})

(defn- degree->semitones
  [n]
  {:pre [(and (int? n) (pos? n))]}
  (let [octaves (js/Math.floor (/ n 8))]
    (+ (* 12 octaves) (get degree-semitones (cond-> (mod n 8) (> n 8) (inc))))))

(defn cells->sequence [base-pitch cells]
  (loop [base-pitch base-pitch
         [[i cell] & cells] (sort-by key cells)
         sequence nil]
    (if cell
      (if-let [root-degree (or (:chord-root cell) (:degree cell))]
        (let [transposed-base-pitch (pitches/transpose base-pitch (* 12 (:transpose cell)))
              root (pitches/transpose transposed-base-pitch
                                      (cond-> (degree->semitones (or (:chord-root cell) (:degree cell)))
                                        (= :flat (:accidental cell)) (dec)
                                        (= :sharp (:accidental cell)) (inc)))]
          (recur transposed-base-pitch
                 cells
                 (assoc sequence i (if (:chord-root cell)
                                     (pitches/triad-chord root (:chord-quality cell))
                                     root))))
        (recur base-pitch cells sequence))
      sequence)))
