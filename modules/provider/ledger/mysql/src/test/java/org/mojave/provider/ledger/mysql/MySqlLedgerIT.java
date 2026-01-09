package org.mojave.provider.ledger.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojave.provider.ledger.contract.Ledger.AccountIdAlreadyTakenException;
import org.mojave.provider.ledger.contract.Ledger.LedgerBalance;
import org.mojave.scheme.common.datatype.enums.accounting.OverdraftMode;
import org.mojave.scheme.common.datatype.enums.accounting.Side;
import org.mojave.scheme.common.datatype.identifier.accounting.AccountId;
import org.mojave.scheme.fspiop.core.Currency;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MySqlLedgerIT {

    private JdbcTemplate jdbcTemplate;
    private ObjectMapper objectMapper;
    private MySqlLedger mySqlLedger;

    @BeforeEach
    public void setUp() {
        this.jdbcTemplate = mock(JdbcTemplate.class);
        this.objectMapper = mock(ObjectMapper.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createLedgerBalance_shouldThrowException_whenAccountIdAlreadyTaken() throws Exception {
        // Arrange
        var accountId = new AccountId(1L);
        var ledgerBalance = new LedgerBalance(
            accountId, Currency.USD, 2, Side.DEBIT, BigDecimal.ZERO, BigDecimal.ZERO,
            OverdraftMode.FORBID, BigDecimal.ZERO, Instant.now()
        );

        // Mock MySqlLedger
        var ledger = mock(MySqlLedger.class);
        doCallRealMethod().when(ledger).createLedgerBalance(any());

        // Use reflection to set jdbcTemplate
        var field = MySqlLedger.class.getDeclaredField("jdbcTemplate");
        field.setAccessible(true);
        field.set(ledger, this.jdbcTemplate);

        var con = mock(Connection.class);
        var stm = mock(PreparedStatement.class);
        var rs = mock(ResultSet.class);

        when(this.jdbcTemplate.execute(any(ConnectionCallback.class))).thenAnswer(invocation -> {
            ConnectionCallback<?> callback = invocation.getArgument(0);
            return callback.doInConnection(con);
        });

        when(con.prepareStatement(anyString())).thenReturn(stm);
        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);

        // Act & Assert
        assertThrows(AccountIdAlreadyTakenException.class, () -> {
            ledger.createLedgerBalance(ledgerBalance);
        });

        verify(con).setAutoCommit(false);
        verify(con).rollback();
        verify(stm, never()).executeUpdate();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createLedgerBalance_shouldInsert_whenAccountIdNotTaken() throws Exception {
        // Arrange
        var accountId = new AccountId(1L);
        var createdAt = Instant.now();
        var ledgerBalance = new LedgerBalance(
            accountId, Currency.USD, 2, Side.DEBIT, BigDecimal.ZERO, BigDecimal.ZERO,
            OverdraftMode.FORBID, BigDecimal.ZERO, createdAt
        );

        // Mock MySqlLedger
        var ledger = mock(MySqlLedger.class);
        doCallRealMethod().when(ledger).createLedgerBalance(any());

        // Use reflection to set jdbcTemplate
        var field = MySqlLedger.class.getDeclaredField("jdbcTemplate");
        field.setAccessible(true);
        field.set(ledger, this.jdbcTemplate);

        var con = mock(Connection.class);
        var stmCheck = mock(PreparedStatement.class);
        var stmInsert = mock(PreparedStatement.class);
        var rs = mock(ResultSet.class);

        when(this.jdbcTemplate.execute(any(ConnectionCallback.class))).thenAnswer(invocation -> {
            ConnectionCallback<?> callback = invocation.getArgument(0);
            return callback.doInConnection(con);
        });

        when(con.prepareStatement(contains("SELECT COUNT(*)"))).thenReturn(stmCheck);
        when(con.prepareStatement(contains("INSERT INTO"))).thenReturn(stmInsert);
        when(stmCheck.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(0);

        // Act
        ledger.createLedgerBalance(ledgerBalance);

        // Assert
        verify(con).setAutoCommit(false);
        verify(stmInsert).executeUpdate();
        verify(con).commit();
    }
}
