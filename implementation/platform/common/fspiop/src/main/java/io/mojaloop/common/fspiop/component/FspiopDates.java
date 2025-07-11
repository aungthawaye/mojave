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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FspiopDates {

    private static final ThreadLocal<SimpleDateFormat> FOR_HEADER = ThreadLocal.withInitial(() -> {

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return sdf;
    });

    private static final ThreadLocal<SimpleDateFormat> FOR_BODY = ThreadLocal.withInitial(() -> {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return sdf;
    });

    public static String forRequestBody() {

        return forRequestBody(new Date());
    }

    public static String forRequestBody(Date date) {

        return FOR_BODY.get().format(date);
    }

    public static String forRequestHeader() {

        return forRequestHeader(new Date());
    }

    public static String forRequestHeader(Date date) {

        return FOR_HEADER.get().format(date);
    }

}
