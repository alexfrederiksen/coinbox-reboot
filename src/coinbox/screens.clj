(ns coinbox.screens
  (:require [coinbox.gamestate :as gamestate :refer [resources]]
            [coinbox.player :as player]
            [coinbox.utils :as utils])
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

(defn update-actors
  "Recursively updates all actors in any nested structure"
  [state ks]
  (reduce (fn [lstate k] 
            (let [new-ks (conj (vec ks) k)
                  obj (get-in lstate new-ks)]
              (-> (if (satisfies? gamestate/Actor obj)

                    ;; update actor
                    (as-> obj $
                      (gamestate/act $ lstate)
                      (if (nil? $)
                        (update-in lstate ks #(dissoc % k))
                        (assoc-in lstate new-ks $)))

                    ;; otherwise recurse this structure
                    (if (associative? obj) 
                      (update-actors lstate new-ks)
                      lstate))))) 

          ;; feed state
          state 
          ;; feed keys
          (-> (get-in state ks) (utils/keys))))

(def main
  (let [stage (atom nil)
        batch (atom nil)]
    (proxy [Screen] []
      ;;; on switch to this screen
      (show []
        (reset! gamestate/state {:actors {:player (player/player)}})
        ;; setup stage
        (reset! stage (Stage.))
        ;; setup sprite batch
        (reset! batch (SpriteBatch.))
        ;; add label
        (let [style (Label$LabelStyle. (BitmapFont.) (Color. 1 1 1 1))
              label (Label. "MAIN SCREEN" style)]
          (.addActor @stage label)))

      ;;; update and render to screen
      (render [delta]

        ;; clear the screen with black
        (.glClearColor (Gdx/gl) 0 0 0 0)
        (.glClear (Gdx/gl) GL20/GL_COLOR_BUFFER_BIT)

        (when (.isKeyJustPressed Gdx/input Input$Keys/P)
          ;; pause screen
          (screen! paused))
        (when (.isKeyJustPressed Gdx/input Input$Keys/R)
          ;; reload resources
          (gamestate/load-resources))

        ;; update game
        (as-> @gamestate/state state

          ;; update delta time
          (assoc state :deltatime delta)

          ;; update actors
          (update-actors state [:actors])

          ;; update gamestate
          (reset! gamestate/state state))

        ;; start rendering
        (.begin @batch)

        ;; draw actors
        (utils/do-in (:actors @gamestate/state) #(gamestate/draw % @gamestate/state @batch))

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



