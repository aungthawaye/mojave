package io.mojaloop.connector.gateway.component;

import io.mojaloop.fspiop.spec.core.PartyIdType;

public class PubSubKeys {

    public static String forParties(String fsp, PartyIdType partyIdType, String partyId, String subId) {

        return "parties:" + fsp + "-" + partyId + "-" + partyIdType + (subId != null && !subId.isBlank() ? "-" + subId : "");
    }

    public static String forPartiesError(String fsp, PartyIdType partyIdType, String partyId, String subId) {

        return "parties-error:" + fsp + "-" + partyId + "-" + partyIdType + (subId != null && !subId.isBlank() ? "-" + subId : "");
    }

    public static String forQuotes(String fsp, String quoteId) {

        return "quotes:" + fsp + "-" + quoteId;
    }

    public static String forQuotesError(String fsp, String quoteId) {

        return "quotes-error:" + fsp + "-" + quoteId;
    }

    public static String forTransfers(String fsp, String transferId) {

        return "transfers:" + fsp + "-" + transferId;
    }

    public static String forTransfersError(String fsp, String transferId) {

        return "transfers-error:" + fsp + "-" + transferId;
    }

}
