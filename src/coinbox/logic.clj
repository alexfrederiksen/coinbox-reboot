(ns coinbox.logic
  (:require [clojure.pprint :refer [pprint]] 
            [coinbox.utils :as utils]
            [coinbox.player :as player]
            [coinbox.gamestate :as gs]
            [clojure.algo.monads :as m]))

;; Some helper functions #######################################################

(m/defmonadfn m-bind*
  "Binds a monadic value to another monadic value, ignoring m1's underlying 
   value.
   m-bind* :: m a -> m b -> m b"
  [m1 m2]
  (m/m-bind m1 (fn [_] m2)))

(m/defmonadfn m-map*
  "Essentially mapcat monadic values, ignoring underlying values. Useful
   for composing monadic State values.
   m-map* :: [m a] -> m ()"
  [f coll]
  (reduce m-bind* (map f coll) ))

(defn ids [m]
  "Sequence of indices for m"
  (range (count m)))

;; Game initialization #########################################################

(defn init-state 
  "Initializes the gamestate
   init-state :: State GameState ()"
  []
  (m/domonad m/state-m 
             [
              ;; initalize player
              _ (player/init)

             ] 
             ;; don't care to return anything useful
             nil))


;; Game logic ##################################################################

;; (defn player-collide-coins
;;   "Perform player-coin collisions and return true if any happen
;;    player-collide-coins :: State GameState Bool"
;;   []
;;   (m/domonad m/state-m
;;              [
;;               ;; get the coins
;;               coins (get-coins)
;;               ;; and attempt to pickup each
;;               _     (m-map* pickup (ids coins))
;;              ]))
;; 

(defn tick
  "Ticks the game
   tick :: State GameState ()"
  [delta]
  (m/domonad m/state-m
             [
              ;; get delta time 
              _ (gs/put :delta-time delta)
              ;; tick the player
              _ (player/tick)
             ] 
             nil))

;; Testing stuff (shall be removed) ############################################

((init-state) {})

