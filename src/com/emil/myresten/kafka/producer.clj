(ns com.emil.myresten.kafka.producer
  (:import org.apache.kafka.clients.producer.KafkaProducer
           (java.util UUID)
           (org.apache.kafka.clients.producer ProducerRecord)
           (org.apache.kafka.common.serialization StringSerializer))
  (:require [com.emil.myresten.kafka.consumer :as c]))



(def config {"key.serializer"    StringSerializer
             "value.serializer"  StringSerializer
             "bootstrap.servers" c/bootstrap-server})


(defn create-producer
  [cfg]
  (KafkaProducer. cfg))


(defn send-message
  [{producer :producer
    message  :message
    topic    :topic}]
  (.send producer (ProducerRecord. topic (.toString UUID/randomUUID) message) nil))