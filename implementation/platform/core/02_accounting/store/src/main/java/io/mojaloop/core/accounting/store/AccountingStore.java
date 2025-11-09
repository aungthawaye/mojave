package io.mojaloop.core.accounting.store;

import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.Set;

public interface AccountingStore {

    AccountData get(AccountCode accountCode);

    Set<AccountData> get(AccountOwnerId ownerId);

    AccountData get(AccountId accountId);

    AccountData get(ChartEntryId chartEntryId, AccountOwnerId ownerId, Currency currency);

    Set<AccountData> get(ChartEntryId chartEntryId);
}
