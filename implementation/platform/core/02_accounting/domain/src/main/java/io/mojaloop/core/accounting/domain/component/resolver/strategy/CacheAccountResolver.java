package io.mojaloop.core.accounting.domain.component.resolver.strategy;

import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.component.resolver.AccountResolver;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.fspiop.spec.core.Currency;

public class CacheAccountResolver implements AccountResolver {

    private final AccountCache accountCache;

    public CacheAccountResolver(AccountCache accountCache) {

        assert accountCache != null;

        this.accountCache = accountCache;
    }

    @Override
    public AccountData resolve(ChartEntryId chartEntryId, AccountOwnerId accountOwnerId, Currency currency) {

        return this.accountCache.get(chartEntryId, accountOwnerId, currency);
    }

}
