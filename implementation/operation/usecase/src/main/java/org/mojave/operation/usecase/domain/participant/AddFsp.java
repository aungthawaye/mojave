package org.mojave.operation.usecase.domain.participant;

import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.fspiop.spec.core.Currency;

public interface AddFsp {

    record Input(FspCode fspCode, String name, Currency currency) { }

}
