package io.mojaloop.core.participant.store.cache;

import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.store.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class ParticipantStoreUT {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantStoreUT.class);

    @Autowired
    private ParticipantStore participantStore;

    @Test
    public void test() {

        var fspData = this.participantStore.getFspData(new FspCode("fsp1"));

        LOGGER.info("fspCode : [{}]", fspData.fspId());
        LOGGER.info("fspCode : [{}]", fspData.fspCode());
        LOGGER.info("name : [{}]", fspData.name());

        for (var currency : fspData.supportedCurrencies()) {
            LOGGER.info("Currency: {}", currency);
        }

        for (var endpoint : fspData.endpoints().entrySet()) {
            LOGGER.info("Endpoint: {} - {}", endpoint.getKey(), endpoint.getValue());
        }
    }

}
