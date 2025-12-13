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
package org.mojave.component.misc.crypto;

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
