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
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public record FspiopHttpRequest(String uri, Map<String, String> headers, Map<String, String> params, String payload) {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopHttpRequest.class);

    public static FspiopHttpRequest with(HttpServletRequest request) throws IOException {

        var cachedRequest = request instanceof CachedServletRequest cachedRequest1 ? cachedRequest1 : new CachedServletRequest(request);

        var uri = cachedRequest.getRequestURI();
        LOGGER.debug("URI: [{}]", uri);

        var headers = new HashMap<String, String>();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.put(headerName, request.getHeader(headerName));
        });
        LOGGER.debug("Headers: [{}]", headers);

        var paramMap = request.getParameterMap();
        var params = new HashMap<String, String>();

        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {

            String key = entry.getKey();
            String[] values = entry.getValue();

            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        }

        LOGGER.debug("Params: [{}]", params);

        var payload = cachedRequest.getCachedBodyAsString();
        LOGGER.debug("Payload: [{}]", payload);

        return new FspiopHttpRequest(uri, headers, params, payload);
    }

    public boolean hasPayload() {

        if (this.payload == null) {
            return false;
        }

        return !this.payload.isEmpty();
    }

}
