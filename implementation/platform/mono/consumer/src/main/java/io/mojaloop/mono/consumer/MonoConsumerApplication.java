package io.mojaloop.mono.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CountDownLatch;

@Import(value = {MonoConsumerConfiguration.class, MonoConsumerSettings.class})
public class MonoConsumerApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(MonoConsumerApplication.class);

    public static void main(String[] args) throws InterruptedException {

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
