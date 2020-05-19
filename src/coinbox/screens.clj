(ns coinbox.screens
  (:require [coinbox.gamestate :as gs]
            [coinbox.utils :as utils]
            [coinbox.logic :as logic]
            [coinbox.renderer :as r])
  (:import [com.badlogic.gdx Game Gdx Input Input$Keys Graphics Screen]
           [com.badlogic.gdx.graphics Color GL20]
           [com.badlogic.gdx.graphics.g2d SpriteBatch BitmapFont]
           [com.badlogic.gdx.scenes.scene2d Stage]
           [com.badlogic.gdx.scenes.scene2d.ui Label Label$LabelStyle]))

(declare main)
(declare paused)

(defn screen! [s] 
  (.postRunnable (Gdx/app)
                #(.setScreen @gs/game s)))

(defn run-state
  "Runs the state and returns the new state"
  [m s]
  (second (m s)))

(def main
  (let [stage (atom nil)
        batch (atom nil)
        fps-label (atom nil)]
    (proxy [Screen] []
      ;;; on switch to this screen
      (show []
        ;; setup stage
        (reset! stage (Stage.))
        ;; setup sprite batch
        (reset! batch (SpriteBatch.))

        ;; init logical state
        (reset! gs/state (run-state (logic/init-state) {}))

        ;; add label
        (let [style (Label$LabelStyle. (BitmapFont.) (Color. 1 1 1 1))
              label (Label. "MAIN SCREEN" style)]
          (.addActor @stage label)
          (reset! fps-label label)))

      ;;; update and render to screen
      (render [delta]

        ;; clear the screen with black
        (.glClearColor Gdx/gl 0 0 0 0)
        (.glClear Gdx/gl GL20/GL_COLOR_BUFFER_BIT)

        (when (.isKeyJustPressed Gdx/input Input$Keys/P)
          ;; pause screen
          (screen! paused))
        (when (.isKeyJustPressed Gdx/input Input$Keys/R)
          ;; reload resources
          (gs/load-resources))

        ;; start rendering
        (.begin @batch)

        ;; tick logic
        (let [new-state (run-state (logic/tick delta) @gs/state)] 
          ;; render
          ((r/render @batch) new-state)
          ;; update gamestate 
          (reset! gs/state new-state))

        ;; end rendering
        (.end @batch)

        ;; update fps label
        (.setText @fps-label (str (->> (/ 1.0 delta) 
                                       (format "%.2f")) " fps"))

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
        (println "Paused")
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



