(ns coinbox.physics
  (:import [com.badlogic.gdx Gdx]))

(def gravity 10000.0)

(defn screen-width
  []
  (.getWidth Gdx/graphics))

(defn bounce
  [v body]
  (-> (* v (:bounce body))
      (-)))

(defn muladd
  [body k & factors]
  (assoc body k (+ (k body) (apply * factors))))

(defn collide-low
  [body x v low]
  (cond-> body (< (x body) low)
    ;; fix x
    (assoc x low
           ;; bounce velocity
           v (bounce (v body) body))))

(defn collide-high
  [body x v high]
  (cond-> body (> (x body) high)
    ;; fix x
    (assoc x high
           ;; bounce velocity
           v (bounce (v body) body))))


(defn collide-left-wall
  [body]
  (collide-low body :x :velx 0))

(defn collide-right-wall
  [body]
  (collide-high body :x :velx (- (screen-width) (:w body))))

(defn collide-ground
  [body]
  (collide-low body :y :vely 0))

(defn wall-collide
  [body]
  (-> body, 
      (collide-left-wall)
      (collide-right-wall)
      (collide-ground)))

(defn box-collide-left
  "Collide with left side of a box"
  [body box]
  (collide-high body :x :velx (- (:x box) (:w body))))

(defn box-collide-right
  "Collide with right side of a box"
  [body box]
  (collide-low body :x :velx (+ (:x box) (:w box))))

(defn box-collide-top
  "Collide with top side of a box"
  [body box]
  (collide-low body :y :vely (+ (:y box) (:h box))))

(defn box-collide-bottom
  "Collide with bottom side of a box"
  [body box]
  (-> body
      (collide-high :y :vely (- (:y box) (:h body))) 
      (assoc :bopped? true)))

(defn left
  [box]
  (:x box))

(defn right
  [box]
  (+ (:x box) (:w box)))

(defn top
  [box]
  (+ (:y box) (:h box)))

(defn bottom
  [box]
  (:y box))

(defn box-intersect?
  [b1 b2]
  (not (or (<= (right b1) (left b2))
           (>= (left b1) (right b2))
           (<= (top b1) (bottom b2))
           (>= (bottom b1) (top b2)))))

(defn box-collide
  [body box vel]
  (cond-> body
    ;; check for box intersection
    (box-intersect? body box) 
    ;; if so, handle it
    (cond-> 
      (pos? (:x vel)) (box-collide-left box)
      (neg? (:x vel)) (box-collide-right box)
      (pos? (:y vel)) (box-collide-bottom box)
      (neg? (:y vel)) (box-collide-top box))))

(defn move
  [this game]
  (as-> this body
    ;; reset bopped condition
    (assoc body :bopped? false)
    ;; update position on x-axis
    (muladd body :x (:velx body) (:deltatime game))
    ;; check for x-collisions
    (box-collide body (-> game :actors :box :body) {:x (:velx body) :y 0})

    ;; update position on y-axis
    (muladd body :y (:vely body) (:deltatime game))
    ;; check for y-collisions
    (box-collide body (-> game :actors :box :body) {:x 0 :y (:vely body)})

    ;; check for wall collision
    (wall-collide body)

    ;; apply gravity
    (muladd body :vely -1 gravity (:deltatime game))
   ))

(defn body
  [x y w h]
  {:x x
   :y y
   :w w
   :h h
   :velx 0
   :vely 0
   :bounce 0
   })
