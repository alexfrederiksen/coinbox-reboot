(ns coinbox.system
  (:require [coinbox.utils :as utils]))

(ns-unmap *ns* 'System)

(defprotocol System
  (hookup [this events] "Subscribe to events (return events)")
  (enroll [this entities-by-component] "Enroll components (return self)")
  (tick [this state events] "Manage entities (state contains entities and :this) (return state"))

(defn filter-elgible
  [entities-by-component prereqs]
  (apply clojure.set/intersection 
    (map entities-by-component prereqs)))

(defn by-component
  [entities]
  (-> (utils/map-kv entities keys)
      (utils/invert-mapvec)
      (utils/map-kv set)))


