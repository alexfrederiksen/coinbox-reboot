(ns coinbox.gamestate
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [coinbox.utils :as utils])
  (:import [com.badlogic.gdx.graphics Texture]
           [com.badlogic.gdx Gdx]))

;; Data ########################################################################

;; the game state (e.g. objects, cameras, etc.)
(defonce state (atom nil))

;; for screen swapping
(defonce game (atom nil))

;; textures and sounds
(defonce resources (atom nil))

;; Monadic state ###############################################################

(defn fetch
  [k]
  (fn [s] [(get s k) s]))

(defn put
  [k v]
  (fn [s] [s (assoc s k v)]))

(defn mutate
  [k f & args]
  (fn [s] [s (apply update s k f args)]))

(defn fetch-one
  [k index]
  (fn [s] [(get-in s (list k index)) s]))

(defn put-one
  [k index v]
  (fn [s] [s (assoc-in s (list k index) v)]))

(defn mutate-one
  [k index f]
  (fn [s] [s (update-in s (list k index) f)]))

(defmacro defstate
  "Creates a new state definition"
  [obj-name ks]
  `(def ~obj-name ~ks))

(defn defgroupstate
  [obj-name ks]
  )

;; Resource management #########################################################

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



