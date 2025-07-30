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
package io.mojaloop.core.participant.utility.store.redis;

import io.mojaloop.component.redis.RedissonOpsClient;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.cache.ParticipantCache;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.utility.store.ParticipantStore;
import io.mojaloop.fspiop.spec.core.PartyIdType;
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
