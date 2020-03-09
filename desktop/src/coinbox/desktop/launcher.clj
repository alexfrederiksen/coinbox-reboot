(ns coinbox.desktop.launcher
  (:require [coinbox.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main []
  (LwjglApplication. (coinbox.core.Game.) "demo" 800 600)
  (Keyboard/enableRepeatEvents true))
