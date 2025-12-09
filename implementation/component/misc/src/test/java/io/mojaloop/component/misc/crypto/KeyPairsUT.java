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

package io.mojaloop.component.misc.crypto;

import io.jsonwebtoken.Jwts;
import io.mojaloop.component.misc.jwt.Jwt;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

public class KeyPairsUT {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyPairsUT.class);

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {

        var keyPair = KeyPairs.EdDsa.generate(KeyPairs.EdDsa.Curve.ED25519);

        var privateKeyPem = Pem.to(keyPair.getPrivate().getEncoded(), "PRIVATE KEY");
        var publicKeyPem = Pem.to(keyPair.getPublic().getEncoded(), "PUBLIC KEY");

        LOGGER.info("Private key: {}", privateKeyPem);
        LOGGER.info("Public key: {}", publicKeyPem);

        var privateKey = KeyPairs.EdDsa.privateKeyOf(privateKeyPem, KeyPairs.EdDsa.Curve.ED25519);
        var publicKey = KeyPairs.EdDsa.publicKeyOf(publicKeyPem, KeyPairs.EdDsa.Curve.ED25519);

        var headers = new HashMap<String, String>();
        headers.put("alg", "RS256");
        headers.put("typ", "JWT");

        var payload = "{\"date\":\"Fri, 23 May 2025 02:30:00 GMT\"}";

        Jwt.Token token = null;

        for (int i = 0; i < 10; i++) {

            var startAt = System.nanoTime();
            token = Jwt.sign(privateKey, Jwts.SIG.EdDSA, headers, payload);
            var endAt = System.nanoTime();

            LOGGER.debug("Signing took {} ms", (endAt - startAt) / 1_000_000);
            LOGGER.debug("token : {}", token);
        }

        var encodedPayload = Jwt.encode(payload);
        LOGGER.debug("encodedPayload : {}", encodedPayload);

        var ok = Jwt.verify(
            publicKey, new Jwt.Token(token.header(), encodedPayload, token.signature()));
        LOGGER.debug("ok : {}", ok);
    }

}
