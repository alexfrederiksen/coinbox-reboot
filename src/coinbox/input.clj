(ns coinbox.input
  (:require [coinbox.system :as sys]
            [coinbox.event :refer [event invoke]]
            [coinbox.lens :as l])
  (:import [com.badlogic.gdx Gdx Input$Keys]))

(def key->GdxKey {:a Input$Keys/A
                  :b Input$Keys/B
                  :c Input$Keys/C
                  :d Input$Keys/D
                  :e Input$Keys/E
                  :f Input$Keys/F
                  :g Input$Keys/G
                  :h Input$Keys/H
                  :i Input$Keys/I
                  :j Input$Keys/J
                  :k Input$Keys/K
                  :l Input$Keys/L
                  :m Input$Keys/M
                  :n Input$Keys/N
                  :o Input$Keys/O
                  :p Input$Keys/P
                  :q Input$Keys/Q
                  :r Input$Keys/R
                  :s Input$Keys/S
                  :t Input$Keys/T
                  :u Input$Keys/U
                  :v Input$Keys/V
                  :w Input$Keys/W
                  :x Input$Keys/X
                  :y Input$Keys/Y
                  :z Input$Keys/Z
                  :space Input$Keys/SPACE
                  })

(def prereqs [:inputlistener])

(def c-inputlistener
  {:target nil
   :keys {}})

(defn key-down
  "Return true when k is down"
  [k]
  (.isKeyPressed Gdx/input (key->GdxKey k)))

(defn listen-key
  "Invoke event if key is pressed"
  [entity k event-name state]
  (if (key-down k) 
    ;; then
    (let [target (sys/find-component entity (-> entity :inputlistener :target))
          event (target event-name)]
      (invoke event state))
    ;; else
    state))

(defn listen-entity
  "Invoke events for entity"
  [entity state]
  (reduce (fn [state [k event]] (listen-key entity k event state)) 
          state
          (-> entity :inputlistener :keys)))

(defrecord InputSystem
  [ids]
  sys/System

  (hookup [this this-key state] state)
  (enroll [this this-key state entities-by-component] 
    (l/with-zoom state {:this :arg} [this-key]
      (l/assoc-in state [:this :ids] (sys/filter-elgible entities-by-component prereqs))))

  (tick [this this-key state]
    (l/with-zoom state {:this :arg} [this-key]
      ;; listen for entities
      (reduce (fn [state entity] (listen-entity entity state)) 
              state (-> (:entities state) 
                        (select-keys ids) 
                        (vals))) )))

(defn input-system
  [] (InputSystem. []))


;; example player
(def player {:pos {:x 0 :y 0}
             :vel {:velx 0 :vely 0}
             :physics {}

             :movecontroller {:move-right (event)
                              :move-left  (event)
                              :move-up    (event)}

             :inputlistener {:target :movecontroller
                             :keys {:s :move-right
                                    :a :move-left
                                    :w :move-up}}})



