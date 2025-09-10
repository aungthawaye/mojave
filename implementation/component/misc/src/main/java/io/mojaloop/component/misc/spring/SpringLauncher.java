package io.mojaloop.component.misc.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.atomic.AtomicReference;

public class SpringLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringLauncher.class);

    private static final AtomicReference<ConfigurableApplicationContext> CTX = new AtomicReference<>();

    public static void launch(Launching launching, String[] args) {

        if (CTX.compareAndSet(null, launching.launch(args))) {

            LOGGER.info("Spring context launched for the first time.");
            CTX.get().registerShutdownHook();
            LOGGER.info("Spring context launched.");

        } else {

            LOGGER.info("Spring context already launched. Re-launching again.");
            CTX.get().close();
            CTX.set(launching.launch(args));
            CTX.get().registerShutdownHook();
            LOGGER.info("Spring context re-launched.");
        }
    }

    @FunctionalInterface
    public interface Launching {

        ConfigurableApplicationContext launch(String[] args);

    }

}
