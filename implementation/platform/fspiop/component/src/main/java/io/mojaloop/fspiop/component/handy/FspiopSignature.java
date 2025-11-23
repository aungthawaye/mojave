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

package io.mojaloop.fspiop.component.handy;

import io.mojaloop.component.misc.jwt.Rs256Jwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

public class FspiopSignature {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopSignature.class);

    public static Header sign(PrivateKey privateKey, Map<String, String> headers, String payload) {

        var token = Rs256Jwt.sign(privateKey, headers, payload);
        LOGGER.debug("Signed Token: ({})", token);

        return new Header(token.signature(), token.header());
    }

    public static boolean verify(PublicKey publicKey, Rs256Jwt.Token token) {

        return Rs256Jwt.verify(publicKey, token);
    }

    public record Header(String signature, String protectedHeader) { }

}
