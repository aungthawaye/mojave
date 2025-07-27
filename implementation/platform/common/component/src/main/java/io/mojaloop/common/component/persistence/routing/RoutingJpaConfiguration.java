package io.mojaloop.common.component.persistence.routing;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableJpaRepositories(basePackages = {"io.mojaloop"}, considerNestedRepositories = true)
@EnableTransactionManagement
public class RoutingJpaConfiguration {

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(RoutingDataSource routingDataSource,
                                                                       RoutingEntityManagerConfigurer.Settings settings) {

        return RoutingEntityManagerConfigurer.configure(routingDataSource, settings, "io.mojaloop");
    }

    @Bean
    public RoutingDataSource routingDataSource(RoutingDataSourceConfigurer.ReadSettings readSettings,
                                               RoutingDataSourceConfigurer.WriteSettings writeSettings) {

        return RoutingDataSourceConfigurer.configure(readSettings, writeSettings);
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }

    public interface RequiredSettings {

        RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings();

        RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings();

        RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings();

    }

}
