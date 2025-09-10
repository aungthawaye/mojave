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

package io.mojaloop.fspiop.common.participant;

import io.mojaloop.component.misc.crypto.Rs256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public record ParticipantContext(String fspCode,
                                 String fspName,
                                 String ilpSecret,
                                 boolean signJws,
                                 boolean verifyJws,
                                 PrivateKey signingKey,
                                 Map<String, PublicKey> publicKeys) {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantContext.class);

    public ParticipantContext {

        if (fspCode == null || fspCode.isEmpty()) {
            throw new IllegalArgumentException("FSP Code is required");
        }

        if (fspName == null || fspName.isEmpty()) {
            throw new IllegalArgumentException("FSP Name is required");
        }

        if (ilpSecret == null || ilpSecret.isEmpty()) {
            throw new IllegalArgumentException("ILP Secret is required");
        }

        if (signJws && (signingKey == null)) {
            throw new IllegalArgumentException("Signing Key (Private Key) is required when signing JWS");
        }

        if (verifyJws && (publicKeys == null || publicKeys.isEmpty())) {
            throw new IllegalArgumentException("Verification Key (Public Key) of FSPs are required when verifying JWS");
        }
    }

    public static ParticipantContext with(String fspCode,
                                          String fspName,
                                          String ilpSecret,
                                          boolean signJws,
                                          boolean verifyJws,
                                          String base64PrivateKey,
                                          Map<String, String> base64PublicKeys) throws NoSuchAlgorithmException, InvalidKeySpecException {

        if (fspCode == null || fspCode.isEmpty()) {
            throw new IllegalArgumentException("FSP Code is required");
        }

        if (fspName == null || fspName.isEmpty()) {
            throw new IllegalArgumentException("FSP Name is required");
        }

        if (ilpSecret == null || ilpSecret.isEmpty()) {
            throw new IllegalArgumentException("ILP Secret is required");
        }

        if (signJws && (base64PrivateKey == null || base64PrivateKey.isEmpty())) {
            throw new IllegalArgumentException("Signing Key (Private Key) is required when signing JWS");
        }

        if (verifyJws && (base64PublicKeys == null || base64PublicKeys.isEmpty())) {
            throw new IllegalArgumentException("Verification Key (Public Key) of FSPs are required when verifying JWS");
        }

        var signingKey = Rs256.privateKeyFromPem(base64PrivateKey);

        var publicKeys = new HashMap<String, PublicKey>();

        for (var entry : base64PublicKeys.entrySet()) {

            publicKeys.put(entry.getKey(), Rs256.publicKeyFromPem(entry.getValue()));
            LOGGER.info("FspCode: [{}], PublicKey: [{}]", entry.getKey(), entry.getValue());
        }

        return new ParticipantContext(fspCode, fspName, ilpSecret, signJws, verifyJws, signingKey, publicKeys);
    }

}
