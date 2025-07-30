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

package io.mojaloop.fspiop.common.data;

import io.mojaloop.component.misc.crypto.Rs256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public record ParticipantDetails(String fspCode,
                                 String fspName,
                                 String ilpSecret,
                                 boolean signJws,
                                 boolean verifyJws,
                                 PrivateKey signingKey,
                                 Map<String, PublicKey> publicKeys) {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantDetails.class);

    public static ParticipantDetails with(String fspCode,
                                          String fspName,
                                          String ilpSecret,
                                          boolean signJws,
                                          boolean verifyJws,
                                          String base64PrivateKey,
                                          Map<String, String> base64PublicKeys) throws NoSuchAlgorithmException, InvalidKeySpecException {

        var signingKey = Rs256.privateKeyFromBase64(base64PrivateKey);

        var publicKeys = new HashMap<String, PublicKey>();

        for (var entry : base64PublicKeys.entrySet()) {

            publicKeys.put(entry.getKey(), Rs256.publicKeyFromBase64(entry.getValue()));
            LOGGER.info("FspCode: [{}], PublicKey: [{}]", entry.getKey(), entry.getValue());
        }

        return new ParticipantDetails(fspCode, fspName, ilpSecret, signJws, verifyJws, signingKey, publicKeys);
    }

}
