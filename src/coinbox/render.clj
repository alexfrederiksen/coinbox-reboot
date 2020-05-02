(ns coinbox.render
  (:require [coinbox.system :as sys]))

(defn render-entity
  [entity]
  (println (str "rendering " entity)))

(def prereqs [:sprite])

(defrecord RenderSystem
  [ids]
  sys/System

  (hookup [this events] events)
  (enroll [this entities-by-component]
    (assoc this :ids 
           (sys/filter-elgible entities-by-component prereqs)))
  (tick [this state events]
    ;; render entities
    (-> (map render-entity (vals (select-keys state ids))) 
        (doall))
    state))

(defn render-system
  [] (RenderSystem. []))
