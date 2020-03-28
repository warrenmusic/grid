(ns wm.grid.utils
  (:require [clojure.string :as string]))

(defn non-blank [s] (when-not (string/blank? s) s))

(defn non-zero [n] (when-not (= 0 n) n))

(defn lower-case? [s] (= s (string/lower-case s)))

(defn positions [pred coll] (keep-indexed (fn [idx x] (when (pred x) idx)) coll))
