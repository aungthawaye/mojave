package io.mojaloop.core.account.contract.query;

import io.mojaloop.core.account.contract.data.AccountData;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.fspiop.spec.core.Currency;

public interface AccountQuery {

    AccountData get(AccountId accountId);

    AccountData get(ChartEntryId chartEntryId, OwnerId ownerId, Currency currency);

}
