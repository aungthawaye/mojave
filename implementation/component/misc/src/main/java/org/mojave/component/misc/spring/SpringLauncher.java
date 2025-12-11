/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.mojave.component.misc.spring;

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
