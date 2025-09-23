package io.mojaloop.core.account.domain.cache;

import io.mojaloop.core.account.contract.data.AccountData;
import io.mojaloop.core.account.domain.model.Account;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.List;

public interface AccountCache {

    void delete(AccountId accountId);

    AccountData get(AccountCode accountCode);

    List<AccountData> get(OwnerId ownerId);

    AccountData get(AccountId accountId);

    AccountData get(ChartEntryId chartEntryId, OwnerId ownerId, Currency currency);

    AccountData save(Account account);

}
