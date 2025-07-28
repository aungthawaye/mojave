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

package io.mojaloop.fspiop.common.component;


import io.mojaloop.component.http.security.Rs256Jwt;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

public class FspiopSignature {

    public static Header sign(PrivateKey privateKey, Map<String, String> headers, String payload) {

        var token = Rs256Jwt.sign(privateKey, headers, payload);

        return new Header(token.signature(), token.header());
    }

    public static boolean verify(PublicKey publicKey, String header, String payload, String signature) {

        return Rs256Jwt.verify(publicKey, header, payload, signature);
    }

    public record Header(String signature, String protectedHeader) { }

}
