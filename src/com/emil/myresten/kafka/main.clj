(ns com.emil.myresten.kafka.main
  (:import org.apache.kafka.clients.consumer.KafkaConsumer
           [org.apache.kafka.common.serialization StringDeserializer]))



(def config {"bootstrap.servers" "localhost:8081"
             "group.id" "experiments-group"
             "key.deserializer" StringDeserializer
             "value.deserializer" StringDeserializer
             "allow.auto.create.topics" false
             "enable.auto.commit" false
             "security.protocol" "PLAINTEXT"})


(defn create-consumer
  [config]
  (KafkaConsumer. config))

(def consumer (create-consumer config))
