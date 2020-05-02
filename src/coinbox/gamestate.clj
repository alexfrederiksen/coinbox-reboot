(ns coinbox.gamestate
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [coinbox.utils :as utils])
  (:import [com.badlogic.gdx.graphics Texture]
           [com.badlogic.gdx Gdx]))

;; define an actor for our game objects
(defprotocol Actor
  (act [this game])
  (act-globally [this game])
  (draw [this game batch]))

(extend-type java.lang.Object
  Actor
  (act [this game]
    (-> (str "Acting not implemented for " (type this))
        (Exception.)
        (throw)))
  (act-globally [this game]
    (-> (str "Acting globally not implemented for " (type this))
        (Exception.)
        (throw)))
  (draw [this game batch]
    (-> (str "Drawing not implemented for " (type this))
        (Exception.)
        (throw))))

;; the game state (e.g. objects, cameras, etc.)
(defonce state (atom nil))

;; for screen swapping
(defonce game (atom nil))

;; textures and sounds
(defonce resources (atom nil))


(defn load-tex
  [tname]
  (println (str "Attempting to load " tname "..."))
  (if (-> (io/resource tname) (nil?))
    ;; return nil
    nil
    ;; otherwise load texture
    (Texture. tname)))

(defn find-animation
  [root]
  (let [[lroot rroot] (string/split root #"\." 2)]
    (take-while (complement nil?) 
                (map #(load-tex (str lroot "-" % "." rroot)) (range)))))


(defprotocol Loadable
  (load-res [this]))

(extend-type java.lang.String
  Loadable
  (load-res [this] (load-tex this)))

(deftype AnimationLoader [root-name]
  Loadable
  (load-res [this] (doall (find-animation root-name))))

(def resource-names
  {:polka "polka-stand-0.png"
   :polka-jump "polka-jump.png"
   :polka-walk (->AnimationLoader "polka-walk.png")
   :coin (->AnimationLoader "coin.png")
   :box "coinbox.png"
   :box-spook "coinbox-spook.png"})


(defn load-resources
  []
  (println "Loading resources...")
  (as-> resource-names $
       ;; load textures in second slots
       (utils/map-kv $ load-res) 
       ;; load atom
       (reset! resources $)))

(defmacro on-gl
  [form]
  `(.postRunnable (Gdx/app)
                  (fn [] ~form)))


