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

package org.mojave.component.misc.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;
import io.jsonwebtoken.security.SignatureAlgorithm;
import org.mojave.component.misc.jackson.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

public final class Jwt {

    private static final Logger LOGGER = LoggerFactory.getLogger(Jwt.class);

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.createJsonMapper();

    private Jwt() { }

    public static String decode(String base64Url) {

        byte[] decoded = DECODER.decode(base64Url);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    public static String encode(String json) {

        return ENCODER.encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    public static Token sign(PrivateKey privateKey, Map<String, String> headers, String payload) {

        return Jwt.sign(privateKey, Jwts.SIG.RS256, headers, payload);
    }

    public static Token sign(PrivateKey privateKey,
                             SignatureAlgorithm alg,
                             Map<String, String> headers,
                             String payload) {

        assert privateKey != null;
        assert headers != null;
        assert payload != null;

        String token = Jwts
                           .builder()
                           .signWith(privateKey, alg)
                           .header()
                           .add("typ", "JWT")
                           .add(headers)
                           .and()
                           .content(payload, "application/json")
                           .compact();

        String[] parts = token.split("\\.");

        return new Jwt.Token(parts[0], parts[1], parts[2], token);
    }

    public static boolean verify(SecretKey secretKey, String token, String payload) {

        try {

            var content = new String(
                Jwts.parser().verifyWith(secretKey).build().parseSignedContent(token).getPayload(),
                StandardCharsets.UTF_8);

            return content.equals(payload);

        } catch (Exception e) {

            LOGGER.error("Error : {0}", e);
            return false;
        }
    }

    public static boolean verify(PublicKey publicKey, Token token) {

        try {

            assert token != null;
            LOGGER.debug("Verifying token : {}", token.full());

            Jwts.parser().verifyWith(publicKey).build().parseSignedContent(token.full());

            return true;

        } catch (Exception e) {

            LOGGER.error("Error : {0}", e);
            return false;
        }
    }

    public record Token(String header, String body, String signature, String full) {

        public Token(String header, String body, String signature, String full) {

            assert header != null;
            assert body != null;
            assert signature != null;
            assert full != null;

            this.header = header;
            this.body = body;
            this.signature = signature;
            this.full = full;
        }

        public Token(String header, String body, String signature) {

            this(header, body, signature, header + "." + body + "." + signature);
        }

    }

}
