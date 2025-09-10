package io.mojaloop.connector.adapter.fsp.payload;

import io.mojaloop.fspiop.spec.core.ExtensionList;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.TransferState;

public class Transfers {

    public static class Post {

        public record Request(String transferId, String quoteId, PartyIdInfo payer, PartyIdInfo payee, Money transferAmount, ExtensionList extensionList) { }

        public record Response(String homeTransactionId) { }

    }

    public static class Patch {

        public record Request(String transferId, TransferState transferState, String completedTimestamp, ExtensionList extensionList) { }

    }

}
