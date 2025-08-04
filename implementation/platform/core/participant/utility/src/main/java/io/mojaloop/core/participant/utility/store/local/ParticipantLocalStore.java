package io.mojaloop.core.participant.utility.store.local;

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.utility.ParticipantUtilityConfiguration;
import io.mojaloop.core.participant.utility.client.ParticipantClient;
import io.mojaloop.core.participant.utility.store.ParticipantStore;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ParticipantLocalStore implements ParticipantStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantLocalStore.class);

    private final ParticipantClient participantClient;

    private final ParticipantUtilityConfiguration.Settings settings;

    private final Map<FspId, FspData> withFspId = new ConcurrentHashMap<>();

    private final Map<FspCode, FspData> withFspCode = new ConcurrentHashMap<>();

    private final Map<OracleId, OracleData> withOracleId = new ConcurrentHashMap<>();

    private final Map<PartyIdType, OracleData> withPartyIdType = new ConcurrentHashMap<>();

    private final Timer timer = new Timer("ParticipantLocalStoreRefreshTimer", true);

    public ParticipantLocalStore(ParticipantClient participantClient, ParticipantUtilityConfiguration.Settings settings) {

        assert participantClient != null;
        assert settings != null;

        this.participantClient = participantClient;
        this.settings = settings;
    }

    @PostConstruct
    public void bootstrap() {

        var interval = this.settings.refreshIntervalMs();

        LOGGER.info("Bootstrapping ParticipantLocalStore");
        this.refreshData();

        // Schedule a timer to refresh data every 30 seconds
        this.timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                ParticipantLocalStore.this.refreshData();
            }
        }, interval, interval); // 30 seconds in milliseconds
    }

    @Override
    public FspData getFspData(FspId fspId) {

        if (fspId == null) {
            return null;
        }

        return this.withFspId.get(fspId);
    }

    @Override
    public FspData getFspData(FspCode fspCode) {

        if (fspCode == null) {
            return null;
        }

        return this.withFspCode.get(fspCode);
    }

    @Override
    public OracleData getOracleData(OracleId oracleId) {

        if (oracleId == null) {
            return null;
        }

        return this.withOracleId.get(oracleId);
    }

    @Override
    public OracleData getOracleData(PartyIdType partyIdType) {

        if (partyIdType == null) {
            return null;
        }

        return this.withPartyIdType.get(partyIdType);
    }

    private void refreshData() {

        try {

            LOGGER.info("Start refreshing participant data");

            // Fetch all FSPs and populate maps
            List<FspData> fsps = this.participantClient.getFsps();

            // Clear existing maps before populating
            this.withFspId.clear();
            this.withFspCode.clear();

            for (FspData fsp : fsps) {
                this.withFspId.put(fsp.fspId(), fsp);
                this.withFspCode.put(fsp.fspCode(), fsp);
            }

            LOGGER.info("Refreshed FSP data, count: {}", fsps.size());

            // Fetch all Oracles and populate maps
            List<OracleData> oracles = this.participantClient.getOracles();

            // Clear existing maps before populating
            this.withOracleId.clear();
            this.withPartyIdType.clear();

            for (OracleData oracle : oracles) {
                this.withOracleId.put(oracle.oracleId(), oracle);
                this.withPartyIdType.put(oracle.type(), oracle);
            }

            LOGGER.info("Refreshed Oracle data, count: {}", oracles.size());

        } catch (RetrofitService.InvocationException e) {
            LOGGER.error("Error refreshing participant data. Will try again later.", e);
        }
    }

}
