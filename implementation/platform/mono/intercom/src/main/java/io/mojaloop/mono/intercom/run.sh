## Mojave Mono Intercom Service

# Service Port
export MOJAVE_INTERCOM_PORT="5002"

# MySQL Balance Database Configuration (used by MySqlBalanceUpdater)
export MONO_BALANCE_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export MONO_BALANCE_DB_USER="root"
export MONO_BALANCE_DB_PASSWORD="password"
export MONO_BALANCE_DB_MIN_POOL_SIZE="2"
export MONO_BALANCE_DB_MAX_POOL_SIZE="2"

# MySQL Ledger Database Configuration (used by Ledger/MySqlLedger)
export MONO_LEDGER_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export MONO_LEDGER_DB_USER="root"
export MONO_LEDGER_DB_PASSWORD="password"
export MONO_LEDGER_DB_MIN_POOL_SIZE="2"
export MONO_LEDGER_DB_MAX_POOL_SIZE="2"

# MySQL Position Database Configuration (used by MySqlPositionUpdater)
export MONO_POSITION_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export MONO_POSITION_DB_USER="root"
export MONO_POSITION_DB_PASSWORD="password"
export MONO_POSITION_DB_MIN_POOL_SIZE="48"
export MONO_POSITION_DB_MAX_POOL_SIZE="48"

# Read Database Configuration (RoutingDataSource - read)
export MONO_READ_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export MONO_READ_DB_USER="root"
export MONO_READ_DB_PASSWORD="password"
export MONO_READ_DB_MIN_POOL_SIZE="2"
export MONO_READ_DB_MAX_POOL_SIZE="2"

# Write Database Configuration (RoutingDataSource - write)
export MONO_WRITE_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export MONO_WRITE_DB_USER="root"
export MONO_WRITE_DB_PASSWORD="password"
export MONO_WRITE_DB_MIN_POOL_SIZE="24"
export MONO_WRITE_DB_MAX_POOL_SIZE="24"

# Flyway
export MONO_FLYWAY_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export MONO_FLYWAY_DB_USER="root"
export MONO_FLYWAY_DB_PASSWORD="password"

java -cp "./*:./lib/*" io.mojaloop.mono.intercom.MonoIntercomApplication