package io.mojaloop.core.wallet.contract.command.position;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public interface UpdateNetDebitCapCommand {

    record Input(@JsonProperty(required = true) @NotNull PositionId positionId, @JsonProperty(required = true) @NotNull BigDecimal netDebitCap) { }

}
