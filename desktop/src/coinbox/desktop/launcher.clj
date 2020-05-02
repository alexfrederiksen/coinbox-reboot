(ns coinbox.desktop.launcher
  (:require [coinbox.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication LwjglApplicationConfiguration]
           [org.lwjgl.input Keyboard])
  (:gen-class))


(defn -main []
  (let [config (LwjglApplicationConfiguration.)]
    ;; to stop Gdx from returning -1 on close
    (set! (.forceExit config) false)
    (set! (.title config) "Coinbox Hero Rebooted")

    (LwjglApplication. (coinbox.core.Game.) config)
    (Keyboard/enableRepeatEvents true)))
