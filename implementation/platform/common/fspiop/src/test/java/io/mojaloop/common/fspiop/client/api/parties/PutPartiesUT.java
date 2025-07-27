package io.mojaloop.common.fspiop.client.api.parties;

import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.client.FspiopClientConfiguration;
import io.mojaloop.common.fspiop.client.api.TestSettings;
import io.mojaloop.common.fspiop.model.core.Currency;
import io.mojaloop.common.fspiop.model.core.PartiesTypeIDPutResponse;
import io.mojaloop.common.fspiop.model.core.Party;
import io.mojaloop.common.fspiop.model.core.PartyIdInfo;
import io.mojaloop.common.fspiop.model.core.PartyIdType;
import io.mojaloop.common.fspiop.model.core.PartyPersonalInfo;
import io.mojaloop.common.fspiop.support.Destination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FspiopClientConfiguration.class, TestSettings.class})
public class PutPartiesUT {

    @Autowired
    PutParties putParties;

    @Test
    public void test() {

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

        this.putParties.putParties(new Destination(new FspCode("fsp2")),
                                   PartyIdType.MSISDN,
                                   "987654321",
                                   new PartiesTypeIDPutResponse(party));
    }

}
