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
package io.mojaloop.core.quoting.domain;

import io.mojaloop.component.flyway.FlywayMigration;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;

public class QuotingFlywayConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway quotingFlyway(QuotingFlywayConfiguration.Settings quotingFlywaySettings) {

        return FlywayMigration.configure(new FlywayMigration.Settings(
            quotingFlywaySettings.url, quotingFlywaySettings.username,
            quotingFlywaySettings.password, quotingFlywaySettings.table,
            quotingFlywaySettings.locations));
    }

    public interface RequiredSettings {

        QuotingFlywayConfiguration.Settings quotingFlywaySettings();

    }

    public record Settings(String url,
                           String username,
                           String password,
                           String table,
                           String[] locations) { }

}
