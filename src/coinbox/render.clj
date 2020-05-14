(ns coinbox.render
  (:require [coinbox.system :as sys]
            [coinbox.gamestate :refer [resources]]
            [coinbox.lens :as l]))

;; impure
(defn render-entity
  [entity batch]
  (.draw batch (get @resources (get-in entity [:sprite :texture]))
         (float (get-in entity [:pos :x]))
         (float (get-in entity [:pos :y]))
         (float (get-in entity [:size :w]))
         (float (get-in entity [:size :h]))))

(def prereqs [:sprite :pos :size])

(defrecord RenderSystem
  [ids batch]
  sys/System

  (hookup [this this-key state] 
    state)
  (enroll [this this-key state entities-by-component]
    (l/with-zoom state {:this :arg} [this-key]
      (do (println "Entrolling first..") state)

      ;; enroll 
      (l/assoc-in state [:this :ids] (sys/filter-elgible entities-by-component prereqs))
      (do (println "Enrolling...") state)
      (do (println state) state)))

  (tick [this this-key state]
    (l/with-zoom state {:this :arg} [this-key]
      ;; render entities
      (do 
        ;; call for side-effects
        (mapv #(render-entity % batch) (-> (:entities state) 
                                           (select-keys ids) 
                                           (vals))) 
        state))))

(defn attach-batch
  [this batch]
  (assoc this :batch batch))

(defn render-system
  [] (RenderSystem. [] nil))
