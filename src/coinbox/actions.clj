(ns coinbox.actions)

(defmacro act-on
  [this game & args]
  `(some-> (vector ~this ~game) ~@args))

(defn <--
  [state ks v]
  (assoc-in (cons 0 state) ks v))

(defn <<<
  [state ks v]
  (assoc-in (cons 1 state) ks v))

(defn <-
  [state ks f]
  (update-in (cons 0 state) ks f))

(defn <<
  [state ks f]
  (update-in (cons 1 state) ks f))

(defn ?--
  [state expr ks v]
  (cond expr (<-- state ks v)
        :else state))
(defn ?<<
  [state expr ks v]
  (cond expr (<<< state ks v)
        :else state))

(defn ?-
  [state expr ks f]
  (cond expr (<- state ks f)
        :else state))

(defn ?<
  [state expr ks f]
  (cond expr (<< state ks f)
        :else state))


