/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.core.accounting.intercom.replier;

import org.mojave.core.accounting.domain.AccountingFlyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CountDownLatch;

@Import(
    value = {
        AccountingIntercomReplierConfiguration.class,
        AccountingIntercomReplierDependencies.class,
        AccountingIntercomReplierSettings.class})
public class AccountingIntercomReplierApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        AccountingIntercomReplierApplication.class);

    static void main(final String[] args) throws InterruptedException {

        AccountingFlyway.migrate(
            System.getenv("FLYWAY_DB_URL"), System.getenv("FLYWAY_DB_USER"),
            System.getenv("FLYWAY_DB_PASSWORD"));

        final var context = new AnnotationConfigApplicationContext(
            AccountingIntercomReplierApplication.class);
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
