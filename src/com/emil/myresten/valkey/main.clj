(ns com.emil.myresten.valkey.main
  (:import redis.clients.jedis.Jedis))


(defn create-client
  [{host :host
    port :port}]
  (Jedis. host port))

(defn valkey:setex
  [{client :client
    key    :key
    val    :val
    ttl    :ttl}]
  (.setex client (.getBytes key) ttl (.getBytes val)))

(defn valkey:get
  [{client :client
    key    :key}]
  (.get client key))


(comment
  (def client (create-client {:host "127.0.0.1"
                              :port 6379}))
  (.isConnected client)


  (def some-key "some-type:some-identifier:some-field")
  (def res (valkey:setex {:client client
                          :key    some-key
                          :val    "some-val"
                          :ttl    10}))

  (valkey:get {:client client
               :key    some-key})

  )