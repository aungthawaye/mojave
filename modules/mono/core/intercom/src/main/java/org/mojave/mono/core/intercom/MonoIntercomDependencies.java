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

package org.mojave.mono.core.intercom;

import org.mojave.core.accounting.contract.engine.LedgerEngine;
import org.mojave.core.accounting.engine.mysql.MySqlLedgerEngine;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.mysql.MySqlWalletEngine;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

public class MonoIntercomDependencies implements MonoIntercomConfiguration.RequiredDependencies {

    private final WalletEngine walletEngine;

    private final LedgerEngine ledgerEngine;

    public MonoIntercomDependencies(ObjectMapper objectMapper) {

        Objects.requireNonNull(objectMapper);

        this.walletEngine = new MySqlWalletEngine(new MySqlWalletEngine.WalletDbSettings(
            new MySqlWalletEngine.WalletDbSettings.Connection(
                System.getenv("MYSQL_WALLET_DB_URL"), System.getenv("MYSQL_WALLET_DB_USER"),
                System.getenv("MYSQL_WALLET_DB_PASSWORD"),
                Long.parseLong(System.getenv("MYSQL_WALLET_DB_CONNECTION_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_WALLET_DB_VALIDATION_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_WALLET_DB_MAX_LIFETIME_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_WALLET_DB_IDLE_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_WALLET_DB_KEEPALIVE_TIMEOUT")), false),
            new MySqlWalletEngine.WalletDbSettings.Pool(
                "wallet-engine", Integer.parseInt(System.getenv("MYSQL_WALLET_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("MYSQL_WALLET_DB_MAX_POOL_SIZE")))));

        this.ledgerEngine = new MySqlLedgerEngine(
            new MySqlLedgerEngine.LedgerDbSettings(
                new MySqlLedgerEngine.LedgerDbSettings.Connection(
                    System.getenv("MYSQL_LEDGER_DB_URL"), System.getenv("MYSQL_LEDGER_DB_USER"),
                    System.getenv("MYSQL_LEDGER_DB_PASSWORD"),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_CONNECTION_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_VALIDATION_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_MAX_LIFETIME_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_IDLE_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_KEEPALIVE_TIMEOUT")), false),
                new MySqlLedgerEngine.LedgerDbSettings.Pool(
                    "accounting-ledgerOperation",
                    Integer.parseInt(System.getenv("MYSQL_LEDGER_DB_MIN_POOL_SIZE")),
                    Integer.parseInt(System.getenv("MYSQL_LEDGER_DB_MAX_POOL_SIZE")))),
            objectMapper);

    }

    @Bean
    @Override
    public LedgerEngine ledgerEngine() {

        return this.ledgerEngine;
    }

    @Bean
    @Override
    public WalletEngine walletEngine() {

        return this.walletEngine;
    }

}
