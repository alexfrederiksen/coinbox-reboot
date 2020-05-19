(ns coinbox.vec)

(defn add
  [[x1 y1] [x2 y2]]
  (vector (+ x1 x2) (+ y1 y2)))

(defn scl
  [[x y] c]
  (vector (* x c) (* y c)))

(defn set-x
  [[x y] x1]
  (vector x1 y))

(defn set-y
  [[x y] y1]
  (vector x y1))


