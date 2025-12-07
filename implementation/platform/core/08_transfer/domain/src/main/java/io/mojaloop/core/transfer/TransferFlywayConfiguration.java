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

package io.mojaloop.core.transfer;

import io.mojaloop.component.flyway.FlywayMigration;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;

public class TransferFlywayConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway transferFlyway(TransferFlywayConfiguration.Settings transferFlywaySettings) {

        return FlywayMigration.configure(new FlywayMigration.Settings(
            transferFlywaySettings.url, transferFlywaySettings.username,
            transferFlywaySettings.password, "flyway_transfer_history",
            new String[]{"classpath:migration/transfer"}));
    }

    public interface RequiredBeans {

    }

    public interface RequiredSettings {

        TransferFlywayConfiguration.Settings transferFlywaySettings();

    }

    public record Settings(String url,
                           String username,
                           String password) { }

}
