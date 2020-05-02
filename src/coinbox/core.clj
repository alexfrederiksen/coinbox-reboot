(ns coinbox.core
  (:require [coinbox.gamestate :as gamestate]
            [coinbox.screens :as screens])
  (:import [java.lang Exception] 
           [com.badlogic.gdx Game Gdx Input$Keys])
  (:gen-class
   :name coinbox.core.Game
   :extends com.badlogic.gdx.Game
   :exposes-methods {render parentRender}))

(defn -render [this]
  (when (.isKeyJustPressed (Gdx/input) Input$Keys/ESCAPE)
    (.exit (Gdx/app)))
  (try 
    (.parentRender this)
    (catch Exception e
      (.printStackTrace e)
      (.setScreen this screens/paused))))

(defn -create [^Game this]
  (reset! gamestate/game this)
  (gamestate/load-resources)
  (.setScreen this screens/paused))
