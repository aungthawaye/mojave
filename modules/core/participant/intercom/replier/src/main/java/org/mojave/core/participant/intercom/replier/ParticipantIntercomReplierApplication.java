package org.mojave.core.participant.intercom.replier;

import org.mojave.core.participant.domain.ParticipantFlyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CountDownLatch;

@Import(
    value = {
        ParticipantIntercomReplierConfiguration.class,
        ParticipantIntercomReplierDependencies.class,
        ParticipantIntercomReplierSettings.class})
public class ParticipantIntercomReplierApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ParticipantIntercomReplierApplication.class);

    static void main(final String[] args) throws InterruptedException {

        ParticipantFlyway.migrate(
            System.getenv("FLYWAY_DB_URL"), System.getenv("FLYWAY_DB_USER"),
            System.getenv("FLYWAY_DB_PASSWORD"));

        final var context = new AnnotationConfigApplicationContext(
            ParticipantIntercomReplierApplication.class);

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
