package org.mojave.core.settlement.contract.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.core.settlement.contract.data.SettlementModelData;
import org.mojave.core.settlement.contract.exception.model.SettlementModelNotFoundException;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;

import java.util.List;

public interface SettlementModelQuery {

    String GET_BY_ID_SUBJECT_NAME = "sub-settlement.settlement-model-query.get-by-id";

    String GET_ALL_SUBJECT_NAME = "sub-settlement.settlement-model-query.get-all";

    String GET_BY_NAME_CONTAINS_SUBJECT_NAME = "sub-settlement.settlement-model-query.get-by-name-contains";

    SettlementModelData get(SettlementModelId settlementModelId) throws SettlementModelNotFoundException;

    List<SettlementModelData> getAll();

    List<SettlementModelData> getByNameContains(String name);

    record GetByIdInput(@JsonProperty(required = true) @NotNull SettlementModelId settlementModelId) { }

    record GetAllInput() { }

    record GetByNameContainsInput(@JsonProperty(required = true) @NotNull String name) { }

}
