package io.mojaloop.core.wallet.contract.command.position;

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;

import java.math.BigDecimal;

public interface UpdateNetDebitCapCommand {

    record Input(PositionId positionId, BigDecimal netDebitCap) { }

}
