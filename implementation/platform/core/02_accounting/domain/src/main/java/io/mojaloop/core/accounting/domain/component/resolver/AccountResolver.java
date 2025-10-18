package io.mojaloop.core.accounting.domain.component.resolver;

import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.fspiop.spec.core.Currency;

public interface AccountResolver {

    AccountData resolve(ChartEntryId chartEntryId, AccountOwnerId accountOwnerId, Currency currency);

}
