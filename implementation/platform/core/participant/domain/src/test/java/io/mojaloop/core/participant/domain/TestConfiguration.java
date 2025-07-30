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

package io.mojaloop.core.participant.domain;

import io.mojaloop.component.flyway.FlywayMigration;
import io.mojaloop.component.vault.Vault;
import io.mojaloop.component.vault.VaultConfiguration;
import org.springframework.context.annotation.Import;

@Import(value = {ParticipantDomainSettings.class, ParticipantDomainConfiguration.class, VaultConfiguration.class, TestSettings.class})
public class TestConfiguration {

    static {

        var vault = new Vault(TestSettings.VAULT_ADDR, TestSettings.VAULT_TOKEN, TestSettings.ENGINE_PATH);

        FlywayMigration.migrate(vault.get(ParticipantDomainSettings.VaultPaths.FLYWAY_PATH, FlywayMigration.Settings.class));
    }

}
