FROM maven:3.9.11-eclipse-temurin-25 AS builder

WORKDIR /workspace

COPY modules ./modules

WORKDIR /workspace/modules

RUN mvn -pl mono/core/consumer -am -DskipTests package

FROM eclipse-temurin:25-jdk

WORKDIR /opt/mojave

COPY --from=builder /workspace/modules/mono/core/consumer/target/classes ./classes
COPY --from=builder /workspace/modules/mono/core/consumer/target/lib ./lib

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -cp '/opt/mojave/classes:/opt/mojave/lib/*' org.mojave.mono.core.consumer.MonoConsumerApplication"]
