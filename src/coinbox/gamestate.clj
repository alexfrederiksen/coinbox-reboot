(ns coinbox.gamestate
  (:import [com.badlogic.gdx.graphics Texture]
           [com.badlogic.gdx Gdx]))

;; the game state (e.g. objects, cameras, etc.)
(defonce state (atom nil))

;; for screen swapping
(defonce game (atom nil))

;; textures and sounds
(defonce resources (atom nil))

(def resource-names
  {:polka "polka-stand-0.png"})

(defn load-tex
  [tname]
  (Texture. tname))

(defmacro on-gl
  [form]
  `(.postRunnable (Gdx/app)
                  (fn [] ~form)))

(defn load-resources
  []
  (println "Loading resources...")
  (->> (into [] resource-names)
       ;; load textures in second slots
       (map #(update % 1 load-tex))
       ;; rebuild map
       (into {})
       ;; load atom
       (reset! resources)))


