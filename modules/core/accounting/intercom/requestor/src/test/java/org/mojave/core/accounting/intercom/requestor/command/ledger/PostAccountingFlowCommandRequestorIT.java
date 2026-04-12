package org.mojave.core.accounting.intercom.requestor.command.ledger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.core.accounting.contract.command.ledger.PostAccountingFlowCommand;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionNotConfiguredException;
import org.mojave.core.accounting.intercom.requestor.AccountingIntercomRequestorTestConfiguration;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.accounting.AccountOwnerId;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.scenario.ScenarioType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingIntercomRequestorTestConfiguration.class})
@DisplayName("Post Accounting Flow Command Requestor Integration Test")
public class PostAccountingFlowCommandRequestorIT {

    @Autowired
    private PostAccountingFlowCommand postAccountingFlowCommand;

    @Test
    @DisplayName("Throw when flow definition is not configured")
    public void flowDefinitionNotConfigured() {

        final var suffix = System.currentTimeMillis();

        assertThrows(
            FlowDefinitionNotConfiguredException.class,
            () -> this.postAccountingFlowCommand.execute(
                new PostAccountingFlowCommand.Input(
                    ScenarioType.P2P_TRANSFER,
                    Currency.USD,
                    new TransactionId(suffix),
                    Instant.now(),
                    Map.of(
                        "PAYER_FSP", new AccountOwnerId(801001L),
                        "PAYEE_FSP", new AccountOwnerId(801002L)),
                    Map.of(
                        "TRANSFER_AMOUNT", new BigDecimal("100.00"),
                        "PAYEE_FSP_FEE", new BigDecimal("4.00")))));
    }

}

