(ns coinbox.logic
  (:require [clojure.pprint :refer [pprint]] 
            [coinbox.event :as event :refer [event]]
            [coinbox.input :refer [input-system]]
            [coinbox.render :as render :refer [render-system]]
            [coinbox.system :as sys]
            [coinbox.utils :as utils]))

(defn hello
  [state]
  (println "HELLOW THERE WHAT IS UP MY DUDE")
  (update-in state [:entities 0 :pos :x] inc))

(defn testplayer [] {:pos {:x 20 :y 0}
                     :size {:w 100 :h 100}
                     :sprite {:texture :polka}
                     :controller {:move-left (event/subscribe (event) hello)}
                     :inputlistener {:target :controller
                                     :keys {:a :move-left}}})


(defn init-events [] {:collide (event)
                      :keydown (event)})

(defn init-systems [] {:input (input-system)
                       :render (render-system)})

(defn init-entities [] [(testplayer)])





(defn to-map
  "Index vector with its positional indices"
  [v]
  (zipmap (range) v))

(defn init-state [] {:events (init-events)
                     :systems (init-systems)
                     :entities (to-map (init-entities))})

(defn call-each
  "Threads state through each value in substate with f
  and passes (obj-value obj-key state & args] to f"
  [state f substate-key & args]
  (reduce-kv (fn [state k v] (apply f v [substate-key k] state args)) 
             state (get state substate-key)))

(defn setup
  [state]
  (println "Setting up..")
  ;; precompute entity lookup by components
  (let [entities-by-component (sys/by-component (:entities state))]
    (as-> state state
        ;; hookup each system to events
        (call-each state sys/hookup :systems)
        ;; enroll entities to each system
        (call-each state sys/enroll :systems entities-by-component))))

(defn tick
  [state]
  ;; tick each system to update entities
  (call-each state sys/tick :systems))

(defn init
  []
  (setup (init-state)))

(defn attach-batch
  [state batch]
  (update-in state [:systems :render] render/attach-batch batch))

