(ns coinbox.player
  (:require [coinbox.gamestate :as gamestate :refer [resources]]
            [coinbox.animation :as anim]
            [coinbox.physics :as physics]
            [coinbox.coin :as coin]
            [coinbox.utils :as utils]
            [coinbox.box :as box])
  (:import [com.badlogic.gdx.graphics.g2d Animation]
           [com.badlogic.gdx.graphics Texture]
           [com.badlogic.gdx Gdx Input$Keys]))


(defn move-axis
  "Move w.r.t object's speed and delta time"
  [this game k f]
  (assoc this k (f (k this) (* (:speed this)
                               (:deltatime game)))))

(def jump-velocity 1300)
(def speed 800)

(def rigid-box {:u1 0.3
                :v1 0.0
                :u2 0.7
                :v2 0.9})

(defn applym
  [m f & ks]
  (apply f (map #(% m) ks)))

(defn render-box
  [body]
  ;; convert rigid normalized to render 
  (let [unit (/ (:w body) (- (:u2 rigid-box) (:u1 rigid-box)))]
    {:x (- (:x body) (* (:u1 rigid-box) unit))
     :y (- (:y body) (* (:v1 rigid-box) unit))
     :w unit
     :h unit}))

(defn body
  [x y size]
  {:x x
   :y y
   :w (* size (- (:u2 rigid-box) (:u1 rigid-box)))
   :h (* size (- (:v2 rigid-box) (:v1 rigid-box)))})

(defn move-right
  [this]
  (-> this
    (assoc :left? false)
    (assoc-in [:body :velx] speed)))

(defn move-left
  [this]
  (-> this
    (assoc :left? true)
    (assoc-in [:body :velx] (- speed))))

(defn jump
  [this]
  (if (zero? (-> this :body :y))
    ;; we're grounded, we can jump
    (assoc-in this [:body :vely] jump-velocity)
    ;; otherwise, we should not jump
    this))

(defn bop-box
  [game]
  (box/bop game 0.0 1.0 1600))

(defn pickup-coins
  [game this]
  )

(defn move
  [this game]
  (cond-> this
    ;; move right
    (.isKeyPressed Gdx/input Input$Keys/L) (move-right)
    ;; move left
    (.isKeyPressed Gdx/input Input$Keys/H) (move-left)
    ;; jump
    (.isKeyPressed Gdx/input Input$Keys/SPACE) (jump)
    ;; kill myself
    (.isKeyPressed Gdx/input Input$Keys/U) ((constantly nil))))

(defn spawn-coin
  [game this]
  (update-in game [:actors :coins] 
             #(vec (conj % (coin/coin (-> this :body :x) 
                                      (-> this :body :y))))))

(defn frame
  [this]
  (cond
    (pos? (-> this :body :y)) (:polka-jump @resources)
    (zero? (-> this :body :velx)) (:polka @resources)
    :else (anim/frame (:walking this))))

(defrecord Player
  [body left? walking]
  gamestate/Actor

  (act [this game] 
    (some-> this
        ;; reset velocity
        (assoc-in [:body :velx] 0)
        ;; move player
        (move game)
        ;; trigger physics
        (update :body #(physics/move % game))
        ;; update animation
        (assoc :walking (anim/run walking (:deltatime game)))))

  (act-globally [this game]
    ;; do something to the game state
    (cond-> game
      ;; spawn a coin
      (.isKeyJustPressed Gdx/input Input$Keys/W) (spawn-coin this)
      ;; bop the box
      (-> this :body :bopped?) (bop-box)))

  (draw [this game batch]
    ;; draw player
    (utils/reset-color batch)
    (let [tex (frame this)
          b (render-box body)]
      (.draw batch tex
             (float (:x b)) 
             (float (:y b))
             (float (:w b)) 
             (float (:h b))
             (int 0) (int 0)
             (.getWidth tex)
             (.getHeight tex)
             left? false))))

(defn player
  []
  (let [b (body 100 0 100)]
    (map->Player {:body (apply physics/body (map b [:x :y :w :h])) 
                  :left? false
                  :walking (anim/animation (:polka-walk @resources) 0.2)})))




(defn player []
  {:sprite {}})

