package io.mojaloop.operation.usecase.domain.participant;

import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.fspiop.spec.core.Currency;

public interface AddFsp {

    record Input(FspCode fspCode, String name, Currency currency) { }

}
