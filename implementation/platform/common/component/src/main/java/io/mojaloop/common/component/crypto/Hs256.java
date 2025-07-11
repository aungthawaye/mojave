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
package io.mojaloop.common.component.crypto;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Hs256 {

    public static String signature(String secret, byte[] message) {

        byte[] hmacSha256;
        byte[] keyBytes = DigestUtils.sha256(secret);

        try {

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
            mac.init(secretKeySpec);

            hmacSha256 = mac.doFinal(message);

        } catch (Exception e) {

            throw new RuntimeException("Failed to calculate hmac-sha256", e);

        }

        return DigestUtils.sha256Hex(hmacSha256);

    }

    public static boolean verify(String secret, String signature, byte[] payload) {

        return Hs256.signature(secret, payload).equals(signature);
    }

}
