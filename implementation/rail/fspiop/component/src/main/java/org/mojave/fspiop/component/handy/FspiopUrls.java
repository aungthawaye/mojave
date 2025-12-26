/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.fspiop.component.handy;

import org.mojave.specification.fspiop.core.PartyIdType;

public class FspiopUrls {

    public static String newUrl(String baseUrl, String existingUri) {

        var requireSeparator = !baseUrl.endsWith("/");
        var newUri = existingUri.startsWith("/") ? existingUri.substring(1) : existingUri;

        return requireSeparator ? baseUrl + "/" + newUri : baseUrl + newUri;
    }

    public static class Parties {

        public static String getParties(String baseUrl, PartyIdType partyIdType, String partyId) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "parties/" + partyIdType + "/" +
                       partyId;
        }

        public static String getParties(String baseUrl,
                                        PartyIdType partyIdType,
                                        String partyId,
                                        String subId) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "parties/" + partyIdType + "/" +
                       partyId + "/" + subId;
        }

        public static String putParties(String baseUrl, PartyIdType partyIdType, String partyId) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "parties/" + partyIdType + "/" +
                       partyId;
        }

        public static String putParties(String baseUrl,
                                        PartyIdType partyIdType,
                                        String partyId,
                                        String subId) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "parties/" + partyIdType + "/" +
                       partyId + "/" + subId;
        }

        public static String putPartiesError(String baseUrl,
                                             PartyIdType partyIdType,
                                             String partyId,
                                             String subId) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "parties/" + partyIdType + "/" +
                       partyId + (subId != null ? "/" + subId : "") + "/error";
        }

    }

    public static class Quotes {

        public static String getQuotes(String baseUrl, String quoteId) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "quotes/" + quoteId;
        }

        public static String postQuotes(String baseUrl) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "quotes";
        }

        public static String putQuotes(String baseUrl, String quoteId) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "quotes/" + quoteId;
        }

        public static String putQuotesError(String baseUrl, String quoteId) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "quotes/" + quoteId + "/error";
        }

    }

    public static class Transfers {

        public static String getTransfers(String baseUrl, String transferId) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "transfers/" + transferId;
        }

        public static String patchTransfers(String baseUrl, String transferId) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "transfers/" + transferId;
        }

        public static String postTransfers(String baseUrl) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "transfers";
        }

        public static String putTransfers(String baseUrl, String transferId) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "transfers/" + transferId;
        }

        public static String putTransfersError(String baseUrl, String transferId) {

            return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "transfers/" + transferId +
                       "/error";
        }

    }

}
