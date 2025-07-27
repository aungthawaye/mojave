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

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rs256UT {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rs256UT.class);

    @Test
    public void test() throws Exception {

        var keyPair = Rs256.generateKeyPair(2048);

        var publicKeyString = Rs256.publicKeyToBase64(keyPair.getPublic());
        LOGGER.debug("Public Key: {}", publicKeyString);
        var privateKeyString = Rs256.privateKeyToBase64(keyPair.getPrivate());
        LOGGER.debug("Private Key: {}", privateKeyString);

        var publicKey = Rs256.publicKeyFromBase64(publicKeyString);
        var privateKey = Rs256.privateKeyFromBase64(privateKeyString);

        var message = "This is a test message";

        var encrypted = Rs256.encrypt(message.getBytes(), publicKey);
        var decrypted = Rs256.decrypt(encrypted, privateKey);

        LOGGER.debug("decrypted : {}", new String(decrypted));
    }
}
