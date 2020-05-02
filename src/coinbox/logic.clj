(ns coinbox.logic
  (:require [clojure.pprint :refer [pprint]] 
            [coinbox.event :refer [event]]
            [coinbox.input :refer [input-system]]
            [coinbox.render :refer [render-system]]
            [coinbox.system :as sys]
            [coinbox.utils :as utils]))

;; example player
(def player {:pos {:x 0 :y 0}
             :vel {:velx 0 :vely 0}
             :physics {}
             :sprite {}

             :movecontroller {:move-right (event)
                              :move-left  (event)
                              :move-up    (event)}

             :inputlistener {:target :movecontroller
                             :keys {:s :move-right
                                    :a :move-left
                                    :w :move-up}}})


(def testplayer {:sprite {}
                 :inputlistener {}})



(def init-events {:collide (event)
                  :keydown (event)})

(def init-systems [(input-system)
                   (render-system)])

(def init-entities [testplayer])





(defn to-map
  "Index vector with its positional indices"
  [v]
  (zipmap (range) v))

(def init-state {:events init-events
                 :systems (to-map init-systems)
                 :entities (to-map init-entities)})

(defn flip
  "Flip first and second argument of function"
  [f]
  (fn [a b & args]
    (apply f b a args)))

(defn update-all
  "Applies f to each x in xs, passing state at key k through each"
  [m k f xs & args]
  (update m k (fn [m-k] (reduce (fn [mm x] (apply f x mm args)) m-k (vals xs)))))

(defn update-each
  "Updates each key under key k with f"
  [m k f & args]
  (update m k (fn [m-k] (utils/map-kv m-k #(apply f % args)))))


(defn tick-system 
  [state system-key]
  (let [system (get-in state [:systems system-key])]
    ;; inject system into entity pool as :this (madness, but very nice in system POV)
    (utils/inject state (flip sys/tick) [:entities] [:systems system-key] :this system (:events state))))

(defn setup
  [state]
  ;; precompute entity lookup by components
  (let [entities-by-component (sys/by-component (:entities state))]
    (-> state
        ;; hookup each system to events
        (update-all :events sys/hookup (:systems state))
        ;; enroll entities to each system
        (update-each :systems sys/enroll entities-by-component))))

(defn tick
  [state]
  ;; tick each system to update entities
  (reduce tick-system state (-> (:systems state) (keys))))

(-> (setup init-state)
    (tick)
    (pprint))
