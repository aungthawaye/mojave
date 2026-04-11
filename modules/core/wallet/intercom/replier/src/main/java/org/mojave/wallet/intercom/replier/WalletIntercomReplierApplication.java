package org.mojave.wallet.intercom.replier;

import org.mojave.wallet.domain.WalletFlyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CountDownLatch;

@Import(
    value = {
        WalletIntercomReplierConfiguration.class,
        WalletIntercomReplierDependencies.class,
        WalletIntercomReplierSettings.class})
public class WalletIntercomReplierApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        WalletIntercomReplierApplication.class);

    static void main(final String[] args) throws InterruptedException {

        WalletFlyway.migrate(
            System.getenv("FLYWAY_DB_URL"), System.getenv("FLYWAY_DB_USER"),
            System.getenv("FLYWAY_DB_PASSWORD"));

        final var context = new AnnotationConfigApplicationContext(
            WalletIntercomReplierApplication.class);

        final var latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            try {

                LOGGER.info("Shutdown signal received. Stopping repliers...");
                context.close();
                LOGGER.info("Spring context closed. Repliers stopped cleanly.");

            } catch (final Exception exception) {

                LOGGER.error("Error during shutdown", exception);

            } finally {

                latch.countDown();
            }

        }));

        latch.await();
    }

}
