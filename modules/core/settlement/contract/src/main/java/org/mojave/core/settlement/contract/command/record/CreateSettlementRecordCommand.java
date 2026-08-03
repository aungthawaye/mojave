package org.mojave.core.settlement.contract.command.record;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.LiquidityDirection;
import org.mojave.scheme.rule.identifier.participant.FspId;
import org.mojave.scheme.rule.identifier.settlement.SettlementAmountId;
import org.mojave.scheme.rule.identifier.settlement.SettlementDefinitionId;
import org.mojave.scheme.rule.identifier.settlement.SettlementRecordId;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.scenario.ScenarioType;

import java.math.BigDecimal;
import java.util.List;

public interface CreateSettlementRecordCommand {

    String SUBJECT_NAME = "sub-settlement.create-settlement-record-command";

    String TOPIC_NAME = "tp-settlement.create-settlement-record-command";

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull TransactionId transactionId,
                 @JsonProperty(required = true) @NotNull ScenarioType scenario,
                 @JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull SettlementDefinitionId settlementModelDefinitionId,
                 SettlementWindowId settlementWindowId,
                 @JsonProperty(required = true) List<SettlementAmount> settlementAmounts) {

        public record SettlementAmount(
            @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String participant,
            FspId fspId,
            @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String amountName,
            @JsonProperty(required = true) @NotNull BigDecimal amount,
            @JsonProperty(required = true) @NotNull Currency currency,
            @JsonProperty(required = true) @NotNull LiquidityDirection direction) { }

    }

    record Output(SettlementRecordId settlementRecordId, List<SettlementAmountId> settlementAmountIds) { }

}
