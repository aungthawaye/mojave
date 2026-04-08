package org.mojave.accounting.engine.mysql;

import com.mysql.cj.jdbc.Driver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mojave.accounting.contract.engine.LedgerEngine;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.accounting.MovementResult;
import org.mojave.common.datatype.enums.accounting.MovementStage;
import org.mojave.common.datatype.enums.accounting.OverdraftMode;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.common.datatype.identifier.accounting.FlowLineId;
import org.mojave.common.datatype.identifier.accounting.LedgerMovementId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.component.flyway.FlywayMigration;
import org.mojave.scheme.rule.accounting.scenario.AccountingScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class MySqlLedgerEngine implements LedgerEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlLedgerEngine.class);

    private static final String SQL_CHECK_LEDGER_BALANCE_EXISTENCE = """
        SELECT 
            COUNT(account_id) 
        FROM lgr_ledger_balance 
        WHERE account_id = ?
        """;

    private static final String SQL_INSERT_LEDGER_BALANCE = """
        INSERT INTO lgr_ledger_balance (
            account_id,
            currency,
            `scale`,
            nature,
            posted_debits,
            posted_credits,
            overdraft_mode,
            overdraft_limit,
            created_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_LEDGER_DR_CR = """
        SELECT posted_debits,
               posted_credits
        FROM lgr_ledger_balance
        WHERE account_id = ?
        """;

    private static final String SQL_CHECK_DUPLICATE_POSTING = """
        SELECT COUNT(*)
        FROM lgr_ledger_movement
        WHERE account_id = ?
          AND side = ?
          AND transaction_id = ?
        """;

    private static final String SQL_SELECT_MOVEMENTS_BY_TRANSACTION = """
        SELECT ledger_movement_id,
               step,
               account_id,
               side,
               currency,
               amount,
               old_debits,
               old_credits,
               new_debits,
               new_credits,
               transaction_id,
               transaction_at,
               scenario,
               flow_definition_id,
               flow_line_id,
               movement_stage,
               movement_result,
               created_at
        FROM lgr_ledger_movement
        WHERE transaction_id = ?
        ORDER BY ledger_movement_id
        """;

    private final JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper;

    public MySqlLedgerEngine(LedgerDbSettings settings, ObjectMapper objectMapper) {

        Objects.requireNonNull(settings);
        Objects.requireNonNull(objectMapper);

        LOGGER.info("MySqlLedgerOperation settings: ({})", settings);

        var flyway = FlywayMigration.configure(new FlywayMigration.Settings(
            settings.connection().url, settings.connection().username(),
            settings.connection().password(), "flyway_mysql_ledger_history",
            new String[]{"classpath:migration/ledger/mysql"}));

        flyway.migrate();

        LOGGER.info("MySqlLedgerOperation flyway migrated successfully.");

        var config = new HikariConfig();

        // Basic
        config.setPoolName(settings.pool().name());
        config.setJdbcUrl(settings.connection().url());
        config.setUsername(settings.connection().username());
        config.setPassword(settings.connection().password());
        config.setDriverClassName(Driver.class.getName());

        // ---- MySQL driver performance flags ----
        // Statement cache
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "500");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096");

        // Batch optimization (if you use batch)
        config.addDataSourceProperty("rewriteBatchedStatements", "true");

        // Metadata / session state caches
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        // Server-side prepared statements
        // If you *know* CALL metadata bugs are not an issue in your env, turn this ON for higher TPS:
        config.addDataSourceProperty("useServerPrepStmts", "true");
        // If you still hit CALL issues, set to false and keep callableStmtCacheSize = 0
        // config.addDataSourceProperty("useServerPrepStmts", "false");
        config.addDataSourceProperty("callableStmtCacheSize", "100");

        // Optional: keep your previous "useResetSession" behaviour
        config.addDataSourceProperty("useResetSession", "true");

        // ---- Hikari pool sizing ----
        config.setMaximumPoolSize(settings.pool().maxPool());
        // For no permanent idle connections: pool can shrink to 0 when app is idle
        config.setMinimumIdle(settings.pool().minPool());

        // ---- Timeouts (fail fast under high TPS) ----
        config.setConnectionTimeout(settings.connection().connectionTimeout());
        config.setValidationTimeout(settings.connection().validationTimeout());
        config.setAutoCommit(settings.connection().autoCommit());

        // ---- Idle behaviour & “no idle wakeup” to DB ----

        // 1) Do NOT set a connectionTestQuery
        //    Hikari will use connection.isValid() only when a connection is borrowed.

        // 2) Disable keepalive pings – no queries while idle.
        config.setKeepaliveTime(settings.connection().keepaliveTime());

        // 3) Let the pool close connections when app is idle, so DB sees *zero* connections
        //    after some quiet period. No idle queries because there are no connections.
        //
        //    Example: after 60s of zero usage, shrink pool to 0.
        config.setIdleTimeout(settings.connection().idleTimeout());

        // 4) Reasonable max lifetime to avoid stale connections,
        //    but still no idle test queries.
        //    Make this a bit less than MySQL wait_timeout if you changed it.
        config.setMaxLifetime(settings.connection().maxLifetime());

        this.jdbcTemplate = new JdbcTemplate(new HikariDataSource(config));
        this.objectMapper = objectMapper;
    }

    @Override
    public LedgerBalance createLedgerBalance(AccountId accountId, Currency currency, Integer scale,
                                             Side nature, BigDecimal postedDebits,
                                             BigDecimal postedCredits, OverdraftMode overdraftMode,
                                             BigDecimal overdraftLimit)
        throws AccountIdAlreadyTakenException {

        final var now = Instant.now();

        try {

            var ledgerBalance = this.jdbcTemplate.execute(
                (ConnectionCallback<LedgerBalance>) con -> {

                    var autoCommit = con.getAutoCommit();

                    try {

                        con.setAutoCommit(false);

                        try (var stm = con.prepareStatement(SQL_CHECK_LEDGER_BALANCE_EXISTENCE)) {

                            stm.setLong(1, accountId.getId());

                            try (var rs = stm.executeQuery()) {

                                if (rs.next() && rs.getInt(1) > 0) {
                                    throw new RuntimeException(new AccountIdAlreadyTakenException(
                                        new AccountId(accountId.getId())));
                                }
                            }
                        }

                        try (var stm = con.prepareStatement(SQL_INSERT_LEDGER_BALANCE)) {

                            stm.setLong(1, accountId.getId());
                            stm.setString(2, currency.name());
                            stm.setInt(3, scale);
                            stm.setString(4, nature.name());
                            stm.setBigDecimal(5, postedDebits);
                            stm.setBigDecimal(6, postedCredits);
                            stm.setString(7, overdraftMode.name());
                            stm.setBigDecimal(8, overdraftLimit);
                            stm.setLong(9, now.getEpochSecond());

                            stm.executeUpdate();
                        }

                        con.commit();

                        return new LedgerBalance(
                            accountId, currency, scale, nature, postedDebits, postedCredits,
                            overdraftMode, overdraftLimit, now);

                    } catch (Exception e) {

                        con.rollback();
                        throw e;

                    } finally {

                        con.setAutoCommit(autoCommit);
                    }

                });

            return ledgerBalance;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            if (e.getCause() instanceof AccountIdAlreadyTakenException exception) {
                throw exception;
            }

            throw (RuntimeException) e;

        }
    }

    @Override
    public DrCr getDrCr(AccountId accountId, Side side) {

        Objects.requireNonNull(accountId);
        Objects.requireNonNull(side);

        try {

            return this.jdbcTemplate.query(
                SQL_SELECT_LEDGER_DR_CR, rs -> {

                    if (!rs.next()) {
                        return new DrCr(BigDecimal.ZERO, BigDecimal.ZERO);
                    }

                    final var debits = rs.getBigDecimal("posted_debits");
                    final var credits = rs.getBigDecimal("posted_credits");

                    return new DrCr(
                        debits == null ? BigDecimal.ZERO : debits,
                        credits == null ? BigDecimal.ZERO : credits);
                }, accountId.getId());

        } catch (RuntimeException e) {

            LOGGER.error("Error:", e);
            throw e;

        }
    }

    @Override
    public String getEngineType() {

        return "MYSQL";
    }

    @Override
    public List<Movement> postAccountingFlow(List<Request> requests, TransactionId transactionId,
                                             Instant transactionAt, AccountingScenario scenario)
        throws
        InsufficientBalanceException,
        OverdraftExceededException,
        RestoreFailedException,
        DuplicatePostingException {

        try {

            var addedKeys = new HashSet<String>();

            requests.forEach(request -> {

                var added = addedKeys.add(
                    request.accountId().getId() + request.side().name() + transactionId.getId());

                if (!added) {
                    throw new RuntimeException(
                        new DuplicatePostingException(
                            request.accountId(), request.side(),
                            transactionId));
                }
            });

            var posting = requests.stream().map(request -> new Posting(
                request.ledgerMovementId().getId(), request.step(), request.accountId().getId(),
                request.side().name(), request.currency().name(), request.amount().toPlainString(),
                transactionId.getId(), transactionAt.getEpochSecond(), scenario.name(),
                request.flowDefinitionId().getId(), request.flowLineId().getId())).toList();

            var postingJson = this.objectMapper.writeValueAsString(posting);

            final var output = this.jdbcTemplate.execute((ConnectionCallback<ProcedureOutput>) con -> {

                var movements = new ArrayList<Movement>();

                try (var stm = con.prepareCall("{call sp_post_ledger_batch_with_movements(?)}")) {

                    stm.setString(1, postingJson);

                    var hasResults = stm.execute();

                    while (hasResults) {

                        try (var rs = stm.getResultSet()) {

                            if (rs != null && rs.next()) {

                                var status = rs.getString("status");

                                switch (status) {
                                    case "ERROR" -> {
                                        handleError(rs, transactionId);
                                    }
                                    case "SUCCESS" -> {
                                        return new ProcedureOutput(ProcedureStatus.SUCCESS, movements);
                                    }
                                    case "IGNORED" -> {
                                        return new ProcedureOutput(ProcedureStatus.IGNORED, movements);
                                    }
                                    default -> { }
                                }
                            }

                        }

                        hasResults = stm.getMoreResults();
                    }
                }

                return new ProcedureOutput(ProcedureStatus.IGNORED, movements);

            });

            if (output.status() == ProcedureStatus.SUCCESS) {
                return this.fetchMovements(transactionId);
            }

            return output.movements();

        } catch (Exception e) {

            LOGGER.error("Exception:", e);

            RuntimeException re = (RuntimeException) e;
            if (re.getCause() != null) {

                if (re.getCause() instanceof InsufficientBalanceException e1) {
                    throw e1;
                }

                if (re.getCause() instanceof OverdraftExceededException e1) {
                    throw e1;
                }

                if (re.getCause() instanceof RestoreFailedException e1) {
                    throw e1;
                }

                if (re.getCause() instanceof DuplicatePostingException e1) {
                    throw e1;
                }
            }

            throw new RuntimeException(re.getCause() != null ? re.getCause() : re);

        }
    }

    private void handleError(ResultSet rs, TransactionId transactionId) throws SQLException {

        var code = rs.getString("err_code");
        var accountId = rs.getLong("err_account_id");
        var side = rs.getString("err_side");
        var currency = rs.getString("err_currency");
        var amount = rs.getBigDecimal("err_amount");
        var debits = rs.getBigDecimal("err_debits");
        var credits = rs.getBigDecimal("err_credits");

        if (code == null || code.isBlank()) {

            if (this.isDuplicatePosting(accountId, side, transactionId)) {
                code = "DUPLICATE_POSTING";
            } else {
                throw new NoMovementResultException();
            }
        }

        switch (code) {

            case "DUPLICATE_POSTING": {
                throw new RuntimeException(
                    new DuplicatePostingException(
                        new AccountId(accountId), Side.valueOf(side),
                        transactionId));
            }

            case "INSUFFICIENT_BALANCE": {
                throw new RuntimeException(
                    new InsufficientBalanceException(
                        new AccountId(accountId), Side.valueOf(side), amount,
                        new DrCr(debits, credits), transactionId));
            }

            case "OVERDRAFT_EXCEEDED": {
                throw new RuntimeException(
                    new OverdraftExceededException(
                        new AccountId(accountId), Side.valueOf(side), amount,
                        new DrCr(debits, credits), transactionId));
            }

            case "RESTORE_FAILED": {
                throw new RuntimeException(
                    new RestoreFailedException(
                        new AccountId(accountId), Side.valueOf(side), amount,
                        new DrCr(debits, credits), transactionId));
            }

            default:
                throw new NoMovementResultException();
        }
    }

    private boolean isDuplicatePosting(final long accountId, final String side,
                                       final TransactionId transactionId) {

        if (accountId <= 0 || side == null || side.isBlank() || transactionId == null) {
            return false;
        }

        final var duplicatePostingCount = this.jdbcTemplate.queryForObject(
            SQL_CHECK_DUPLICATE_POSTING,
            Long.class,
            accountId,
            side,
            transactionId.getId());

        return duplicatePostingCount != null && duplicatePostingCount > 0;
    }

    private List<Movement> fetchMovements(final TransactionId transactionId) {

        return this.jdbcTemplate.query(
            SQL_SELECT_MOVEMENTS_BY_TRANSACTION,
            (rs, __) -> new Movement(
                new LedgerMovementId(rs.getLong("ledger_movement_id")),
                rs.getInt("step"),
                new AccountId(rs.getLong("account_id")),
                Side.valueOf(rs.getString("side")),
                Currency.valueOf(rs.getString("currency")),
                rs.getBigDecimal("amount"),
                new DrCr(rs.getBigDecimal("old_debits"), rs.getBigDecimal("old_credits")),
                new DrCr(rs.getBigDecimal("new_debits"), rs.getBigDecimal("new_credits")),
                new TransactionId(rs.getLong("transaction_id")),
                Instant.ofEpochSecond(rs.getLong("transaction_at")),
                AccountingScenario.valueOf(rs.getString("scenario")),
                new FlowDefinitionId(rs.getLong("flow_definition_id")),
                new FlowLineId(rs.getLong("flow_line_id")),
                MovementStage.valueOf(rs.getString("movement_stage")),
                MovementResult.valueOf(rs.getString("movement_result")),
                Instant.ofEpochSecond(rs.getLong("created_at"))),
            transactionId.getId());
    }

    public record LedgerDbSettings(LedgerDbSettings.Connection connection,
                                   LedgerDbSettings.Pool pool) {

        public record Connection(String url,
                                 String username,
                                 String password,
                                 long connectionTimeout,
                                 long validationTimeout,
                                 long maxLifetime,
                                 long idleTimeout,
                                 long keepaliveTime,
                                 boolean autoCommit) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

    private record Posting(long ledgerMovementId,
                           int step,
                           long accountId,
                           String side,
                           String currency,
                           String amount,
                           long transactionId,
                           long transactionAt,
                           String scenario,
                           long flowDefinitionId,
                           long flowLineId) { }

    private record ProcedureOutput(ProcedureStatus status, List<Movement> movements) { }

    private enum ProcedureStatus {
        SUCCESS,
        IGNORED
    }

}
