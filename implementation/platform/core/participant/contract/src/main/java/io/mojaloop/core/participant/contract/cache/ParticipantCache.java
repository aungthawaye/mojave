package io.mojaloop.core.participant.contract.cache;

import io.mojaloop.common.datatype.identifier.participant.FspId;
import io.mojaloop.common.datatype.identifier.participant.FxpId;
import io.mojaloop.common.datatype.identifier.participant.OracleId;
import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.model.core.Currency;
import io.mojaloop.common.fspiop.model.core.PartyIdType;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.FxpData;
import io.mojaloop.core.participant.contract.data.OracleData;

import java.util.Set;

public interface ParticipantCache {

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
