(ns coinbox.lens
  (:require [clojure.pprint])
  (:require [clojure.core :as core])
  (:refer-clojure :exclude [assoc assoc-in update update-in]))

(defn fill-in
  [lens args]
  (let [;; filter all :arg keys in the lens
        arg-keys (filter #(= (get lens %) :arg) (keys lens))
        ;; and pair with args
        arg-map (zipmap arg-keys args) ]
    ;; fill in args in map return [filled rest-of-args]
    (vector (merge lens arg-map) (nthrest args (count arg-keys)))))

(defn zoom
  "Attaches lens data to map (adds :lens)"
  [state raw-lens args]
  (let [[lens rest-args] (fill-in raw-lens args)]
    (core/assoc state :lens lens)))

(defn zoom*
  "Attaches lens data to map (adds :lens)"
  [state filled-lens]
  (core/assoc state :lens filled-lens))


(defn unzoom
  "Unattaches lens data from map (pops to old :lens value)"
  [state old-lens]
  (core/assoc state :lens old-lens))

(defmacro with-zoom
  "Zoom/unzoom automatically with the given lens"
  [state lens args & body]
  `(let [old-lens# (get ~state :lens)
         ~state (zoom ~state ~lens ~args)]
     (as-> ~state ~state
       ~@body
       (unzoom ~state old-lens#))))

(defmacro defn-zoomed
  "Attatch a lens to function and zooms/unzooms automatically"
  [fn-name lens fn-args & fn-body]
  ;; get state symbol 
  (let [state (first fn-args)]
    ;; create a function in terms of state and args
    `(def ~fn-name 
       (fn [state# & args#]
         ;; fill lens and zoom
         (let [[filled-lens# rest-args#] (fill-in ~lens args#)
                old-lens# (get state# :lens)
                zoomed-state# (zoom* state# filled-lens#)]

           ;; bind rest of the args to the fn bindings (ignoring first state arg)
           (apply (fn ~fn-args
                    ;; use as-> macro and unzoom when finished
                    (as-> zoomed-state# ~state
                      ~@fn-body
                      (unzoom ~state old-lens#))) nil rest-args#))))))

(defn assoc
  "Like cloure.core/assoc, but k can be a zoomed key"
  [m k v]
  (let [lens (get m :lens)
        abs-key (get lens k)]
    (core/assoc-in m abs-key v)))

(defn update
  "Like cloure.core/update, but k can be a zoomed key"
  [m k f]
  (let [lens (get m :lens)
        abs-key (get lens k)]
    (core/update-in m abs-key f)))

(defn assoc-in
  "Like clojure.core/assoc-in, but key k can be a zoomed key"
  [m [k & ks] v]
  (update m k (fn [sub] (core/assoc-in sub ks v))))

(defn update-in
  "Like clojure.core/update-in, but key k can be a zoomed key"
  [m [k & ks] f]
  (update m k (fn [sub] (core/update-in sub ks f))))

(def testlens {:this :arg
   :systems [:systems]})

(def teststate {:actors {0 {:me 0 :teeth 10}
                         1 :you}
                :systems {:runner :irunstuff}})

(-> (macroexpand-1 '(defn-zoomed say-stuff
  {:this :arg
   :systems [:systems]}
  [state msg]
  (assoc state :this :notme)))
    (clojure.pprint/pprint))

(defn-zoomed say-stuff
  {:this :arg
   :systems [:systems]}
  [state msg]
  (do (println state) state)
  (do (println msg) state)

  (assoc-in state [:this :me] 10))

(say-stuff teststate [:actors 0] "yay!!")

