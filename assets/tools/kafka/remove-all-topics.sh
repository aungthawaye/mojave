kafka-topics.sh --bootstrap-server localhost:9092 --list \
  | grep -v '^__' \
  | xargs -r -I{} kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic {}
