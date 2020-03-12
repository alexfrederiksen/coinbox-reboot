(ns coinbox.animation)

(defn run
  [this delta]
  (update this :elapsed (partial + delta)))

(defn ping-pong
  [this]
  ;; (elapsed % (2 * frame-dur))
  (as-> this $
    (/ (:elapsed $) (:frame-dur $))
    (int $)
    (mod $ (dec (* 2 (count (:frames this)))))
    (- $ (dec (count (:frames this))))
    (Math/abs $)
    (nth (:frames this) $)))

(defn looping
  [this]
  (as-> this $
    (/ (:elapsed $) (:frame-dur $))
    (int $)
    (mod $ (count (:frames this)))
    (nth (:frames this) $)))

(defn frame
  [this]
  ((:mode this) this))

(defn animation
  ([frames dur mode]
  {:frames frames
   :dur dur
   :frame-dur (/ dur (count frames))
   :mode mode
   :elapsed 0.0})
  ([frames dur]
   (animation frames dur looping)))


