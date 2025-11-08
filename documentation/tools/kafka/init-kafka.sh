#!/bin/bash
KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"
./bin/kafka-storage.sh format --standalone -t $KAFKA_CLUSTER_ID -c config/server.properties