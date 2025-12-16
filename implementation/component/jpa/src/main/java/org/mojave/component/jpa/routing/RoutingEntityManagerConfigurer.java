/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.component.jpa.routing;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.Properties;

public class RoutingEntityManagerConfigurer {

    public static LocalContainerEntityManagerFactoryBean configure(RoutingDataSource routingDataSource,
                                                                   Settings settings,
                                                                   String... packagesToScan) {

        var entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setDataSource(routingDataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        entityManagerFactoryBean.setPackagesToScan(packagesToScan);

        Properties jpaProperties = new Properties();

        jpaProperties.put("hibernate.show_sql", settings.showSql);
        jpaProperties.put("hibernate.format_sql", settings.formatSql);
        jpaProperties.put("hibernate.hibernate.connection.provider_disables_autocommit", true);

        entityManagerFactoryBean.setJpaProperties(jpaProperties);
        entityManagerFactoryBean.setPersistenceUnitName(settings.persistenceUnit);

        return entityManagerFactoryBean;

    }

    public record Settings(String persistenceUnit, boolean showSql, boolean formatSql) { }

}
