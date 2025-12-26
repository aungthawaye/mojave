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
package org.mojave.fspiop.service.component;

import jakarta.servlet.http.HttpServletRequest;
import org.mojave.component.web.request.CachedServletRequest;
import org.mojave.fspiop.component.type.Payee;
import org.mojave.fspiop.component.type.Payer;
import org.mojave.fspiop.component.handy.FspiopHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public record FspiopHttpRequest(Payer payer,
                                Payee payee,
                                String method,
                                String uri,
                                String contentType,
                                Map<String, String> headers,
                                Map<String, String> params,
                                String payload) {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopHttpRequest.class);

    public static FspiopHttpRequest with(HttpServletRequest request) throws IOException {

        var cachedRequest = request instanceof CachedServletRequest already ? already :
                                new CachedServletRequest(request);

        var method = cachedRequest.getMethod();
        LOGGER.debug("Method: ({})", method);

        var payerHeader = switch (method) {
            case "POST", "PATCH", "GET" -> FspiopHeaders.Names.FSPIOP_SOURCE;
            case "PUT" -> FspiopHeaders.Names.FSPIOP_DESTINATION;
            default -> throw new IllegalStateException("Unexpected value: " + method);
        };

        var payeeHeader = switch (method) {
            case "POST", "PATCH", "GET" -> FspiopHeaders.Names.FSPIOP_DESTINATION;
            case "PUT" -> FspiopHeaders.Names.FSPIOP_SOURCE;
            default -> throw new IllegalStateException("Unexpected value: " + method);
        };

        var payer = new Payer(cachedRequest.getHeader(payerHeader));
        LOGGER.debug("Payer: ({})", payer);

        var payee = new Payee(cachedRequest.getHeader(payeeHeader));
        LOGGER.debug("Payee: ({})", payee);

        var uri = cachedRequest.getRequestURI();
        LOGGER.debug("URI: ({})", uri);

        var contentType = cachedRequest.getContentType();
        LOGGER.debug("Content-Type: ({})", contentType);

        var headers = new HashMap<String, String>();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.put(headerName, request.getHeader(headerName));
        });
        LOGGER.debug("Headers: ({})", headers);

        var params = new HashMap<String, String>();
        request.getParameterMap().forEach((k, v) -> params.put(k, v[0]));
        LOGGER.debug("Params: ({})", params);

        var payload = cachedRequest.getCachedBodyAsString();
        LOGGER.debug("Payload: ({})", payload);

        return new FspiopHttpRequest(
            payer, payee, method, uri, contentType, headers, params, payload);
    }

    public boolean hasPayload() {

        if (this.payload == null) {
            return false;
        }

        return !this.payload.isEmpty();
    }

}
