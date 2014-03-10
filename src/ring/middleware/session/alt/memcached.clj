(ns ring.middleware.session.alt.memcached
  (:use ring.middleware.session.store)
  (:require 
   [clojurewerkz.spyglass.client :as client])
  (:import [java.util UUID]))

(deftype MemcachedStore [client]
  SessionStore
  (read-session [_ key]
    (try
      (or (client/get client key) {})
      (catch Exception e {})))
  (write-session [_ key data]
    (let [key (or key (str (UUID/randomUUID)))]
      (client/set client key (* 3600 24 7) data)
      key))
  (delete-session [_ key]
    (if-not (client/delete client key) key)))

(defn mem-store
  [servers & opts]
  (let [cli (client/bin-connection servers)]
    (MemcachedStore. cli)))

