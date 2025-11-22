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

package io.mojaloop.component.jpa.routing;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class RoutingDataSourceConfigurer {

    public static RoutingDataSource configure(ReadSettings readSettings, WriteSettings writeSettings) {

        var readDataSource = RoutingDataSourceConfigurer.readDataSource(readSettings);
        var writeDataSource = RoutingDataSourceConfigurer.writeDataSource(writeSettings);

        var routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSources = new HashMap<>();

        dataSources.put(RoutingDataSource.Keys.READ, readDataSource);
        dataSources.put(RoutingDataSource.Keys.WRITE, writeDataSource);

        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);

        return routingDataSource;
    }

    private static DataSource readDataSource(ReadSettings readSettings) {

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

    private static DataSource writeDataSource(WriteSettings writeSettings) {

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

    public record WriteSettings(Connection connection, Pool pool) {

        public record Connection(String url, String username, String password, boolean autoCommit) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

    public record ReadSettings(ReadSettings.Connection connection, ReadSettings.Pool pool) {

        public record Connection(String url, String username, String password, boolean autoCommit) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

}
