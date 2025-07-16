package io.mojaloop.core.participant.domain.cache;

import io.mojaloop.common.component.redis.RedissonOpsClient;
import io.mojaloop.common.datatype.identifier.participant.FspId;
import io.mojaloop.common.datatype.identifier.participant.FxpId;
import io.mojaloop.common.datatype.identifier.participant.OracleId;
import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.model.core.Currency;
import io.mojaloop.common.fspiop.model.core.PartyIdType;
import io.mojaloop.core.participant.contract.cache.ParticipantCache;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.FxpData;
import io.mojaloop.core.participant.contract.data.OracleData;
import org.redisson.api.RMap;
import org.redisson.api.RSetMultimap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Qualifier(ParticipantCache.Qualifiers.REDIS)
public class ParticipantRedisCache implements ParticipantCache {

    private final RMap<Long, FspData> fspWithId;

    private final RMap<String, FspData> fspWithFspCode;

    private final RMap<Long, OracleData> oracleWithId;

    private final RMap<String, OracleData> oracleWithPartyType;

    private final RMap<Long, FxpData> fxpWithId;

    private final RSetMultimap<String, FxpData> fxpWithCurrencyPair;

    public ParticipantRedisCache(RedissonOpsClient redissonOpsClient) {

        assert redissonOpsClient != null;

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
        this.fspWithFspCode.remove(deleted.fspCode().getFspCode());
    }

    @Override
    public void delete(OracleId oracleId) {

        var deleted = this.oracleWithId.remove(oracleId.getId());
        this.oracleWithPartyType.remove(deleted.type().name());
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

        return this.fspWithFspCode.get(fspCode.getFspCode());
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

    @Override
    public void save(FspData fspData) {

        this.fspWithId.put(fspData.fspId().getId(), fspData);
        this.fspWithFspCode.put(fspData.fspCode().getFspCode(), fspData);
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
