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

package io.mojaloop.fspiop.service.component;

import io.mojaloop.component.web.request.CachedServletRequest;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public record FspiopHttpRequest(Source source,
                                Destination destination,
                                String method,
                                String uri,
                                String contentType,
                                Map<String, String> headers,
                                Map<String, String> params,
                                String payload) {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopHttpRequest.class);

    public static FspiopHttpRequest with(HttpServletRequest request) throws IOException {

        var cachedRequest = request instanceof CachedServletRequest cachedRequest1 ? cachedRequest1 : new CachedServletRequest(request);

        var source = new Source(cachedRequest.getHeader(FspiopHeaders.Names.FSPIOP_SOURCE));
        LOGGER.debug("Source: [{}]", source);

        var destinationHeader = cachedRequest.getHeader(FspiopHeaders.Names.FSPIOP_DESTINATION);
        var destination = destinationHeader == null || destinationHeader.isEmpty() ? Destination.EMPTY() :
                              new Destination(cachedRequest.getHeader(FspiopHeaders.Names.FSPIOP_DESTINATION));
        LOGGER.debug("Destination: [{}]", destination);

        var method = cachedRequest.getMethod();
        LOGGER.debug("Method: [{}]", method);

        var uri = cachedRequest.getRequestURI();
        LOGGER.debug("URI: [{}]", uri);

        var contentType = cachedRequest.getContentType();
        LOGGER.debug("Content-Type: [{}]", contentType);

        var headers = new HashMap<String, String>();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.put(headerName, request.getHeader(headerName));
        });
        LOGGER.debug("Headers: [{}]", headers);

        var params = new HashMap<String, String>();
        request.getParameterMap().forEach((k, v) -> params.put(k, v[0]));
        LOGGER.debug("Params: [{}]", params);

        var payload = cachedRequest.getCachedBodyAsString();
        LOGGER.debug("Payload: [{}]", payload);

        return new FspiopHttpRequest(source, destination, method, uri, contentType, headers, params, payload);
    }

    public boolean hasPayload() {

        if (this.payload == null) {
            return false;
        }

        return !this.payload.isEmpty();
    }

}
