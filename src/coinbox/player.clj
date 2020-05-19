(ns coinbox.player
  (:require [coinbox.gamestate :as gs]
            [coinbox.vec :as v]
            [coinbox.spritebatch :as b]
            [clojure.algo.monads :as m])
  (:import [com.badlogic.gdx Gdx Input$Keys]))

;; Initialization ##############################################################

(def size 100)

(defn init
  []
  (gs/put :player {
                   :vel (vector 0 0)
                   :pos (vector 0 0)
                  }))

;; Logic #######################################################################

(defn input
  "Capture input for player"
  []
  {:left?  (.isKeyPressed Gdx/input Input$Keys/H)
   :right? (.isKeyPressed Gdx/input Input$Keys/L)
   :up?    (.isKeyPressed Gdx/input Input$Keys/K)
   })

(defn push
  "Compute velocity by input"
  [this]
  (let [{:keys [left? right? up?]} (input)]
    ;; compute
    (update this :vel 
            (fn [cur-vel] 
              (cond->  (v/set-x cur-vel 0)
                left?  (v/set-x -500) 
                right? (v/set-x  500) 
                up?    (v/set-y  900)))) ))

(def gravity 5100)

(defn physics 
  [this delta]
  (update this :vel 
          (fn [vel] (v/add vel (v/scl [0 (- gravity)] delta) ))))

(defn collide
  [{[x y] :pos :as this}]
  (cond-> this
    (< y 0) (-> (update :pos (fn [pos] (v/set-y pos 0)))
                (update :vel (fn [vel] (v/set-y vel 0))) )))

(defn move
  "Move based on velocity"
  [this delta]
  (let [vel (get this :vel)
        pos (get this :pos)]
    (assoc this :pos 
           ;; compute new position 
           (v/add pos (v/scl vel delta)) )))

(defn tick
  "Tick the player
   tick :: State GameState ()"
  []
  (m/domonad m/state-m
             [
              delta (gs/fetch  :delta-time)
              _     (gs/mutate :player push)
              _     (gs/mutate :player physics delta)
              _     (gs/mutate :player move delta)
              _     (gs/mutate :player collide)
             ]
             nil))

(defn render
  [{[x y] :pos} batch]
  (b/draw batch (:polka @gs/resources) x y size size))
