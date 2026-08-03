package org.mojave.core.settlement.contract.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.core.settlement.contract.data.SettlementWindowData;
import org.mojave.core.settlement.contract.exception.window.SettlementWindowNotFoundException;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;

import java.util.List;

public interface SettlementWindowQuery {

    String GET_BY_ID_SUBJECT_NAME = "sub-settlement.settlement-window-query.get-by-id";

    String GET_ALL_SUBJECT_NAME = "sub-settlement.settlement-window-query.get-all";

    String GET_OPEN_SUBJECT_NAME = "sub-settlement.settlement-window-query.get-open";

    SettlementWindowData get(SettlementWindowId settlementWindowId) throws SettlementWindowNotFoundException;

    List<SettlementWindowData> getAll();

    SettlementWindowData getOpen(SettlementModelId settlementModelId,
                                 Currency currency,
                                 FspGroupId fspGroupId) throws SettlementWindowNotFoundException;

    record GetByIdInput(@JsonProperty(required = true) @NotNull SettlementWindowId settlementWindowId) { }

    record GetAllInput() { }

    record GetOpenInput(@JsonProperty(required = true) @NotNull SettlementModelId settlementModelId,
                        @JsonProperty(required = true) @NotNull Currency currency,
                        @JsonProperty(required = true) @NotNull FspGroupId fspGroupId) { }

}
