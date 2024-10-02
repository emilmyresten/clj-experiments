(ns com.emil.myresten.kafka.consumer
  (:import org.apache.kafka.clients.consumer.KafkaConsumer
           (java.time Duration)
           (org.apache.kafka.common TopicPartition)
           [org.apache.kafka.common.serialization StringDeserializer]))


(def bootstrap-server "localhost:9092")
(def config {"bootstrap.servers"        bootstrap-server
             "max.poll.interval.ms"     (int 3000)          ;3s for rebalance
             "key.deserializer"         StringDeserializer
             "value.deserializer"       StringDeserializer
             "allow.auto.create.topics" true
             "enable.auto.commit"       false
             "security.protocol"        "PLAINTEXT"})


(defn create-consumer
  [config]
  (KafkaConsumer. config))

(defn subscripe-to-topic
  [{topic    :topic
    consumer :consumer}]
  (.subscribe consumer [topic]))

(defn dopoll
  [{consumer  :consumer
    do-commit :do-commit}]
  (let [consume (atom true)]
    (future
      (while (deref consume)
        (let [records (.poll consumer (Duration/ofMillis 1500))
              num-records (.count records)
              committed-offset (->> (.assignment consumer)
                                    (.committed consumer)
                                    (vals)
                                    (first)
                                    (.offset))]
          (println "Number of ConsumerRecords in poll: " num-records)
          (println "Committed offset: " committed-offset)
          (when (not (zero? num-records))
            (doseq [record records]
              (println "event: " (.value record) " | offset: " (.offset record))))
          (if do-commit
            (.commitSync consumer)
            (do (println "seeking to offset " committed-offset)
              (.seek consumer
                     (TopicPartition. (.topic (first records)) (.partition (first records)))
                     committed-offset))
            ))))
    (fn []
      (println "Stopped consuming. Open poll will complete.")
      (reset! consume false))))

(comment
  (def consumer-1 (create-consumer
                    (merge config {"group.id" "consumer-1"})))

  (subscripe-to-topic {:topic    "test-topic"
                       :consumer consumer-1})

  (def stop-fn-1 (dopoll {:consumer consumer-1}))

  (stop-fn-1)




  (def consumer-2 (create-consumer
                    (merge config {"group.id" "consumer-2"})))

  (subscripe-to-topic {:topic    "test-topic"
                       :consumer consumer-2})

  (def stop-fn-2 (dopoll {:consumer  consumer-2
                          :do-commit true}))

  (stop-fn-2)

  )


