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

package io.mojaloop.fspiop.component.handy;

import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;

import java.text.SimpleDateFormat;
import java.time.Instant;
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

    private static final ThreadLocal<SimpleDateFormat> FOR_DATE_OF_BIRTH = ThreadLocal.withInitial(() -> {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf;
    });

    public static String forDateOfBirth(Date date) {

        return FOR_DATE_OF_BIRTH.get().format(date);
    }

    public static String forRequestBody() {

        return forRequestBody(new Date());
    }

    public static String forRequestBody(Date date) {

        return FOR_BODY.get().format(date);
    }

    public static String forRequestBody(Instant instant) {

        return FOR_BODY.get().format(Date.from(instant));
    }

    public static String forRequestHeader() {

        return forRequestHeader(new Date());
    }

    public static String forRequestHeader(Date date) {

        return FOR_HEADER.get().format(date);
    }

    public static String forRequestHeader(Instant instant) {

        return FOR_HEADER.get().format(Date.from(instant));
    }

    public static Instant fromRequestBody(String body) throws FspiopException {

        try {
            return FOR_BODY.get().parse(body).toInstant();
        } catch (Exception e) {
            throw new FspiopException(
                FspiopErrors.GENERIC_VALIDATION_ERROR, "Error parsing date from request body.");
        }
    }

    public static Instant fromRequestHeader(String header) throws FspiopException {

        try {
            return FOR_HEADER.get().parse(header).toInstant();
        } catch (Exception e) {
            throw new FspiopException(
                FspiopErrors.GENERIC_VALIDATION_ERROR, "Error parsing date from request header.");
        }
    }

}