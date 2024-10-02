zookeeper-server-start /opt/homebrew/etc/kafka/zookeeper.properties


kafka-server-start /opt/homebrew/etc/kafka/server.properties


kafka-console-producer --topic test-topic --bootstrap-server localhost:9092
