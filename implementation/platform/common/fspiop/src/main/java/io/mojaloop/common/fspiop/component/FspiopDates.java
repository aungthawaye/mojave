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
