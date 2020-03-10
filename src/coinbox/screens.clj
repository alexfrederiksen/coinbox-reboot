(ns coinbox.screens
  (:require [coinbox.gamestate :as gamestate :refer [resources]]
            [coinbox.player :as player])
  (:import [com.badlogic.gdx Game Gdx Input Input$Keys Graphics Screen]
           [com.badlogic.gdx.graphics Color GL20]
           [com.badlogic.gdx.graphics.g2d SpriteBatch BitmapFont]
           [com.badlogic.gdx.scenes.scene2d Stage]
           [com.badlogic.gdx.scenes.scene2d.ui Label Label$LabelStyle]))

(declare main)
(declare paused)

(defn screen! [s] 
  (.postRunnable (Gdx/app)
                #(.setScreen @gamestate/game s)))

(def main
  (let [stage (atom nil)
        batch (atom nil)]
    (proxy [Screen] []
      (show []
        (reset! gamestate/state {:player {:x 0 :y 0}})
        ;; setup stage
        (reset! stage (Stage.))
        ;; setup sprite batch
        (reset! batch (SpriteBatch.))
        ;; add label
        (let [style (Label$LabelStyle. (BitmapFont.) (Color. 1 1 1 1))
              label (Label. "MAIN SCREEN" style)]
          (.addActor @stage label)))

      (render [delta]
        (.glClearColor (Gdx/gl) 0 0 0 0)
        (.glClear (Gdx/gl) GL20/GL_COLOR_BUFFER_BIT)
        (when (.isKeyJustPressed Gdx/input Input$Keys/P)
          ;; pause screen
          (screen! paused))
        (when (.isKeyJustPressed Gdx/input Input$Keys/R)
          ;; reload resources
          (gamestate/load-resources))

        ;; update game
        (->> @gamestate/state
             (player/act)
             (reset! gamestate/state))

        ;; start rendering
        (.begin @batch)

        (.draw @batch (:polka @resources) 
               (float (-> @gamestate/state (:player) (:x))) 
               (float (-> @gamestate/state (:player) (:y)))
               (float 100) 
               (float 100))

        ;; end rendering
        (.end @batch)

        ;; render stage
        (doto @stage
          (.act delta)
          (.draw)))

      (dispose [])
      (hide [])
      (pause [])
      (resize [w h])
      (resume []))))

(def paused
  (let [stage (atom nil)]
    (proxy [Screen] []
      (show []
        (reset! stage (Stage.))
        (let [style (Label$LabelStyle. (BitmapFont.) (Color. 1 1 1 1))
              label (Label. "PAUSE" style)]
          (.addActor @stage label)))
      (render [delta]
        (.glClearColor (Gdx/gl) 0 0 0 0)
        (.glClear (Gdx/gl) GL20/GL_COLOR_BUFFER_BIT)
        (when (.isKeyJustPressed Gdx/input Input$Keys/P)
          (screen! main))
        (doto @stage
          (.act delta)
          (.draw)))
      (dispose [])
      (hide [])
      (pause [])
      (resize [w h])
      (resume []))))



