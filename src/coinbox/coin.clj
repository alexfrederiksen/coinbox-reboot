(ns coinbox.coin
  (:require [coinbox.physics :as physics]
            [coinbox.animation :as anim]
            [coinbox.gamestate :as gamestate :refer [resources]])
  (:import [com.badlogic.gdx.graphics Color]))

(defrecord Coin
  [body velx vely sprite color]
  gamestate/Actor
  (act [this game]
    (-> this
        (assoc :body (physics/move body game))
        (assoc :sprite (anim/run sprite (:deltatime game)))))
  (act-globally [this game] game)
  (draw [this game batch]
    (.setColor batch color)
    (.draw batch (anim/frame sprite) 
           (float (:x body)) 
           (float (:y body)) 
           (float (:w body)) 
           (float (:h body)))))

(defn pickup
  [this]
  this)

(defn random-color 
  []
  (Color. (float (rand)) 
          (float (rand)) 
          (float (rand)) 
          (float 1)))

(def coin-size 30)

(defn coin
  [x y]
  (map->Coin {:body (-> (physics/body (- x (/ coin-size 2)) 
                                  (- y (/ coin-size 2)) 
                                  coin-size coin-size)
                        (assoc :bounce 0.70))
              :sprite (anim/animation (:coin @resources) 0.15 anim/ping-pong)
              :color (random-color)}))
