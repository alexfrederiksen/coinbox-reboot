(ns coinbox.utils
  (:refer-clojure :exclude [keys]))

;; ### Old functions for mixing maps and vectors ###############################

(defprotocol Keyable
  (keys [this]))

(extend-protocol Keyable
  clojure.lang.IPersistentVector
  (keys [this] (range (count this)))

  clojure.lang.IPersistentMap
  (keys [this] (clojure.core/keys this))
  
  java.lang.Object
  (keys [this] (list)))

(defprotocol Doable
  (do-in [this f] "Applies f to all non-associables/non-records in nested structure"))

(extend-protocol Doable
  java.lang.Object
  (do-in [this f] (f this))

  clojure.lang.IPersistentVector
  (do-in [this f] (doseq [o this] (do-in o f)))
  
  clojure.lang.IPersistentMap
  (do-in [this f] (if (record? this) 
                    (f this) 
                    (doseq [k (keys this)] (do-in (k this) f)))))

;; ### Quality of life functions ###############################################

(defn map-kv
  "Maps a function over the values of a map"
  [m f]
  (reduce-kv (fn [m k v] (assoc m k (f v))) m m))

(defn reset-color
  "Resets the batch to the default white color"
  [batch]
  (.setColor batch 
             (float 1) (float 1) 
             (float 1) (float 1)))



;; ### Map->vector inverting ###################################################

(defn conj-mapvec
  [m e c]
  (update m c #(conj % e)))

(defn conj-mapvec*
  [m e cs]
  (reduce (fn [m c]
            (conj-mapvec m e c)) m cs))

(defn blank-vals
  [m]
  (zipmap (apply concat (vals m)) (repeat [])))

(defn invert-mapvec
  "Reverse a mapping A -> [B] to mapping B -> [A]"
  [m]
  (reduce-kv conj-mapvec*
             (blank-vals m) m))


;; ### Injections! #############################################################

(defn inject
  "Injects a key into parameter"
  [m f base-keys inj-keys inj-name & args]

  ;; build injected map
  (let [injected (-> (get-in m base-keys)
                     (assoc inj-name (get-in m inj-keys)))

        ;; apply function
        out (apply f injected args)

        ;; parse altered output
        out-inj (get out inj-name)
        out-base (dissoc out inj-name)]

    ;; update original structure
    (-> m
        (assoc-in base-keys out-base)
        (assoc-in inj-keys out-inj))))

