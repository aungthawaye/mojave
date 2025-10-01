package io.mojaloop.connector.gateway.component;

import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.spec.core.PartyIdType;

public class PubSubKeys {

    public static String forParties(Payee payee, PartyIdType partyIdType, String partyId, String subId) {

        return "parties:" + payee.fspCode() + "-" + partyId + "-" + partyIdType + (subId != null && !subId.isBlank() ? "-" + subId : "");
    }

    public static String forPartiesError(Payee payee, PartyIdType partyIdType, String partyId, String subId) {

        return "parties-error:" + payee.fspCode() + "-" + partyId + "-" + partyIdType + (subId != null && !subId.isBlank() ? "-" + subId : "");
    }

    public static String forQuotes(String quoteId) {

        return "quotes:" + quoteId;
    }

    public static String forQuotesError(String quoteId) {

        return "quotes-error:" + quoteId;
    }

    public static String forTransfers(String transferId) {

        return "transfers:" + transferId;
    }

    public static String forTransfersError(Payee payee, String transferId) {

        return "transfers-error:" + transferId;
    }

}
