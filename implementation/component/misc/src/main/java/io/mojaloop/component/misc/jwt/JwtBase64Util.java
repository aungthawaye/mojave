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
package io.mojaloop.component.misc.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtBase64Util {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    /**
     * Add padding if missing (for decoding compatibility).
     * Useful if decoder library requires correct padding.
     *
     * @param base64Url unpadded string
     * @return padded base64url string
     */
    public static String addPadding(String base64Url) {

        int remainder = base64Url.length() % 4;
        if (remainder == 0) {
            return base64Url;
        }
        return base64Url + "=".repeat(4 - remainder);
    }

    /**
     * Decode a Base64Url-encoded string to its original JSON.
     *
     * @param base64Url the base64url-encoded string
     * @return decoded JSON string
     */
    public static String decode(String base64Url) {

        byte[] decoded = DECODER.decode(base64Url);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    /**
     * Encode a JSON string to Base64Url (no padding).
     *
     * @param json the JSON string to encode
     * @return base64url-encoded string without padding
     */
    public static String encode(String json) {

        return ENCODER.encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Remove trailing padding '=' from a Base64Url string.
     *
     * @param base64Url padded base64url string
     * @return unpadded base64url string
     */
    public static String removePadding(String base64Url) {

        return base64Url.replaceAll("=+$", "");
    }

}
