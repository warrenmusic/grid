(ns wm.grid.db)

(defn- session-state [db]
  (select-keys db [:bars :base-pitch :cells]))

(defn shareable-url [db]
  (let [state-str (js/btoa (pr-str (session-state db)))
        url (js/URL. js/location)]
    (set! (.-pathname url) "/")
    (set! (.-hash url) state-str)
    (.toString url)))
