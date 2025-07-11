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
package io.mojaloop.common.component.http.jwt;

import io.jsonwebtoken.Jwts;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class Hs256Jwt {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hs256Jwt.class);

    private Hs256Jwt() { }

    public static SecretKey secretKey(String secretKey) {

        return new SecretKeySpec(DigestUtils.sha256(secretKey), 0, 32, "HMACSHA256");
    }

    public static Token sign(SecretKey secretKey, Map<String, ?> headers, String payload) {

        if (payload.isEmpty()) {
            payload = "{}";
        }

        String token = Jwts
                           .builder()
                           .signWith(secretKey)
                           .header()
                           .add("typ", "JWT")
                           .add(headers)
                           .and()
                           .content(payload, "application/json")
                           .compact();

        String[] parts = token.split("\\.");

        return new Token(parts[0], parts[1], parts[2], token);
    }

    public static boolean verify(SecretKey secretKey, String token, String payload) {

        try {

            var content = new String(Jwts.parser().verifyWith(secretKey).build().parseSignedContent(token).getPayload(),
                                     StandardCharsets.UTF_8);

            return content.equals(payload);

        } catch (Exception e) {

            LOGGER.error("Error : {0}", e);
            return false;
        }
    }

    public record Token(String header, String body, String signature, String full) { }

}
