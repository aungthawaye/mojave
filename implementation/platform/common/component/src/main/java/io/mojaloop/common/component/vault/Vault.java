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
package io.mojaloop.common.component.vault;

import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;

import java.net.URI;
import java.util.Objects;

public class Vault {

    private final String enginePath;

    private final VaultTemplate vaultTemplate;

    public Vault(String vaultAddress, String vaultToken, String enginePath) {

        try {

            VaultEndpoint vaultEndpoint = VaultEndpoint.from(new URI(vaultAddress));
            this.vaultTemplate = new VaultTemplate(vaultEndpoint, new TokenAuthentication(vaultToken));
            this.enginePath = enginePath;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    public <T> T get(String path, Class<T> template) {

        VaultKeyValueOperations keyValueOperations =
            this.vaultTemplate.opsForKeyValue(this.enginePath,
                                              VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);

        return Objects.requireNonNull(keyValueOperations.get(path, template)).getData();
    }

}
