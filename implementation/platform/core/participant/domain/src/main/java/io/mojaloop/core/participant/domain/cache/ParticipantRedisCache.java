package io.mojaloop.core.participant.domain.cache;

import io.mojaloop.common.component.redis.RedisOpsClient;
import io.mojaloop.common.datatype.identifier.participant.FspId;
import io.mojaloop.common.datatype.identifier.participant.FxpId;
import io.mojaloop.common.datatype.identifier.participant.OracleId;
import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.cache.ParticipantCache;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.FxpData;
import io.mojaloop.core.participant.contract.data.OracleData;
import org.redisson.api.RMap;
import org.redisson.api.RSetMultimap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(ParticipantCache.Qualifiers.REDIS)
public class ParticipantRedisCache implements ParticipantCache {

    private final RMap<Long, FspData> fspWithId;

    private final RMap<String, FspData> fspWithFspCode;

    private final RMap<Long, OracleData> oracleWithId;

    private final RMap<String, OracleData> oracleWithPartyType;

    private final RMap<Long, FxpData> fxpWithId;

    private final RSetMultimap<String, FxpData> fxpWithCurrencyPair;

    public ParticipantRedisCache(RedisOpsClient redisOpsClient) {

        assert redisOpsClient != null;

        var redissonClient = redisOpsClient.getRedissonClient();

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

        this.oracleWithId.remove(oracleId.getId());
    }

    @Override
    public void delete(FxpId fxpId) {

        var deleted = this.fxpWithId.remove(fxpId.getId());
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
    public FxpData getFxpData(FxpId fxpId) {

        return this.fxpWithId.get(fxpId.getId());
    }

    @Override
    public OracleData getOracleData(OracleId oracleId) {

        return this.oracleWithId.get(oracleId.getId());
    }

    @Override
    public void save(FspData fspData) {

        this.fspWithId.put(fspData.fspId().getId(), fspData);
        this.fspWithFspCode.put(fspData.fspCode().getFspCode(), fspData);
    }

    @Override
    public void save(FxpData fxpData) {

    }

    @Override
    public void save(OracleData oracleData) {

    }

}
