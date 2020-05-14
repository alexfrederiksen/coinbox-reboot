(ns coinbox.system
  (:require [coinbox.utils :as utils]
            [clojure.set :refer [intersection]]))

(ns-unmap *ns* 'System)

(defprotocol System
  (hookup [this this-key state] "Subscribe to events (return events)")
  (enroll [this this-key state entities-by-component] "Enroll components (return self)")
  (tick [this this-key state] "Manage entities (state contains entities and :this) (return state"))

(defn find-component
  [entity c]
  (if (contains? entity c)
    (get entity c) 
    ;; otherwise throw error
    (throw (str "Component \"" c "\" not found in entity"))))

(defn filter-elgible
  [entities-by-component prereqs]
  (apply intersection 
    (map #(get entities-by-component % []) prereqs)))

(defn by-component
  [entities]
  (-> (utils/map-kv entities keys)
      (utils/invert-mapvec)
      (utils/map-kv set)))


