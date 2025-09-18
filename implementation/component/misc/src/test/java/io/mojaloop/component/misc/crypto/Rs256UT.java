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
package io.mojaloop.component.misc.crypto;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;

public class Rs256UT {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rs256UT.class);

    @Test
    public void testGenerateKeyPair() throws NoSuchAlgorithmException {

        KeyPair keyPair = Rs256.generateKeyPair(2048);
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        LOGGER.debug("Private key : [{}]", Rs256.privateKeyToPem(privateKey).replaceAll("\n", "\\\\n"));
        LOGGER.debug("Private key : [{}]", Rs256.privateKeyToPem(privateKey));
        LOGGER.debug("Public Key : [{}]", Rs256.publicKeyToPem(publicKey).replaceAll("\n", "\\\\n"));
        LOGGER.debug("Public Key : [{}]", Rs256.publicKeyToPem(publicKey));

    }

    @Test
    public void testPrivateKeyPemConversion() throws Exception {
        // Generate a key pair
        KeyPair keyPair = Rs256.generateKeyPair(2048);
        PrivateKey originalKey = keyPair.getPrivate();

        // Convert to PEM and back
        String pemKey = Rs256.privateKeyToPem(originalKey);
        PrivateKey convertedKey = Rs256.privateKeyFromPem(pemKey);

        // Verify the conversion worked correctly
        assertNotNull(pemKey);
        assertTrue(pemKey.startsWith("-----BEGIN PRIVATE KEY-----"));
        assertTrue(pemKey.endsWith("-----END PRIVATE KEY-----"));

        // Test that the keys are functionally equivalent by using them for encryption/decryption
        String testMessage = "Test message for encryption";
        byte[] encrypted = Rs256.encrypt(testMessage.getBytes(), keyPair.getPublic());
        byte[] decrypted = Rs256.decrypt(encrypted, convertedKey);

        assertEquals(testMessage, new String(decrypted));
    }

    @Test
    public void testPublicKeyPemConversion() throws Exception {
        // Generate a key pair
        KeyPair keyPair = Rs256.generateKeyPair(2048);
        PublicKey originalKey = keyPair.getPublic();

        // Convert to PEM and back
        String pemKey = Rs256.publicKeyToPem(originalKey);
        PublicKey convertedKey = Rs256.publicKeyFromPem(pemKey);

        // Verify the conversion worked correctly
        assertNotNull(pemKey);
        assertTrue(pemKey.startsWith("-----BEGIN PUBLIC KEY-----"));
        assertTrue(pemKey.endsWith("-----END PUBLIC KEY-----"));

        // Test that the keys are functionally equivalent by using them for signing/verification
        String testMessage = "Test message for signing";
        String signature = Rs256.sign(keyPair.getPrivate(), testMessage);

        assertTrue(Rs256.verify(convertedKey, testMessage, signature));
    }

}
