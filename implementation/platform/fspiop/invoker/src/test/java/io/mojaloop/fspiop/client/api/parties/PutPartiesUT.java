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

package io.mojaloop.fspiop.client.api.parties;

import io.mojaloop.fspiop.client.api.TestSettings;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import io.mojaloop.fspiop.invoker.api.parties.PutParties;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.Party;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.PartyPersonalInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FspiopInvokerConfiguration.class, TestSettings.class})
public class PutPartiesUT {

    @Autowired
    PutParties putParties;

    @Test
    public void test() throws FspiopException {

        var party = new Party();
        party.setName("ATA");
        party.setSupportedCurrencies(List.of(Currency.USD, Currency.EUR, Currency.GBP));

        var partyPersonalInfo = new PartyPersonalInfo();
        partyPersonalInfo.setDateOfBirth("1990-01-01");
        partyPersonalInfo.setKycInformation("12/TaMaNa(N)123456");
        party.setPersonalInfo(partyPersonalInfo);

        var partyIdInfo = new PartyIdInfo();
        partyIdInfo.fspId("fsp1");
        partyIdInfo.partyIdType(PartyIdType.MSISDN);
        partyIdInfo.partyIdentifier("0987654321");
        party.setPartyIdInfo(partyIdInfo);

        this.putParties.putParties(new Destination("fsp2"), PartyIdType.MSISDN, "987654321", new PartiesTypeIDPutResponse(party));
    }

}
