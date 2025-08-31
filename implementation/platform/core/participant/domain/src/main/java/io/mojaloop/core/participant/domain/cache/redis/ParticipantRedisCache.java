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

package io.mojaloop.core.participant.domain.cache.redis;

import io.mojaloop.component.redis.RedissonOpsClient;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.FxpId;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.FxpData;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.domain.cache.ParticipantCache;
import io.mojaloop.core.participant.domain.repository.FspRepository;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RMap;
import org.redisson.api.RSetMultimap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Qualifier(ParticipantCache.Qualifiers.REDIS)
public class ParticipantRedisCache implements ParticipantCache {

    private final FspRepository fspRepository;

    private final RMap<Long, FspData> fspWithId;

    private final RMap<String, FspData> fspWithFspCode;

    private final RMap<Long, OracleData> oracleWithId;

    private final RMap<String, OracleData> oracleWithPartyType;

    private final RMap<Long, FxpData> fxpWithId;

    private final RSetMultimap<String, FxpData> fxpWithCurrencyPair;

    public ParticipantRedisCache(FspRepository fspRepository, RedissonOpsClient redissonOpsClient) {

        assert fspRepository != null;
        assert redissonOpsClient != null;

        this.fspRepository = fspRepository;

        var redissonClient = redissonOpsClient.getRedissonClient();

        this.fspWithId = redissonClient.getMap(ParticipantCache.Names.FSP_WITH_ID);
        this.fspWithFspCode = redissonClient.getMap(ParticipantCache.Names.FSP_WITH_FSP_CODE);

        this.oracleWithId = redissonClient.getMap(ParticipantCache.Names.ORACLE_WITH_ID);
        this.oracleWithPartyType = redissonClient.getMap(ParticipantCache.Names.ORACLE_WITH_PARTY_TYPE);

        this.fxpWithId = redissonClient.getMap(ParticipantCache.Names.FXP_WITH_ID);
        this.fxpWithCurrencyPair = redissonClient.getSetMultimap(ParticipantCache.Names.FXP_WITH_CURRENCY_PAIR);
    }

    @Override
    public void delete(FspId fspId) {

        var deleted = this.fspWithId.remove(fspId.getId());
        this.fspWithFspCode.remove(deleted.fspCode().value());
    }

    @Override
    public void delete(OracleId oracleId) {

        var deleted = this.oracleWithId.remove(oracleId.getId());
        this.oracleWithPartyType.remove(deleted.type());
    }

    @Override
    public void delete(FxpId fxpId) {

        var deleted = this.fxpWithId.remove(fxpId.getId());

        deleted.fxRatePairs().forEach((currencyPair, fxRatePairData) -> this.fxpWithCurrencyPair.remove(currencyPair, deleted));
    }

    @Override
    public FspData getFspData(FspId fspId) {

        return this.fspWithId.get(fspId.getId());
    }

    @Override
    public FspData getFspData(FspCode fspCode) {

        return this.fspWithFspCode.get(fspCode.value());
    }

    @Override
    public Set<FxpData> getFxRatePairData(Currency sourceCurrency, Currency targetCurrency) {

        return new HashSet<>(this.fxpWithCurrencyPair.get(Keys.forFxRatePair(sourceCurrency, targetCurrency)));
    }

    @Override
    public FxpData getFxpData(FxpId fxpId) {

        return this.fxpWithId.get(fxpId.getId());
    }

    @Override
    public OracleData getOracleData(OracleId oracleId) {

        return this.oracleWithId.get(oracleId.getId());
    }

    @Override
    public OracleData getOracleData(PartyIdType partyIdType) {

        return this.oracleWithPartyType.get(partyIdType.name());
    }

    @PostConstruct
    public void postConstruct() {

        var fsps = this.fspRepository.findAll();

        for (var fsp : fsps) {

            this.save(fsp.convert());
        }
    }

    @Override
    public void save(FspData fspData) {

        this.fspWithId.put(fspData.fspId().getId(), fspData);
        this.fspWithFspCode.put(fspData.fspCode().value(), fspData);
    }

    @Override
    public void save(FxpData fxpData) {

        this.fxpWithId.put(fxpData.fxpId().getId(), fxpData);

        fxpData.fxRatePairs().forEach((currencyPair, fxRatePairData) -> this.fxpWithCurrencyPair.put(currencyPair, fxpData));
    }

    @Override
    public void save(OracleData oracleData) {

        this.oracleWithId.put(oracleData.oracleId().getId(), oracleData);
        this.oracleWithPartyType.put(oracleData.type().name(), oracleData);
    }

}
