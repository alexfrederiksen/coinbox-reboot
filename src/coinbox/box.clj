(ns coinbox.box
  (:require [coinbox.physics :as physics]
            [coinbox.gamestate :as gamestate :refer [resources]]
            [coinbox.utils :as utils]
            [coinbox.coin :as coin]))


(defn pushed?
  [this]
  (-> (and (zero? (:pushx this))
           (zero? (:pushy this)))
      (not)))

(defn spawn-radius
  [body]
  ;; did I just use triangle inequality instead of a square root?,
  ;; Yea. I did.
  (-> (+ (:w body) (:h body))
      (/ 2)))

(defn launch-coin
  [game x y vx vy]
  (as-> (coin/coin x y) coin
        (assoc-in coin [:body :velx] vx)
        (assoc-in coin [:body :vely] vy)
        (update-in game [:actors :coins] #(vec (conj % coin)))))

(defn rotate-vec
  [x y t]
  ;; [cos t -sin t] [x]
  ;; [sin t  cos t] [y]
  {:x (- (* x (Math/cos t)) (* y (Math/sin t)))
   :y (+ (* x (Math/sin t)) (* y (Math/cos t)))})

(def launch-noise 0.3)

(defn centered-rand
  [x]
  (- (rand x) (/ x 2)))

(defn launch-coins
  [game dx dy frce]
  (let [box (-> game :actors :box :body)
        rad (spawn-radius box)
        noise (centered-rand launch-noise)
        nv (rotate-vec dx dy noise)
        dx (:x nv)
        dy (:y nv)
        coin-x (+ (:x box) (* dx rad) (/ (:w box) 2))
        coin-y (+ (:y box) (* dy rad) (/ (:h box) 2))
        coin-vx (* dx frce)
        coin-vy (* dy frce)]
    (launch-coin game 
                 coin-x coin-y 
                 coin-vx coin-vy)))


(defn bop
  [game dx dy frce]
  (as-> (-> game :actors :box) $
        ;; update box
        (-> $
            (update :pushx #(+ % (* dx 20)))
            (update :pushy #(+ % (* dy 20))))
        ;; update box instance in game
        (assoc-in game [:actors :box] $)
        ;; spawn coins
        (launch-coins $ dx dy frce)))

(defn frame
  [this]
  (cond 
    (pushed? this) (:box-spook @resources)
    :else (:box @resources)))

(def push-decay 0.5)

(defn decay
  [x t]
  (let [xx (* x t)]
    (if (< xx 0.001)
      0
      xx)))

(defrecord Box
  [body pushx pushy]
  gamestate/Actor
  (act [this game]
    (-> this
        ;; reset push
        (update :pushx #(decay % push-decay))
        (update :pushy #(decay % push-decay))))
  (act-globally [this game] game)
  (draw [this game batch]
    (utils/reset-color batch)
    (.draw batch (frame this) 
           (float (+ pushx (:x body))) 
           (float (+ pushy (:y body))) 
           (float (:w body)) 
           (float (:h body)))))

(defn box
  [x y]
  (map->Box {:body {:x x :y y :w 50 :h 50}
             :pushx 0
             :pushy 0}))
