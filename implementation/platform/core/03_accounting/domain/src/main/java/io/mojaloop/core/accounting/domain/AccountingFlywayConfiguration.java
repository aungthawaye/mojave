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
package io.mojaloop.core.accounting.domain;

import io.mojaloop.component.flyway.FlywayMigration;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

public class AccountingFlywayConfiguration {

    @Bean(initMethod = "migrate")
    @Qualifier("accountingFlyway")
    public Flyway accountingFlyway(AccountingFlywayConfiguration.Settings accountingFlywaySettings) {

        return FlywayMigration.configure(new FlywayMigration.Settings(
            accountingFlywaySettings.url, accountingFlywaySettings.username,
            accountingFlywaySettings.password, "flyway_accounting_history",
            new String[]{"classpath:migration/accounting"}));
    }

    public interface RequiredSettings {

        AccountingFlywayConfiguration.Settings accountingFlywaySettings();

    }

    public record Settings(String url,
                           String username,
                           String password) { }

}
