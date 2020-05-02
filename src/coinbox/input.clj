(ns coinbox.input
  (:require [coinbox.system :as sys]
            [coinbox.event :refer [event invoke]])
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
  [e k event-name]
  (when (key-down k) 
    (let [target (get e (:target e))]
      ;; check wether target component is actually there
      (when (nil? target) 
        (throw (str "Component \"" (:target e) "\" not found in entity")))

      (let [event (target event-name)]
        (invoke event)))))

(defn listen-entity
  "Invoke events for entity"
  [entity]
  (apply map (fn [[k v]] listen-key entity k v) 
         (:keys entity)))


(defrecord InputSystem
  [ids]
  sys/System

  (hookup [this events] events)
  (enroll [this entities-by-component] 
    (assoc this :ids 
           (sys/filter-elgible entities-by-component prereqs)))

  (tick [this state events]
    ;; listen for entities
    (-> (map listen-entity (vals (select-keys state ids)))
        (doall))
    state))

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



