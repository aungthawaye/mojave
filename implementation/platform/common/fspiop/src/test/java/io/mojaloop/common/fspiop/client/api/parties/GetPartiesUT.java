package io.mojaloop.common.fspiop.client.api.parties;

import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.client.FspiopClientConfiguration;
import io.mojaloop.common.fspiop.client.api.TestSettings;
import io.mojaloop.common.fspiop.model.core.PartyIdType;
import io.mojaloop.common.fspiop.support.Destination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FspiopClientConfiguration.class, TestSettings.class})
public class GetPartiesUT {

    @Autowired
    GetParties getParties;

    @Test
    public void test() {

        this.getParties.getParties(new Destination(new FspCode("fsp2")), PartyIdType.MSISDN, "987654321");
    }

}
