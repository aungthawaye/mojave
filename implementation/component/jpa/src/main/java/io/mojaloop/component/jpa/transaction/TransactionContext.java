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

package io.mojaloop.component.jpa.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public final class TransactionContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionContext.class);

    private static final ThreadLocal<TransactionStatus> CURRENT = new ThreadLocal<>();

    private static final ThreadLocal<PlatformTransactionManager> TX_MANAGER = new ThreadLocal<>();

    private TransactionContext() {

    }

    private static void clear() {

        CURRENT.remove();
        TX_MANAGER.remove();
    }

    public static void commit() {

        TransactionStatus status = CURRENT.get();
        PlatformTransactionManager txManager = TX_MANAGER.get();

        if (status == null || txManager == null) {
            throw new IllegalStateException("No transaction started for this thread.");
        }

        try {

            txManager.commit(status);

        } finally {

            clear();
        }
    }

    public static void rollback() {

        TransactionStatus status = CURRENT.get();
        PlatformTransactionManager txManager = TX_MANAGER.get();

        if (status == null || txManager == null) {

            LOGGER.warn("No transaction started for this thread.");
            return;
        }

        try {

            txManager.rollback(status);

        } finally {

            clear();
        }
    }

    private static void start(PlatformTransactionManager txManager, String name, int propagation, int isolation) {

        if (CURRENT.get() != null) {

            LOGGER.warn("Transaction already started for this thread.");
            return;
        }

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName(name);
        def.setPropagationBehavior(propagation);
        def.setIsolationLevel(isolation);

        TransactionStatus status = txManager.getTransaction(def);

        CURRENT.set(status);
        TX_MANAGER.set(txManager);
    }

    public static void start(PlatformTransactionManager txManager, String name) {

        start(txManager, name, TransactionDefinition.PROPAGATION_REQUIRED, TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    public static void startNew(PlatformTransactionManager txManager, String name) {

        start(txManager, name, TransactionDefinition.PROPAGATION_REQUIRES_NEW, TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

}
