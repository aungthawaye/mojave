package io.mojaloop.component.misc.crypto;

import java.util.Base64;

public final class Pem {

    private static final Base64.Encoder MIME_ENCODER = Base64.getMimeEncoder(64, new byte[]{'\n'});

    private static final Base64.Decoder MIME_DECODER = Base64.getMimeDecoder();

    private static final String BEGIN_MARKER = "-----BEGIN";

    private static final String END_MARKER = "-----END";

    private static final String MARKER = "-----";

    private Pem() {

    }

    public static byte[] from(String pem) {

        final var BEGIN_LENGTH = BEGIN_MARKER.length();

        pem = pem.startsWith(BEGIN_MARKER) ?
                  pem.substring(BEGIN_LENGTH - 1, pem.indexOf(END_MARKER)) : pem;
        pem = pem.substring(pem.indexOf(MARKER) + MARKER.length());

        pem = pem.replaceAll("\n", "");

        return MIME_DECODER.decode(pem);
    }

    public static String to(byte[] content, String title) {

        assert content != null;

        String base64 = MIME_ENCODER.encodeToString(content);
        String normalizedTitle = title == null ? null : title.trim();

        if (normalizedTitle == null || normalizedTitle.isEmpty()) {
            return base64;
        }

        return BEGIN_MARKER + " " + normalizedTitle + "-----" + System.lineSeparator() + base64 +
                   System.lineSeparator() + END_MARKER + " " + normalizedTitle + "-----" +
                   System.lineSeparator();

    }

}
