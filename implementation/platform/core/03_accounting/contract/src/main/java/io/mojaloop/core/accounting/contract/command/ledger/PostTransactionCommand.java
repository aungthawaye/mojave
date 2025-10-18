package io.mojaloop.core.accounting.contract.command.ledger;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.core.common.datatype.enums.accounting.MovementResult;
import io.mojaloop.core.common.datatype.enums.accounting.MovementStage;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.LedgerMovementId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface PostTransactionCommand {

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull TransactionType transactionType,
                 @JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull TransactionId transactionId,
                 @JsonProperty(required = true) @NotNull Instant transactionAt,
                 @JsonProperty(required = true) @NotNull Map<String, AccountOwnerId> participants,
                 @JsonProperty(required = true) @NotNull Map<String, BigDecimal> amounts) {

    }

    record Output(List<Flow> flows) {

        public record Flow(AccountId accountId,
                           AccountOwnerId ownerId,
                           ChartEntryId chartEntryId,
                           LedgerMovementId ledgerMovementId,
                           Side side,
                           BigDecimal amount,
                           DrCr oldDrCr,
                           DrCr newDrCr,
                           MovementStage movementStage,
                           MovementResult movementResult) {

        }

        public record DrCr(BigDecimal debits, BigDecimal credits) { }

    }

}
