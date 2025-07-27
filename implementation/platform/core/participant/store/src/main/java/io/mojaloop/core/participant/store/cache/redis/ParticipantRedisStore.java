package io.mojaloop.core.participant.store.cache.redis;

import io.mojaloop.common.component.redis.RedissonOpsClient;
import io.mojaloop.common.datatype.identifier.participant.FspId;
import io.mojaloop.common.datatype.identifier.participant.OracleId;
import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.model.core.PartyIdType;
import io.mojaloop.core.participant.contract.cache.ParticipantCache;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.store.cache.ParticipantStore;
import org.redisson.api.RMap;
import org.springframework.stereotype.Component;

@Component
public class ParticipantRedisStore implements ParticipantStore {

    private final RMap<Long, FspData> fspWithId;

    private final RMap<String, FspData> fspWithFspCode;

    private final RMap<Long, OracleData> oracleWithId;

    private final RMap<String, OracleData> oracleWithPartyType;

    public ParticipantRedisStore(RedissonOpsClient redissonOpsClient) {

        assert redissonOpsClient != null;

        var redissonClient = redissonOpsClient.getRedissonClient();

        this.fspWithId = redissonClient.getMap(ParticipantCache.Names.FSP_WITH_ID);
        this.fspWithFspCode = redissonClient.getMap(ParticipantCache.Names.FSP_WITH_FSP_CODE);

        this.oracleWithId = redissonClient.getMap(ParticipantCache.Names.ORACLE_WITH_ID);
        this.oracleWithPartyType = redissonClient.getMap(ParticipantCache.Names.ORACLE_WITH_PARTY_TYPE);

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
    public OracleData getOracleData(OracleId oracleId) {

        return this.oracleWithId.get(oracleId.getId());
    }

    @Override
    public OracleData getOracleData(PartyIdType partyIdType) {

        return this.oracleWithPartyType.get(partyIdType.name());
    }

}
