/*-
 * ================================================================================
 * Mojave
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

    public static String forTransfersError(String transferId) {

        return "transfers-error:" + transferId;
    }

}
