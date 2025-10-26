package io.mojaloop.component.misc.handy;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class EpochSecondIndex {

    public static int of(int hour, int minute, ZoneId zoneId) {

        var current = ZonedDateTime.now(zoneId);

        return of(current.withHour(hour).withMinute(minute).withSecond(0).withNano(0));
    }

    public static int of(ZonedDateTime zdt) {

        return zdt.withZoneSameInstant(ZoneOffset.UTC).toLocalTime().toSecondOfDay();
    }

}
