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

package io.mojaloop.core.participant.domain.cache;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.FxpId;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.FxpData;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.PartyIdType;

import java.util.Set;

public interface ParticipantCache {

    void clear();

    void delete(FspId fspId);

    void delete(OracleId oracleId);

    void delete(FxpId fxpId);

    FspData getFspData(FspId fspId);

    FspData getFspData(FspCode fspCode);

    Set<FxpData> getFxRatePairData(Currency sourceCurrency, Currency targetCurrency);

    FxpData getFxpData(FxpId fxpId);

    OracleData getOracleData(OracleId oracleId);

    OracleData getOracleData(PartyIdType partyIdType);

    void save(FspData fspData);

    void save(FxpData fxpData);

    void save(OracleData oracleData);

    class Qualifiers {

        public static final String REDIS = "redis";

        public static final String DEFAULT = REDIS;

    }

    class Keys {

        public static String forFxRatePair(Currency sourceCurrency, Currency targetCurrency) {

            return sourceCurrency.name() + "_" + targetCurrency.name();
        }

    }

    class Names {

        public static final String FSP_WITH_ID = "pcp-fsp-with-id";

        public static final String FSP_WITH_FSP_CODE = "pcp-fsp-with-fsp-code";

        public static final String ORACLE_WITH_ID = "pcp-oracle-with-id";

        public static final String ORACLE_WITH_PARTY_TYPE = "pcp-oracle-with-party-type";

        public static final String FXP_WITH_ID = "pcp-fxp-with-id";

        public static final String FXP_WITH_CURRENCY_PAIR = "pcp-fxp-with-currency-pair";

    }

}
