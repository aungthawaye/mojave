package org.mojave.component.misc.handy;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TimeIndexUT {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeIndexUT.class);

    @Test
    public void test(){

        var now = TimeIndex.of(ZonedDateTime.now(ZoneOffset.UTC));
        var time = TimeIndex.toInstant(now);
        LOGGER.info("{}", now);
        LOGGER.info("{}", time);

        now = TimeIndex.of(15, 0, ZoneId.of("GMT+06:30"));
        time = TimeIndex.toInstant(now);
        LOGGER.info("{}", now);
        LOGGER.info("{}", time);

        now = TimeIndex.of(16, 30, ZoneId.of("GMT+08:00"));
        time = TimeIndex.toInstant(now);
        LOGGER.info("{}", now);
        LOGGER.info("{}", time);

    }
}
