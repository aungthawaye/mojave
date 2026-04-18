package org.mojave.core.accounting.domain;

import org.mojave.core.accounting.contract.engine.LedgerEngine;
import org.mojave.core.accounting.engine.mysql.MySqlLedgerEngine;
import org.mojave.component.misc.handy.EnvOrProperty;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

public class AccountingDomainDependencies
    implements AccountingDomainConfiguration.RequiredDependencies {

    private static final String MYSQL_LEDGER_DB_URL = "MYSQL_LEDGER_DB_URL";

    private static final String MYSQL_LEDGER_DB_USER = "MYSQL_LEDGER_DB_USER";

    private static final String MYSQL_LEDGER_DB_PASSWORD = "MYSQL_LEDGER_DB_PASSWORD";

    private static final String MYSQL_LEDGER_DB_CONNECTION_TIMEOUT = "MYSQL_LEDGER_DB_CONNECTION_TIMEOUT";

    private static final String MYSQL_LEDGER_DB_VALIDATION_TIMEOUT = "MYSQL_LEDGER_DB_VALIDATION_TIMEOUT";

    private static final String MYSQL_LEDGER_DB_MAX_LIFETIME_TIMEOUT = "MYSQL_LEDGER_DB_MAX_LIFETIME_TIMEOUT";

    private static final String MYSQL_LEDGER_DB_IDLE_TIMEOUT = "MYSQL_LEDGER_DB_IDLE_TIMEOUT";

    private static final String MYSQL_LEDGER_DB_KEEPALIVE_TIMEOUT = "MYSQL_LEDGER_DB_KEEPALIVE_TIMEOUT";

    private static final String MYSQL_LEDGER_DB_MIN_POOL_SIZE = "MYSQL_LEDGER_DB_MIN_POOL_SIZE";

    private static final String MYSQL_LEDGER_DB_MAX_POOL_SIZE = "MYSQL_LEDGER_DB_MAX_POOL_SIZE";

    private final ObjectMapper objectMapper;

    public AccountingDomainDependencies(final ObjectMapper objectMapper) {

        Objects.requireNonNull(objectMapper);

        this.objectMapper = objectMapper;

    }

    @Bean
    @Override
    public LedgerEngine ledgerEngine() {

        return new MySqlLedgerEngine(
            new MySqlLedgerEngine.LedgerDbSettings(
                new MySqlLedgerEngine.LedgerDbSettings.Connection(
                    EnvOrProperty.get(MYSQL_LEDGER_DB_URL), EnvOrProperty.get(MYSQL_LEDGER_DB_USER),
                    EnvOrProperty.get(MYSQL_LEDGER_DB_PASSWORD),
                    Long.parseLong(EnvOrProperty.get(MYSQL_LEDGER_DB_CONNECTION_TIMEOUT)),
                    Long.parseLong(EnvOrProperty.get(MYSQL_LEDGER_DB_VALIDATION_TIMEOUT)),
                    Long.parseLong(EnvOrProperty.get(MYSQL_LEDGER_DB_MAX_LIFETIME_TIMEOUT)),
                    Long.parseLong(EnvOrProperty.get(MYSQL_LEDGER_DB_IDLE_TIMEOUT)),
                    Long.parseLong(EnvOrProperty.get(MYSQL_LEDGER_DB_KEEPALIVE_TIMEOUT)), false),
                new MySqlLedgerEngine.LedgerDbSettings.Pool(
                    "accounting-ledgerOperation",
                    Integer.parseInt(EnvOrProperty.get(MYSQL_LEDGER_DB_MIN_POOL_SIZE)),
                    Integer.parseInt(EnvOrProperty.get(MYSQL_LEDGER_DB_MAX_POOL_SIZE)))),
            this.objectMapper);
    }

}
