package io.mojaloop.connector.adapter.fsp.payload;

import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.PartyPersonalInfo;

import java.util.List;

public class Parties {

    public static class Get {

        public record Request(PartyIdType partyIdType, String partyId, String subId) { }

        public record Response(List<Currency> supportedCurrencies, String name, PartyPersonalInfo personalInfo) { }

    }

}
