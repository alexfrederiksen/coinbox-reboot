(ns coinbox.event)

(defn event [] {:subscribers {} :i 0})

(defn invoke
  [event & args]
  (-> (map #(apply % args) (-> (:subscribers event)
                               (vals)))
      (doall)))

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
  (let [[e id] (subscribe callback)]
    e))

(defn unsubscribe
  [event id]
  (update event :subscribers #(dissoc % id)))
