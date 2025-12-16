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
package org.mojave.core.accounting.consumer;

import org.mojave.core.accounting.domain.AccountingFlyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CountDownLatch;

@Import(
    value = {
        AccountingConsumerConfiguration.class,
        AccountingConsumerDependencies.class,
        AccountingConsumerSettings.class})
public class AccountingConsumerApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(
        AccountingConsumerApplication.class);

    public static void main(String[] args) throws InterruptedException {

        AccountingFlyway.migrate(
            System.getenv("FLYWAY_DB_URL"),
            System.getenv("FLYWAY_DB_USER"), System.getenv("FLYWAY_DB_PASSWORD"));

        var context = new AnnotationConfigApplicationContext(
            AccountingFlyway.class, AccountingConsumerApplication.class);
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
