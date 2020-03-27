(ns wm.grid.cells.core
  "A cell is a state machine that takes a single character of user input as an
  event and provides a new pitch value and a display value."
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as spec]
            [wm.grid.cells.pitches :as pitches]
            [wm.grid.cells.degrees :as degrees]))

(defn- non-blank [s] (when-not (string/blank? s) s))
(defn- non-zero [n] (when-not (= 0 n) n))
(defn- lower-case? [s] (= s (string/lower-case s)))

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

(spec/def ::degree (spec/and int? pos?))

(spec/def ::transpose (spec/and int? non-zero))

(spec/def ::display-value string?)

(spec/def ::cell (spec/keys :opt-un [::degree
                                     ::transpose
                                     ::display-value]))

(defn- handle-degree [cell s]
  (-> cell
      (update :degree #(some-> (str % s) (js/Number.parseInt)))
      (dissoc :chord-quality :chord-root)))

(defn- handle-transpose [cell s]
  (-> cell
      (update :transpose (comp non-zero (if (= "+" s) inc dec)))
      (dissoc :chord-quality :chord-root)))

(defn- handle-chord [cell s]
  (let [quality (if (lower-case? s) :minor :major)
        numeral (cond-> (str (:chord-numeral cell) s)
                  :always (-> (numeral->integer) (integer->numeral))
                  (= :minor quality) (string/lower-case)
                  (= :major quality) (string/upper-case))]
    (-> cell
        (dissoc :degree :transpose)
        (assoc :chord-quality quality
               :chord-root (numeral->integer numeral)
               :chord-numeral numeral))))

(defn- handle-backspace [cell]
  (cond
    (:degree cell)
    (update cell :degree #(some-> %
                                  (str)
                                  (subs 0 (dec (count (str %))))
                                  (non-blank)
                                  (js/Number.parseInt)))
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

(defn- update-cell-display-value [cell]
  (assoc cell :display-value (str (transpose-str cell)
                                  (:degree cell)
                                  (:chord-numeral cell))))

(defn update-cell [cell s]
  (-> (cond
        (some? (re-matches #"[0-9]" s)) (handle-degree cell s)
        (some? (re-matches #"[\+\-]" s)) (handle-transpose cell s)
        (some? (re-matches #"[iIvVxX]" s)) (handle-chord cell s)
        (= "Backspace" s) (handle-backspace cell)
        :else cell)
      (update-cell-display-value)))

(defn finalize-cell [cell]
  (cond-> cell
    (and (some? (:transpose cell)) (nil? (:degree cell))) (dissoc :transpose)
    :always (update-cell-display-value)))

(defn- pitches* [base-pitch [[i cell] & cells] pitches]
  (if cell
    (let [transposed-base-pitch (pitches/transpose base-pitch (* 12 (:transpose cell)))]
      (recur transposed-base-pitch
             cells
             (assoc pitches i (pitches/transpose transposed-base-pitch
                                                 (get-in degrees/semitones [:major (:degree cell)])))))
    pitches))

(defn pitches [base-pitch cells] (pitches* base-pitch cells nil))
