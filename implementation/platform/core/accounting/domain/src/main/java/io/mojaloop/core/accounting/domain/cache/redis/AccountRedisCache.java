/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.core.accounting.domain.cache.redis;

import io.mojaloop.component.redis.RedissonOpsClient;
import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.repository.AccountRepository;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.OwnerId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RMap;
import org.redisson.api.RSetMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Qualifier(AccountCache.Qualifiers.REDIS)
public class AccountRedisCache implements AccountCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRedisCache.class);

    private final AccountRepository accountRepository;

    private final RMap<Long, AccountData> withId;

    private final RMap<String, AccountData> withCode;

    private final RSetMultimap<Long, AccountData> withOwnerId;

    private final RMap<String, AccountData> withChartEntryIdOwnerIdCurrency;

    public AccountRedisCache(AccountRepository accountRepository, RedissonOpsClient redissonOpsClient) {

        assert accountRepository != null;
        assert redissonOpsClient != null;

        this.accountRepository = accountRepository;

        this.withId = redissonOpsClient.getRedissonClient().getMap(Names.WITH_ID);
        this.withCode = redissonOpsClient.getRedissonClient().getMap(Names.WITH_CODE);
        this.withOwnerId = redissonOpsClient.getRedissonClient().getSetMultimap(Names.WITH_OWNER_ID);
        this.withChartEntryIdOwnerIdCurrency = redissonOpsClient.getRedissonClient()
                                                                .getMap(Names.WITH_CHARTENTRYID_OWNERID_CURRENCY);

    }

    @Override
    public void clear() {

        this.withId.clear();
        this.withCode.clear();
        this.withOwnerId.clear();
        this.withChartEntryIdOwnerIdCurrency.clear();
    }

    @Override
    public void delete(AccountId accountId) {

        var deleted = this.withId.remove(accountId.getId());

        if (deleted == null) {
            return;
        }

        this.withCode.remove(deleted.code().value());

        var set = this.withOwnerId.get(deleted.ownerId().getId());

        if (set != null && !set.isEmpty()) {

            set.stream()
               .filter(a -> a.accountId().equals(accountId))
               .forEach(a -> this.withOwnerId.remove(a.ownerId().getId(), a));

        }

        var key = AccountCache.Keys.forChart(deleted.chartEntryId(), deleted.ownerId(), deleted.currency());
        this.withChartEntryIdOwnerIdCurrency.remove(key);
    }

    @Override
    public AccountData get(AccountCode accountCode) {

        return this.withCode.get(accountCode.value());
    }

    @Override
    public Set<AccountData> get(OwnerId ownerId) {

        return this.withOwnerId.get(ownerId.getId());
    }

    @Override
    public AccountData get(AccountId accountId) {

        return this.withId.get(accountId.getId());
    }

    @Override
    public AccountData get(ChartEntryId chartEntryId, OwnerId ownerId, Currency currency) {

        var key = AccountCache.Keys.forChart(chartEntryId, ownerId, currency);

        return this.withChartEntryIdOwnerIdCurrency.get(key);
    }

    @PostConstruct
    public void postConstruct() {

        this.clear();

        var accounts = this.accountRepository.findAll();

        for (var account : accounts) {

            this.save(account.convert());
        }
    }

    @Override
    public void save(AccountData account) {

        this.withId.put(account.accountId().getId(), account);
        this.withCode.put(account.code().value(), account);
        this.withOwnerId.put(account.ownerId().getId(), account);

        var key = AccountCache.Keys.forChart(account.chartEntryId(), account.ownerId(), account.currency());

        this.withChartEntryIdOwnerIdCurrency.put(key, account);
    }

}
