package org.mojave.core.accounting.ledger.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojave.core.accounting.contract.engine.LedgerEngine;
import org.mojave.core.accounting.engine.mysql.MySqlLedgerEngine;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.math.BigDecimal;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MySqlLedgerEngineIT {

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {

        this.jdbcTemplate = mock(JdbcTemplate.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getDrCr_shouldReturnPostedDebitsAndCredits_whenLedgerBalanceExists()
        throws Exception {

        final var ledgerOperation = mock(MySqlLedgerEngine.class);
        final var accountId = new AccountId(1L);
        final var resultSet = mock(ResultSet.class);

        doCallRealMethod().when(ledgerOperation).getDrCr(any(), any());

        final var field = MySqlLedgerEngine.class.getDeclaredField("jdbcTemplate");
        field.setAccessible(true);
        field.set(ledgerOperation, this.jdbcTemplate);

        when(this.jdbcTemplate.query(anyString(), any(ResultSetExtractor.class), anyLong()))
            .thenAnswer(invocation -> {

                final ResultSetExtractor<?> extractor = invocation.getArgument(1);

                return extractor.extractData(resultSet);
            });

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBigDecimal("posted_debits")).thenReturn(new BigDecimal("10.00"));
        when(resultSet.getBigDecimal("posted_credits")).thenReturn(new BigDecimal("4.00"));

        final LedgerEngine.DrCr drCr = ledgerOperation.getDrCr(accountId, Side.DEBIT);

        assertEquals(new BigDecimal("10.00"), drCr.debits());
        assertEquals(new BigDecimal("4.00"), drCr.credits());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getDrCr_shouldReturnZeroes_whenLedgerBalanceDoesNotExist() throws Exception {

        final var ledgerOperation = mock(MySqlLedgerEngine.class);
        final var accountId = new AccountId(1L);
        final var resultSet = mock(ResultSet.class);

        doCallRealMethod().when(ledgerOperation).getDrCr(any(), any());

        final var field = MySqlLedgerEngine.class.getDeclaredField("jdbcTemplate");
        field.setAccessible(true);
        field.set(ledgerOperation, this.jdbcTemplate);

        when(this.jdbcTemplate.query(anyString(), any(ResultSetExtractor.class), anyLong()))
            .thenAnswer(invocation -> {

                final ResultSetExtractor<?> extractor = invocation.getArgument(1);

                return extractor.extractData(resultSet);
            });

        when(resultSet.next()).thenReturn(false);

        final LedgerEngine.DrCr drCr = ledgerOperation.getDrCr(accountId, Side.CREDIT);

        assertEquals(BigDecimal.ZERO, drCr.debits());
        assertEquals(BigDecimal.ZERO, drCr.credits());
    }
}
