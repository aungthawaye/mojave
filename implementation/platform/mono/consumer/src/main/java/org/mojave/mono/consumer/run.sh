#!/usr/bin/env bash
# Kafka
export KAFKA_BROKER_URL="localhost:9092"

# For Caches
export ACCOUNT_TIMER_CACHE_REFRESH_INTERVAL_MS="5000"
export CHART_ENTRY_TIMER_CACHE_REFRESH_INTERVAL_MS="5000"
export FLOW_DEFINITION_TIMER_CACHE_REFRESH_INTERVAL_MS="5000"

# Ledger Database Configuration
export MYSQL_LEDGER_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export MYSQL_LEDGER_DB_USER="root"
export MYSQL_LEDGER_DB_PASSWORD="password"
export MYSQL_LEDGER_DB_CONNECTION_TIMEOUT="30000"
export MYSQL_LEDGER_DB_VALIDATION_TIMEOUT="5000"
export MYSQL_LEDGER_DB_MAX_LIFETIME_TIMEOUT="1800000"
export MYSQL_LEDGER_DB_IDLE_TIMEOUT="600000"
export MYSQL_LEDGER_DB_KEEPALIVE_TIMEOUT="300000"
export MYSQL_LEDGER_DB_MIN_POOL_SIZE="2"
export MYSQL_LEDGER_DB_MAX_POOL_SIZE="2"

# MySQL Balance Database Configuration (used by MySqlBalanceUpdater)
export MYSQL_BALANCE_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export MYSQL_BALANCE_DB_USER="root"
export MYSQL_BALANCE_DB_PASSWORD="password"
export MYSQL_BALANCE_DB_CONNECTION_TIMEOUT="30000"
export MYSQL_BALANCE_DB_VALIDATION_TIMEOUT="5000"
export MYSQL_BALANCE_DB_MAX_LIFETIME_TIMEOUT="1800000"
export MYSQL_BALANCE_DB_IDLE_TIMEOUT="600000"
export MYSQL_BALANCE_DB_KEEPALIVE_TIMEOUT="300000"
export MYSQL_BALANCE_DB_MIN_POOL_SIZE="2"
export MYSQL_BALANCE_DB_MAX_POOL_SIZE="2"

# MySQL Position Database Configuration (used by MySqlPositionUpdater)
export MYSQL_POSITION_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export MYSQL_POSITION_DB_USER="root"
export MYSQL_POSITION_DB_PASSWORD="password"
export MYSQL_POSITION_DB_CONNECTION_TIMEOUT="30000"
export MYSQL_POSITION_DB_VALIDATION_TIMEOUT="5000"
export MYSQL_POSITION_DB_MAX_LIFETIME_TIMEOUT="1800000"
export MYSQL_POSITION_DB_IDLE_TIMEOUT="600000"
export MYSQL_POSITION_DB_KEEPALIVE_TIMEOUT="300000"
export MYSQL_POSITION_DB_MIN_POOL_SIZE="2"
export MYSQL_POSITION_DB_MAX_POOL_SIZE="2"

# Read Database Configuration
export READ_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export READ_DB_USER="root"
export READ_DB_PASSWORD="password"
export READ_DB_CONNECTION_TIMEOUT="30000"
export READ_DB_VALIDATION_TIMEOUT="5000"
export READ_DB_MAX_LIFETIME_TIMEOUT="1800000"
export READ_DB_IDLE_TIMEOUT="600000"
export READ_DB_KEEPALIVE_TIMEOUT="300000"
export READ_DB_MIN_POOL_SIZE="1"
export READ_DB_MAX_POOL_SIZE="2"

# Write Database Configuration
export WRITE_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export WRITE_DB_USER="root"
export WRITE_DB_PASSWORD="password"
export WRITE_DB_CONNECTION_TIMEOUT="30000"
export WRITE_DB_VALIDATION_TIMEOUT="5000"
export WRITE_DB_MAX_LIFETIME_TIMEOUT="1800000"
export WRITE_DB_IDLE_TIMEOUT="600000"
export WRITE_DB_KEEPALIVE_TIMEOUT="300000"
export WRITE_DB_MIN_POOL_SIZE="2"
export WRITE_DB_MAX_POOL_SIZE="2"

# Flyway
export FLYWAY_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export FLYWAY_DB_USER="root"
export FLYWAY_DB_PASSWORD="password"

java -cp "./*:./lib/*" -Dlog4j.configurationFile=classpath:mono-consumer-log4j2.xml org.mojave.mono.consumer.MonoConsumerApplication