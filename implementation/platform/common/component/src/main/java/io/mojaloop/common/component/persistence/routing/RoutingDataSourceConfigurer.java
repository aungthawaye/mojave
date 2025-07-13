package io.mojaloop.common.component.persistence.routing;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class RoutingDataSourceConfigurer {

    public RoutingDataSource configure(SettingsProvider settingsProvider) {

        var readSettings = settingsProvider.routingDataSourceConfigurerReadSettings();
        var readDataSource = this.readDataSource(readSettings);

        var writeSettings = settingsProvider.routingDataSourceConfigurerWriteSettings();
        var writeDataSource = this.writeDataSource(writeSettings);

        var routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSources = new HashMap<>();

        dataSources.put(RoutingDataSource.Qualifiers.READ, readDataSource);
        dataSources.put(RoutingDataSource.Qualifiers.WRITE, writeDataSource);

        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);

        return routingDataSource;
    }

    private DataSource readDataSource(RoutingDataSourceConfigurer.ReadSettings readSettings) {

        var config = new HikariConfig();

        config.setPoolName(readSettings.pool().name());
        config.setJdbcUrl(readSettings.connection().url());
        config.setUsername(readSettings.connection().username());
        config.setPassword(readSettings.connection().password());
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

        config.setMaximumPoolSize(readSettings.pool().maxPool());
        config.setAutoCommit(readSettings.connection().autoCommit());

        return new HikariDataSource(config);
    }

    private DataSource writeDataSource(RoutingDataSourceConfigurer.WriteSettings writeSettings) {

        var config = new HikariConfig();

        config.setPoolName(writeSettings.pool().name());
        config.setJdbcUrl(writeSettings.connection().url());
        config.setUsername(writeSettings.connection().username());
        config.setPassword(writeSettings.connection().password());
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

        config.setMaximumPoolSize(writeSettings.pool().maxPool());
        config.setAutoCommit(writeSettings.connection().autoCommit());

        return new HikariDataSource(config);
    }

    public interface SettingsProvider {

        RoutingDataSourceConfigurer.ReadSettings routingDataSourceConfigurerReadSettings();

        RoutingDataSourceConfigurer.WriteSettings routingDataSourceConfigurerWriteSettings();

    }

    public record WriteSettings(RoutingDataSourceConfigurer.WriteSettings.Connection connection,
                                RoutingDataSourceConfigurer.WriteSettings.Pool pool) {

        public record Connection(String url, String username, String password, boolean autoCommit) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

    public record ReadSettings(RoutingDataSourceConfigurer.WriteSettings.Connection connection,
                               RoutingDataSourceConfigurer.WriteSettings.Pool pool) {

        public record Connection(String url, String username, String password, boolean autoCommit) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

}
