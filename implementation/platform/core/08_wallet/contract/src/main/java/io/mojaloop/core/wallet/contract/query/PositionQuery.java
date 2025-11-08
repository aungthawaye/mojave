package io.mojaloop.core.wallet.contract.query;

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.wallet.contract.data.PositionData;

import java.util.List;

public interface PositionQuery {

    PositionData get(PositionId positionId);

    List<PositionData> getAll();

}
