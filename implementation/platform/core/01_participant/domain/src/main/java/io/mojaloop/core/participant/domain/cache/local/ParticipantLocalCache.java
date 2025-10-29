/*-
 * ================================================================================
 * Mojave
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
package io.mojaloop.core.participant.domain.cache.local;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.FxpId;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.FxpData;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.domain.cache.ParticipantCache;
import io.mojaloop.core.participant.domain.repository.FspRepository;
import io.mojaloop.core.participant.domain.repository.OracleRepository;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class ParticipantLocalCache implements ParticipantCache {

    private final FspRepository fspRepository;

    private final OracleRepository oracleRepository;

    private final Map<Long, FspData> fspWithId;

    private final Map<String, FspData> fspWithFspCode;

    private final Map<Long, OracleData> oracleWithId;

    private final Map<String, OracleData> oracleWithPartyType;

    private final Map<Long, FxpData> fxpWithId;

    private final Map<String, Set<FxpData>> fxpWithCurrencyPair;

    public ParticipantLocalCache(FspRepository fspRepository, OracleRepository oracleRepository) {

        assert fspRepository != null;
        assert oracleRepository != null;

        this.fspRepository = fspRepository;
        this.oracleRepository = oracleRepository;

        this.fspWithId = new ConcurrentHashMap<>();
        this.fspWithFspCode = new ConcurrentHashMap<>();

        this.oracleWithId = new ConcurrentHashMap<>();
        this.oracleWithPartyType = new ConcurrentHashMap<>();

        this.fxpWithId = new ConcurrentHashMap<>();
        this.fxpWithCurrencyPair = new ConcurrentHashMap<>();
    }

    @Override
    public void bootstrap() {

        this.clear();

        var fsps = this.fspRepository.findAll();
        var oracles = this.oracleRepository.findAll();

        for (var fsp : fsps) {

            this.save(fsp.convert());
        }

        for (var oracle : oracles) {

            this.save(oracle.convert());
        }
    }

    @Override
    public void clear() {

        this.fspWithId.clear();
        this.fspWithFspCode.clear();
        this.oracleWithId.clear();
        this.oracleWithPartyType.clear();
        this.fxpWithId.clear();
        this.fxpWithCurrencyPair.clear();
    }

    @Override
    public void delete(FspId fspId) {

        var deleted = this.fspWithId.remove(fspId.getId());

        if (deleted == null) {
            return;
        }

        this.fspWithFspCode.remove(deleted.fspCode().value());
    }

    @Override
    public void delete(OracleId oracleId) {

        var deleted = this.oracleWithId.remove(oracleId.getId());

        if (deleted == null) {
            return;
        }

        this.oracleWithPartyType.remove(deleted.type().name());
    }

    @Override
    public void delete(FxpId fxpId) {

        var deleted = this.fxpWithId.remove(fxpId.getId());

        if (deleted == null) {
            return;
        }

        deleted.fxRatePairs().forEach((currencyPair, __) -> {

            var set = this.fxpWithCurrencyPair.get(currencyPair);

            if (set == null || set.isEmpty()) {
                return;
            }

            set.stream().filter(fxp -> fxp.fxpId().equals(fxpId)).forEach(fxp -> this.fxpWithCurrencyPair.remove(currencyPair, fxp));
        });
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

        this.bootstrap();
    }

    @Override
    public void save(FspData fspData) {

        this.fspWithId.put(fspData.fspId().getId(), fspData);
        this.fspWithFspCode.put(fspData.fspCode().value(), fspData);
    }

    @Override
    public void save(FxpData fxpData) {

        this.fxpWithId.put(fxpData.fxpId().getId(), fxpData);

        fxpData.fxRatePairs().forEach((currencyPair, _fxpRatePairData) -> this.fxpWithCurrencyPair.computeIfAbsent(currencyPair, key -> new HashSet<>()).add(fxpData));
    }

    @Override
    public void save(OracleData oracleData) {

        this.oracleWithId.put(oracleData.oracleId().getId(), oracleData);
        this.oracleWithPartyType.put(oracleData.type().name(), oracleData);
    }

}
