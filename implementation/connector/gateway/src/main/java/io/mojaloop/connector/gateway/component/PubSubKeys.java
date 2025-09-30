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

    public static String forQuotes(Payee payee, String quoteId) {

        return "quotes:" + payee.fspCode() + "-" + quoteId;
    }

    public static String forQuotesError(Payee payee, String quoteId) {

        return "quotes-error:" + payee.fspCode() + "-" + quoteId;
    }

    public static String forTransfers(Payee payee, String transferId) {

        return "transfers:" + payee.fspCode() + "-" + transferId;
    }

    public static String forTransfersError(Payee payee, String transferId) {

        return "transfers-error:" + payee.fspCode() + "-" + transferId;
    }

}
