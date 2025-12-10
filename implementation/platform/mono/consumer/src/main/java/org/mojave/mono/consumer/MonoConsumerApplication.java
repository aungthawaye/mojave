package org.mojave.mono.consumer;

import org.mojave.core.accounting.domain.AccountingFlyway;
import org.mojave.core.transaction.domain.TransactionFlyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CountDownLatch;

@Import(
    value = {
        MonoConsumerConfiguration.class,
        MonoConsumerDependencies.class,
        MonoConsumerSettings.class})
public class MonoConsumerApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(MonoConsumerApplication.class);

    public static void main(String[] args) throws InterruptedException {

        TransactionFlyway.migrate(
            System.getenv("MONO_FLYWAY_DB_URL"),
            System.getenv("MONO_FLYWAY_DB_USER"), System.getenv("MONO_FLYWAY_DB_PASSWORD"));

        AccountingFlyway.migrate(
            System.getenv("MONO_FLYWAY_DB_URL"),
            System.getenv("MONO_FLYWAY_DB_USER"), System.getenv("MONO_FLYWAY_DB_PASSWORD"));

        var context = new AnnotationConfigApplicationContext(MonoConsumerApplication.class);
        var latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            try {

                LOGGER.info("Shutdown signal received. Stopping consumers...");
                context.close();
                LOGGER.info("Spring context closed. Consumers stopped cleanly.");

            } catch (Exception e) {
                LOGGER.error("Error during shutdown", e);
            } finally {
                latch.countDown();
            }

        }));

        latch.await();
    }

}
