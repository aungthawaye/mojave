package io.mojaloop.common.component.persistence.routing;

import org.hibernate.dialect.MySQLDialect;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.Properties;

public class RoutingEntityManagerConfigurer {

    public LocalContainerEntityManagerFactoryBean configure(RoutingDataSource routingDataSource,
                                                            RoutingEntityManagerConfigurer.SettingsProvider settingsProvider,
                                                            String... packagesToScan) {

        var settings = settingsProvider.routingEntityManagerConfigurerSettings();

        var entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setDataSource(routingDataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        entityManagerFactoryBean.setPackagesToScan(packagesToScan);

        Properties jpaProperties = new Properties();

        jpaProperties.put("hibernate.dialect", MySQLDialect.class);
        jpaProperties.put("hibernate.show_sql", settings.showSql);
        jpaProperties.put("hibernate.format_sql", settings.formatSql);
        jpaProperties.put("hibernate.hibernate.connection.provider_disables_autocommit", true);

        entityManagerFactoryBean.setJpaProperties(jpaProperties);
        entityManagerFactoryBean.setPersistenceUnitName(settings.persistenceUnit);

        return entityManagerFactoryBean;

    }

    public interface SettingsProvider {

        RoutingEntityManagerConfigurer.Settings routingEntityManagerConfigurerSettings();

    }

    public record Settings(String persistenceUnit, String showSql, String formatSql) { }

}
