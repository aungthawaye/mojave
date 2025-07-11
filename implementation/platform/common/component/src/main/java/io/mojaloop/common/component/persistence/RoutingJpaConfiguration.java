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
package io.mojaloop.common.component.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.mojaloop.common.component.persistence.datasource.RoutingDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.dialect.MySQLDialect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@EnableJpaRepositories(
    basePackages = "io.mojaloop",
    enableDefaultTransactions = false,
    considerNestedRepositories = true)
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RoutingJpaConfiguration {

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource routingDataSource, JpaSettings jpaSettings) {

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(routingDataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        entityManagerFactoryBean.setPackagesToScan(jpaSettings.packagesToScan);

        Properties jpaProperties = new Properties();

        jpaProperties.put("hibernate.dialect", MySQLDialect.class);
        jpaProperties.put("hibernate.show_sql", jpaSettings.showSql);
        jpaProperties.put("hibernate.format_sql", jpaSettings.formatSql);
        jpaProperties.put("hibernate.hibernate.connection.provider_disables_autocommit", true);

        entityManagerFactoryBean.setJpaProperties(jpaProperties);
        entityManagerFactoryBean.setPersistenceUnitName(jpaSettings.persistenceUnit);

        return entityManagerFactoryBean;

    }

    @Bean
    public DataSource routingDataSource(@Qualifier(RoutingDataSource.Qualifiers.READ) DataSource readDataSource,
                                        @Qualifier(RoutingDataSource.Qualifiers.WRITE) DataSource writeDataSource) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSources = new HashMap<>();

        dataSources.put(RoutingDataSource.Qualifiers.READ, readDataSource);
        dataSources.put(RoutingDataSource.Qualifiers.WRITE, writeDataSource);

        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);

        return routingDataSource;

    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);

    }

    @Bean
    @Qualifier(RoutingDataSource.Qualifiers.WRITE)
    public DataSource writeDataSource(WriteSettings writeSettings) {

        var config = new HikariConfig();

        config.setPoolName(writeSettings.pool.name());
        config.setJdbcUrl(writeSettings.connection.url());
        config.setUsername(writeSettings.connection.username());
        config.setPassword(writeSettings.connection.password());
        config.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("rereadBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("maintainTimeStats", false);

        config.setMaximumPoolSize(writeSettings.pool.maxPool());
        config.setAutoCommit(writeSettings.connection.autoCommit());

        return new HikariDataSource(config);
    }

    public interface SettingsProvider {

        JpaSettings routingJpaConfigurationJpaSettings();

        ReadSettings routingJpaConfigurationReadSettings();

        WriteSettings routingJpaConfigurationWriteSettings();

    }

    public record JpaSettings(String persistenceUnit, String[] packagesToScan, String showSql, String formatSql) { }

    public record WriteSettings(Connection connection, Pool pool) {

        public record Connection(String url, String username, String password, boolean autoCommit) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

    public record ReadSettings(WriteSettings.Connection connection, WriteSettings.Pool pool) {

        public record Connection(String url, String username, String password, boolean autoCommit) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

}
