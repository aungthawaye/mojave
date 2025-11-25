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

    public static RoutingDataSource configure(ReadSettings readSettings,
                                              WriteSettings writeSettings) {

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

//        config.setPoolName(readSettings.pool().name());
//        config.setJdbcUrl(readSettings.connection().url());
//        config.setUsername(readSettings.connection().username());
//        config.setPassword(readSettings.connection().password());
//        config.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());
//
//        config.addDataSourceProperty("cachePrepStmts", "true");
//        config.addDataSourceProperty("prepStmtCacheSize", "250");
//        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//        config.addDataSourceProperty("useServerPrepStmts", true);
//        config.addDataSourceProperty("useLocalSessionState", true);
//        config.addDataSourceProperty("rereadBatchedStatements", true);
//        config.addDataSourceProperty("cacheResultSetMetadata", true);
//        config.addDataSourceProperty("cacheServerConfiguration", true);
//        config.addDataSourceProperty("elideSetAutoCommits", true);
//        config.addDataSourceProperty("maintainTimeStats", false);
//
//        config.setMaximumPoolSize(readSettings.pool().maxPool());
//        config.setAutoCommit(readSettings.connection().autoCommit());

        // Basic
        config.setPoolName(readSettings.pool().name());
        config.setJdbcUrl(readSettings.connection().url());
        config.setUsername(readSettings.connection().username());
        config.setPassword(readSettings.connection().password());
        config.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());

        // ---- MySQL driver performance flags ----
        // Statement cache
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "500");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096");

        // Batch optimization (if you use batch)
        config.addDataSourceProperty("rewriteBatchedStatements", "true");

        // Metadata / session state caches
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        // Server-side prepared statements
        // If you *know* CALL metadata bugs are not an issue in your env, turn this ON for higher TPS:
        config.addDataSourceProperty("useServerPrepStmts", "true");
        // If you still hit CALL issues, set to false and keep callableStmtCacheSize = 0
        // config.addDataSourceProperty("useServerPrepStmts", "false");
        config.addDataSourceProperty("callableStmtCacheSize", "0");

        // Optional: keep your previous "useResetSession" behaviour
        config.addDataSourceProperty("useResetSession", "true");

        // ---- Hikari pool sizing ----
        config.setMaximumPoolSize(readSettings.pool().maxPool());
        // For no permanent idle connections: pool can shrink to 0 when app is idle
        config.setMinimumIdle(0);

        // ---- Timeouts (fail fast under high TPS) ----
        config.setConnectionTimeout(250);         // ms – tune (200–500ms typical)
        config.setValidationTimeout(1000);        // ms – how long isValid() may take
        config.setAutoCommit(true);               // fine for most OLTP workloads

        // ---- Idle behaviour & “no idle wakeup” to DB ----

        // 1) Do NOT set a connectionTestQuery
        //    Hikari will use connection.isValid() only when a connection is borrowed.

        // 2) Disable keepalive pings – no queries while idle.
        config.setKeepaliveTime(0L);              // default, but set explicitly for clarity

        // 3) Let the pool close connections when app is idle, so DB sees *zero* connections
        //    after some quiet period. No idle queries because there are no connections.
        //
        //    Example: after 60s of zero usage, shrink pool to 0.
        config.setIdleTimeout(60_000L);           // 60s – adjust as you like

        // 4) Reasonable max lifetime to avoid stale connections,
        //    but still no idle test queries.
        //    Make this a bit less than MySQL wait_timeout if you changed it.
        config.setMaxLifetime(30 * 60_000L);      // 30 minutes

        // If you want to *never* recycle connections proactively (not generally recommended):
        // config.setIdleTimeout(0L);             // don't reap idles
        // config.setMaxLifetime(0L);            // infinite lifetime; DB/firewall will close

        return new HikariDataSource(config);
    }

    private static DataSource writeDataSource(WriteSettings writeSettings) {

        var config = new HikariConfig();

        // Basic
        config.setPoolName(writeSettings.pool().name());
        config.setJdbcUrl(writeSettings.connection().url());
        config.setUsername(writeSettings.connection().username());
        config.setPassword(writeSettings.connection().password());
        config.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());

        // ---- MySQL driver performance flags ----
        // Statement cache
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "500");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096");

        // Batch optimization (if you use batch)
        config.addDataSourceProperty("rewriteBatchedStatements", "true");

        // Metadata / session state caches
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        // Server-side prepared statements
        // If you *know* CALL metadata bugs are not an issue in your env, turn this ON for higher TPS:
        config.addDataSourceProperty("useServerPrepStmts", "true");
        // If you still hit CALL issues, set to false and keep callableStmtCacheSize = 0
        // config.addDataSourceProperty("useServerPrepStmts", "false");
        config.addDataSourceProperty("callableStmtCacheSize", "0");

        // Optional: keep your previous "useResetSession" behaviour
        config.addDataSourceProperty("useResetSession", "true");

        // ---- Hikari pool sizing ----
        config.setMaximumPoolSize(writeSettings.pool().maxPool());
        // For no permanent idle connections: pool can shrink to 0 when app is idle
        config.setMinimumIdle(0);

        // ---- Timeouts (fail fast under high TPS) ----
        config.setConnectionTimeout(250);         // ms – tune (200–500ms typical)
        config.setValidationTimeout(1000);        // ms – how long isValid() may take
        config.setAutoCommit(true);               // fine for most OLTP workloads

        // ---- Idle behaviour & “no idle wakeup” to DB ----

        // 1) Do NOT set a connectionTestQuery
        //    Hikari will use connection.isValid() only when a connection is borrowed.

        // 2) Disable keepalive pings – no queries while idle.
        config.setKeepaliveTime(0L);              // default, but set explicitly for clarity

        // 3) Let the pool close connections when app is idle, so DB sees *zero* connections
        //    after some quiet period. No idle queries because there are no connections.
        //
        //    Example: after 60s of zero usage, shrink pool to 0.
        config.setIdleTimeout(60_000L);           // 60s – adjust as you like

        // 4) Reasonable max lifetime to avoid stale connections,
        //    but still no idle test queries.
        //    Make this a bit less than MySQL wait_timeout if you changed it.
        config.setMaxLifetime(30 * 60_000L);      // 30 minutes

        // If you want to *never* recycle connections proactively (not generally recommended):
        // config.setIdleTimeout(0L);             // don't reap idles
        // config.setMaxLifetime(0L);            // infinite lifetime; DB/firewall will close

        return new HikariDataSource(config);
    }

    public record WriteSettings(Connection connection, Pool pool) {

        public record Connection(String url,
                                 String username,
                                 String password,
                                 boolean autoCommit) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

    public record ReadSettings(ReadSettings.Connection connection, ReadSettings.Pool pool) {

        public record Connection(String url,
                                 String username,
                                 String password,
                                 boolean autoCommit) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

}
