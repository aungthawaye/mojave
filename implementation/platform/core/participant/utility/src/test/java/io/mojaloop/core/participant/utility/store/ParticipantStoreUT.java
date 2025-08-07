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

package io.mojaloop.core.participant.utility.store;

import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.utility.TestConfiguration;
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
    public void test() throws InterruptedException {

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

        Thread.sleep(10000);
    }

}
