(ns coinbox.renderer
  (:require [coinbox.player :as player] 
            [coinbox.gamestate :as gs]
            [clojure.algo.monads :as m]))

(defn render
  [batch]
  (m/domonad m/state-m
             [
              ;; render the player
              _     (gs/mutate :player player/render batch)
             ]
             nil))
