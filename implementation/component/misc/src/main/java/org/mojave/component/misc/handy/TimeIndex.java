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

package org.mojave.component.misc.handy;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class TimeIndex {

    public static int of(ZonedDateTime zdt) {

        return zdt.withZoneSameInstant(ZoneOffset.UTC).toLocalTime().toSecondOfDay();
    }

    public static int of(int hour, int minute, ZoneId zoneId) {

        var current = ZonedDateTime.now(zoneId);

        return of(current.withHour(hour).withMinute(minute).withSecond(0).withNano(0));
    }

    public static Instant toInstant(int timeIndex) {

        var _at00 = ZonedDateTime
                        .now(ZoneOffset.UTC)
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

        return _at00.plusSeconds(timeIndex).toInstant();

    }

}
