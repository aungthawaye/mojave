package org.mojave.core.settlement.contract.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.core.settlement.contract.data.SettlementModelDefinitionData;
import org.mojave.core.settlement.contract.exception.definition.SettlementModelDefinitionNotFoundException;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.TransactionPeriod;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.identifier.settlement.SettlementDefinitionId;
import org.mojave.scheme.rule.scenario.ScenarioType;

import java.util.List;

public interface SettlementModelDefinitionQuery {

    String GET_BY_ID_SUBJECT_NAME = "sub-settlement.settlement-model-definition-query.get-by-id";

    String GET_ALL_SUBJECT_NAME = "sub-settlement.settlement-model-definition-query.get-all";

    String GET_BY_NAME_CONTAINS_SUBJECT_NAME = "sub-settlement.settlement-model-definition-query.get-by-name-contains";

    String GET_BY_MATCH_SUBJECT_NAME = "sub-settlement.settlement-model-definition-query.get-by-match";

    SettlementModelDefinitionData get(SettlementDefinitionId settlementModelDefinitionId)
        throws SettlementModelDefinitionNotFoundException;

    List<SettlementModelDefinitionData> getAll();

    List<SettlementModelDefinitionData> getByNameContains(String name);

    SettlementModelDefinitionData get(ScenarioType scenario,
                                      Currency currency,
                                      FspGroupId fspGroupId,
                                      TransactionPeriod transactionPeriod)
        throws SettlementModelDefinitionNotFoundException;

    record GetByIdInput(@JsonProperty(required = true) @NotNull SettlementDefinitionId settlementModelDefinitionId) { }

    record GetAllInput() { }

    record GetByNameContainsInput(@JsonProperty(required = true) @NotNull String name) { }

    record GetByMatchInput(@JsonProperty(required = true) @NotNull ScenarioType scenario,
                           @JsonProperty(required = true) @NotNull Currency currency,
                           @JsonProperty(required = true) @NotNull FspGroupId fspGroupId,
                           @JsonProperty(required = true) @NotNull TransactionPeriod transactionPeriod) { }

}
