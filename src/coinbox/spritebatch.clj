(ns coinbox.spritebatch)

(defn draw
  "Draw a sprite to the screen with given batch"
  [batch tex x y w h]
  (.draw batch tex
         (float x) (float y)
         (float w) (float h)))

(defn draw*
  "Draw a sprite to the screen with given batch"
  [batch tex x y w h {:keys [flip-x? flip-y?] :or {flip-x? false
                                                   flip-y? false}}]
  (.draw batch tex
         (float x) (float y)
         (float w) (float h)))

