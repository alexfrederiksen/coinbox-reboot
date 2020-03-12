(ns coinbox.player
  (:require [coinbox.gamestate :as gamestate :refer [resources]]
            [coinbox.animation :as anim])
  (:import [com.badlogic.gdx.graphics.g2d Animation]
           [com.badlogic.gdx.graphics Texture]
           [com.badlogic.gdx Gdx Input$Keys]))

(defn move-axis
  "Move w.r.t object's speed and delta time"
  [this game k f]
  (assoc this k (f (k this) (* (:speed this)
                               (:deltatime game)))))


(defn move
  [this game]
  (cond-> this
    ;; move right
    (.isKeyPressed Gdx/input Input$Keys/L) (-> (move-axis game :x +) 
                                               (assoc :left? false)
                                               (assoc :velx 1))
    ;; move left
    (.isKeyPressed Gdx/input Input$Keys/H) (-> (move-axis game :x -) 
                                               (assoc :left? true)
                                               (assoc :velx -1))
    ;; kill myself
    (.isKeyPressed Gdx/input Input$Keys/U) ((constantly nil))))

(defn frame
  [this]
  (cond
    (zero? (:velx this)) (:polka @resources)
    :else (anim/frame (:walking this))))

(defrecord Player
  [x y velx jumping? left? speed walking]
  gamestate/Actor

  (act [this game] 
    (some-> this
        ;; reset velocity
        (assoc :velx 0)
        ;; move player
        (move game)
        ;; update animation
        (assoc :walking (anim/run walking (:deltatime game)))))

  (act-globally [this game]
    ;; do something to the game state
    )

  (draw [this game batch]
    ;; draw player
    (let [tex (frame this)]
      (.draw batch tex
             (float x) 
             (float y)
             (float 100) 
             (float 100)
             (int 0) (int 0)
             (.getWidth tex)
             (.getHeight tex)
             left? false))))

(defn player
  []
  (->Player 100 0 0 
            false false 500 
            (anim/animation (:polka-walk @resources) 0.2)))




