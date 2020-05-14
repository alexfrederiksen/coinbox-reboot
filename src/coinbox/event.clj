(ns coinbox.event)

(defn event [] {:subscribers {} :i 0})

(defn invoke
  [event state & args]
  (reduce (fn [state f] (apply f state args)) state (-> (:subscribers event) 
                                                        (vals))))

(defn subscribe*
  ([event callback]
  (let [id (:i event)]
    (-> event
        ;; increment id counter
        (update :i inc)
        ;; assoc new subscriber
        (assoc-in [:subscribers id] callback)
        ;; return new event and id
        (vector id)))))

(defn subscribe
  [event callback]
  (let [[e id] (subscribe* event callback)]
    e))

(defn unsubscribe
  [event id]
  (update event :subscribers #(dissoc % id)))
