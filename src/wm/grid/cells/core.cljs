(ns wm.grid.cells.core
  "A cell is a state machine that takes a single character of user input as an
  event and provides a new pitch value and a display value."
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as spec]
            [wm.grid.cells.pitches :as pitches]
            [wm.grid.cells.degrees :as degrees]))

(defn- non-blank [s] (when-not (string/blank? s) s))
(defn- non-zero [n] (when-not (= 0 n) n))

(spec/def ::degree (spec/and int? pos?))

(spec/def ::transpose (spec/and int? non-zero))

(spec/def ::display-value string?)

(spec/def ::cell (spec/keys :opt-un [::degree
                                     ::transpose
                                     ::display-value]))

(defn- handle-degree [cell s]
  (update cell :degree #(some-> (str % s)
                                (js/Number.parseInt))))

(defn- handle-transpose [cell s]
  (update cell :transpose (comp non-zero (if (= "+" s) inc dec))))

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
    :else cell))

(defn- transpose-str [n]
  (when n (string/join (repeat (js/Math.abs n) (if (pos? n) "+" "-")))))

(defn- update-cell-display-value [cell]
  (assoc cell :display-value (str (transpose-str (:transpose cell))
                                  (:degree cell))))

(defn update-cell [cell s]
  (-> (cond
        (some? (re-matches #"[0-9]" s)) (handle-degree cell s)
        (some? (re-matches #"[\+\-]" s)) (handle-transpose cell s)
        (= "Backspace" s) (handle-backspace cell)
        :else cell)
      (update-cell-display-value)))

(defn finalize-cell [cell]
  (cond-> cell
    (and (some? (:transpose cell)) (nil? (:degree cell))) (dissoc :transpose)
    :always (update-cell-display-value)))

(defn- pitches* [base-pitch [[i cell] & cells] pitches]
  (if cell
    (recur base-pitch
           cells
           (assoc pitches i (pitches/transpose base-pitch (get-in degrees/semitones [:major (:degree cell)]))))
    pitches))

(defn pitches [base-pitch cells] (pitches* base-pitch cells nil))
