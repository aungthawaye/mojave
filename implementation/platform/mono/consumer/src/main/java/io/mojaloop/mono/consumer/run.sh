# Transaction Consumer

# Kafka
export KAFKA_BROKER_URL="localhost:9092"

# For Caches
export ACCOUNT_TIMER_CACHE_REFRESH_INTERVAL_MS="5000"
export CHART_ENTRY_TIMER_CACHE_REFRESH_INTERVAL_MS="5000"
export FLOW_DEFINITION_TIMER_CACHE_REFRESH_INTERVAL_MS="5000"

# Flyway Migration Database Configuration
export MONO_FLYWAY_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"
export MONO_FLYWAY_DB_USER="root"
export MONO_FLYWAY_DB_PASSWORD="password"

# Ledger Database Configuration
export MONO_MYSQL_LEDGER_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export MONO_MYSQL_LEDGER_DB_USER="root"
export MONO_MYSQL_LEDGER_DB_PASSWORD="password"
export MONO_MYSQL_LEDGER_DB_MIN_POOL_SIZE="4"
export MONO_MYSQL_LEDGER_DB_MAX_POOL_SIZE="4"

# Read Database Configuration
export MONO_READ_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export MONO_READ_DB_USER="root"
export MONO_READ_DB_PASSWORD="password"
export MONO_READ_DB_MIN_POOL_SIZE="1"
export MONO_READ_DB_MAX_POOL_SIZE="2"

# Write Database Configuration
export MONO_WRITE_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export MONO_WRITE_DB_USER="root"
export MONO_WRITE_DB_PASSWORD="password"
export MONO_WRITE_DB_MIN_POOL_SIZE="1"
export MONO_WRITE_DB_MAX_POOL_SIZE="2"

java -cp "./*:./lib/*" io.mojaloop.mono.consumer.MonoConsumerApplication