(ns coinbox.utils
  (:refer-clojure :exclude [keys]))

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

(defn grow
  "Adds a value to each collections, and starts one if emtpy"
  [colls v]
  (if (empty? colls)
    (list (list v))
    (map #(conj % v) colls)))

(defn keys-in
  "Recursively find all key sequences"
  ([m]
  (if (associative? m)
    (->> (map (fn [k] 
                ;; for each key, grow a path
                (grow (keys-in (get m k)) k)) 
           (keys m))
         (apply concat))
    (list)))
  ([m & ks]
   (as-> (keys-in (get-in m ks)) $
         (reduce grow $ (reverse ks)))))

;; (keys-in {:actors {:p1 0 :p2 1 :p3 [0 1 2 3]} :nopes 2})

(defn map-kv
  "Maps a function over the values of a map"
  [m f]
  (reduce-kv (fn [m k v] (assoc m k (f v))) m m))
