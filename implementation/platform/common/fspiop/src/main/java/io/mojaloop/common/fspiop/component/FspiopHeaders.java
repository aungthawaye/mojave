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
package io.mojaloop.common.fspiop.component;

import java.util.HashMap;
import java.util.Map;

public class FspiopHeaders {

    public static class Names {

        public static final String ACCEPT = "accept";

        public static final String CONTENT_TYPE = "content-type";

        public static final String DATE = "date";

        public static final String FSPIOP_SOURCE = "fspiop-source";

        public static final String FSPIOP_DESTINATION = "fspiop-destination";

        public static final String FSPIOP_HTTP_METHOD = "fspiop-http-method";

        public static final String FSPIOP_SIGNATURE = "fspiop-signature";

        public static final String FSPIOP_ENCRYPTION = "fspiop-encryption";

        public static final String FSPIOP_URI = "fspiop-uri";

    }

    public static class Values {

        /**
         * IMPORTANT:
         * For sending a request to HUB, we must put an "Accept" header.
         * But, for all the PUT callbacks, we MUST NOT.
         */

        public static class Parties {

            public static final String ACCEPT = "application/vnd.interoperability.parties+json;version=2.0";

            public static final String CONTENT_TYPE = "application/vnd.interoperability.parties+json;version=2.0";

            public static Map<String, String> forRequest(String source, String destination) {

                var headers = new HashMap<String, String>();

                headers.put(Names.ACCEPT, ACCEPT);
                headers.put(Names.CONTENT_TYPE, CONTENT_TYPE);
                headers.put(Names.DATE, FspiopDates.forRequestHeader());
                headers.put(Names.FSPIOP_SOURCE, source);

                if (destination != null) {
                    headers.put(Names.FSPIOP_DESTINATION, destination);
                }

                return headers;
            }

            public static Map<String, String> forCallback(String source, String destination, String signature) {

                var headers = new HashMap<String, String>();

                headers.put(Names.CONTENT_TYPE, CONTENT_TYPE);
                headers.put(Names.DATE, FspiopDates.forRequestHeader());
                headers.put(Names.FSPIOP_SOURCE, source);

                if (destination != null) {
                    headers.put(Names.FSPIOP_DESTINATION, destination);
                }

                return headers;
            }

        }

        public static class Quotes {

            public static final String ACCEPT = "application/vnd.interoperability.quotes+json;version=2.0";

            public static final String CONTENT_TYPE = "application/vnd.interoperability.quotes+json;version=2.0";

            public static Map<String, String> forRequest(String source, String destination) {

                var headers = new HashMap<String, String>();

                headers.put(Names.ACCEPT, ACCEPT);
                headers.put(Names.CONTENT_TYPE, CONTENT_TYPE);
                headers.put(Names.DATE, FspiopDates.forRequestHeader());
                headers.put(Names.FSPIOP_SOURCE, source);

                if (destination != null) {
                    headers.put(Names.FSPIOP_DESTINATION, destination);
                }

                return headers;
            }

            public static Map<String, String> forCallback(String source, String destination) {

                var headers = new HashMap<String, String>();

                headers.put(Names.CONTENT_TYPE, CONTENT_TYPE);
                headers.put(Names.DATE, FspiopDates.forRequestHeader());
                headers.put(Names.FSPIOP_SOURCE, source);

                if (destination != null) {
                    headers.put(Names.FSPIOP_DESTINATION, destination);
                }

                return headers;
            }
        }

        public static class Transfers {

            public static final String ACCEPT = "application/vnd.interoperability.transfers+json;version=2.0";

            public static final String CONTENT_TYPE = "application/vnd.interoperability.transfers+json;version=2.0";

            public static Map<String, String> forRequest(String source, String destination) {

                var headers = new HashMap<String, String>();

                headers.put(Names.ACCEPT, ACCEPT);
                headers.put(Names.CONTENT_TYPE, CONTENT_TYPE);
                headers.put(Names.DATE, FspiopDates.forRequestHeader());
                headers.put(Names.FSPIOP_SOURCE, source);

                if (destination != null) {
                    headers.put(Names.FSPIOP_DESTINATION, destination);
                }

                return headers;
            }

            public static Map<String, String> forCallback(String source, String destination) {

                var headers = new HashMap<String, String>();

                headers.put(Names.ACCEPT, ACCEPT);
                headers.put(Names.CONTENT_TYPE, CONTENT_TYPE);
                headers.put(Names.DATE, FspiopDates.forRequestHeader());
                headers.put(Names.FSPIOP_SOURCE, source);

                if (destination != null) {
                    headers.put(Names.FSPIOP_DESTINATION, destination);
                }

                return headers;
            }
        }

        public static class Participants {

            public static final String ACCEPT = "application/vnd.interoperability.participants+json;version=2.0";

            public static final String CONTENT_TYPE = "application/vnd.interoperability.participants+json;version=2.0";

            public static Map<String, String> forRequest(String source, String destination) {

                var headers = new HashMap<String, String>();

                headers.put(Names.ACCEPT, ACCEPT);
                headers.put(Names.CONTENT_TYPE, CONTENT_TYPE);
                headers.put(Names.DATE, FspiopDates.forRequestHeader());
                headers.put(Names.FSPIOP_SOURCE, source);

                if (destination != null) {
                    headers.put(Names.FSPIOP_DESTINATION, destination);
                }

                return headers;
            }

            public static Map<String, String> forCallback(String source, String destination) {

                var headers = new HashMap<String, String>();

                headers.put(Names.ACCEPT, ACCEPT);
                headers.put(Names.CONTENT_TYPE, CONTENT_TYPE);
                headers.put(Names.DATE, FspiopDates.forRequestHeader());
                headers.put(Names.FSPIOP_SOURCE, source);

                if (destination != null) {
                    headers.put(Names.FSPIOP_DESTINATION, destination);
                }

                return headers;
            }
        }


    }

}
