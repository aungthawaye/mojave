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
package io.mojaloop.common.component.http;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

public class HttpRequestContext {

    private static final ThreadLocal<ContentCachingRequestWrapper> REQUEST_WRAPPER = new ThreadLocal<>();

    public static ContentCachingRequestWrapper getRequestWrapper() {

        return REQUEST_WRAPPER.get();
    }

    public static void setRequestWrapper(HttpServletRequest request) {

        if (request instanceof ContentCachingRequestWrapper wrapper) {

            REQUEST_WRAPPER.set(wrapper);

        } else {

            var wrapper = new ContentCachingRequestWrapper(request);
            wrapper.getParameterMap();
            REQUEST_WRAPPER.set(wrapper);

        }
    }

}
