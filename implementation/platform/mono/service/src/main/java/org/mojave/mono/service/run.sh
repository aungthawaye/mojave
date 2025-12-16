#!/usr/bin/env bash
## Mojave Mono Service

# Service Port
export MOJAVE_SERVICE_PORT="5003"

# Kafka
export KAFKA_BOOTSTRAP_SERVERS="localhost:9092"

# FSPIOP Service Settings
export FSPIOP_SERVICE_REQUEST_AGE_MS="300000"
export FSPIOP_SERVICE_REQUEST_AGE_VERIFICATION="true"

# FSPIOP Participant Settings
export FSPIOP_FSP_CODE="hub"
export FSPIOP_FSP_NAME="Mojave Hub"
export FSPIOP_ILP_SECRET="53cr3t"
export FSPIOP_SIGN_JWS="true"
export FSPIOP_VERIFY_JWS="true"
export FSPIOP_FSPS="fsp1,fsp2"
# Private key for this FSP (PEM, single line or quoted/multiline as supported by your loader)
export FSPIOP_PRIVATE_KEY_PEM="-----BEGINPRIVATEKEY-----MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDPFpQus+FOPjDt8/aDQ3VcrQSlr7x8hqxtODQYOVWF6AEoC0JCbaNq6CHVDXVLesiDPMhVL2EBDYUuCt8fY1+t7+B666/XMKoCVq+SgpJ3YqH0C1ma09XnluRWMOoFIHldq0wGBW5AP9D7rh5rhBKFpTAH6A0cd/J9USoBS7svro+g+G5fXLfgBs3jj4o4wmG3xk2hzFssU1NgCMpYi28YhPhzJZd6A2SRe6PD00NO1R1gco+OarJdmbIBPj5CuBn54B16h+t2LkKIFGpMvppz5siN6F+EgCkQKXS+pC9jiU/MtKlsyKWpvmu2DG7nqTD5EDFdQOf7ujLeR0Zc4Zt5AgMBAAECggEAPIO9k5Qu/50+0RocbCk3T3ijxgL/kmgMVPI6juWaL0InrFr15tGBEol48Xg4UXE0zNzjMblhoFwiEd1NbC+u0vYo4+KhhMGMvJg2QBr1z/s7lkQG0C6c8ErwuJscl5jwvkWDNrM5j37Wg+VSsFpsyy8FRRozUqFec+5W1wAZWAoke3MfCcx3llAhuRQ/fq06od4FbeSuBH5wdhqlKZvrqz0nsHN7C6L+PokT0f6AkiCsbAM9Zd8OAyWJuZA7sqJySN4JBvmd54xx4MLOGo/K8N6opVMxWpLR6foGP6fvXNK5+P3WBt+aqLSm69EfsFjPgWJ+3mxeaQnPy90vUI9xTQKBgQD8mBi9Cex1KkJIiMIHX39A6DNV921R8GjzZqwVx21xuRs8c9GN8+6i/gNfGLMv8EqN4aYO4wMBcSlvVxNadPLK+x8lfUxC/LjyPYI0PlXYsDoy5sEhALENbh6Jvaw6JQf8dhc/lT/be5IqMRMeoRukBNEg/JRwh6ibavhw+o6hPwKBgQDR4WevxVSm0mC0qMaGkNq81ZuBWfOCbzNIFET2EF/H1X1zyqvvVdnOq0lKTv4WitZ0HA7aZ1vShtM/gjZ514AUY1btGDabWGww+2L473cGz83E4V7lrY2grpGa+iYTT4+T9dm6JOIhc8lQqJIfnJ6He/PhzniCcwlVLc+SsoFdRwKBgQClsWfY6V+Q4+2jAwK0L3KcMzUpmSq+60MNFzae+rjSNqilGRxHT9IkXRf9E4jHU4q0U2bIsSVRltelT6tEVR7HA7/EqdKSxpDTZoG4n4NUpxUmOrRJX9jEhI3HsQH/CjeY5iYN8Wt4IDEbOCHgn7Iv/3DngIgm4PY7LQpXoiRZpQKBgQC+UQtY1DOi4npW2ATN2WTAf2J3tT2fNX7nCZpPFyIDb5BHA61NZigPxDq9a0v03QkCWL8PRVJ6YVAF8um7KrM1ya71aFN8In5ZHFvvKBZi0uTgdw06cFpRLntScSs9r5Oes/0vUmrLstcGkRWQHNGUQc4xIK0efXolB1mGyN+N2QKBgB9IrVUZ186NzbHR83YG3aQJIvHsCQ2FsgAH63WzWCJSyAo9cfGL9g4qHmBmZ72GEsPpnot5Kyv6U3793OySATO35+rxPlvvmigetDLw0pc1TGP9p1YLC8tNA+7CtwiHJl4OfJza7XgPcqUcE5cQPolxfy0XTcu4sLp23XsHQ/+d-----ENDPRIVATEKEY-----"
# Public keys of other FSPs (names must match FSPIOP_FSPS)
export FSPIOP_PUBLIC_KEY_PEM_OF_FSP2="-----BEGINPUBLICKEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzxaULrPhTj4w7fP2g0N1XK0Epa+8fIasbTg0GDlVhegBKAtCQm2jaugh1Q11S3rIgzzIVS9hAQ2FLgrfH2Nfre/geuuv1zCqAlavkoKSd2Kh9AtZmtPV55bkVjDqBSB5XatMBgVuQD/Q+64ea4QShaUwB+gNHHfyfVEqAUu7L66PoPhuX1y34AbN44+KOMJht8ZNocxbLFNTYAjKWItvGIT4cyWXegNkkXujw9NDTtUdYHKPjmqyXZmyAT4+QrgZ+eAdeofrdi5CiBRqTL6ac+bIjehfhIApECl0vqQvY4lPzLSpbMilqb5rtgxu56kw+RAxXUDn+7oy3kdGXOGbeQIDAQAB-----ENDPUBLICKEY-----"
export FSPIOP_PUBLIC_KEY_PEM_OF_FSP1="-----BEGINPUBLICKEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzxaULrPhTj4w7fP2g0N1XK0Epa+8fIasbTg0GDlVhegBKAtCQm2jaugh1Q11S3rIgzzIVS9hAQ2FLgrfH2Nfre/geuuv1zCqAlavkoKSd2Kh9AtZmtPV55bkVjDqBSB5XatMBgVuQD/Q+64ea4QShaUwB+gNHHfyfVEqAUu7L66PoPhuX1y34AbN44+KOMJht8ZNocxbLFNTYAjKWItvGIT4cyWXegNkkXujw9NDTtUdYHKPjmqyXZmyAT4+QrgZ+eAdeofrdi5CiBRqTL6ac+bIjehfhIApECl0vqQvY4lPzLSpbMilqb5rtgxu56kw+RAxXUDn+7oy3kdGXOGbeQIDAQAB-----ENDPUBLICKEY-----"

# Participant Store
export PARTICIPANT_STORE_REFRESH_INTERVAL_MS="300000"

# Intercom Base URLs
export PARTICIPANT_INTERCOM_BASE_URL="http://localhost:5002"
export TRANSACTION_INTERCOM_BASE_URL="http://localhost:5002"
export WALLET_INTERCOM_BASE_URL="http://localhost:5002"

# Quoting
export QUOTING_STATEFUL="false"

# Quoting Flyway
export QOT_FLYWAY_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export QOT_FLYWAY_DB_USER="root"
export QOT_FLYWAY_DB_PASSWORD="password"

# Read Database Configuration
export READ_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export READ_DB_USER="root"
export READ_DB_PASSWORD="password"
export READ_DB_CONNECTION_TIMEOUT="30000"
export READ_DB_VALIDATION_TIMEOUT="5000"
export READ_DB_MAX_LIFETIME_TIMEOUT="1800000"
export READ_DB_IDLE_TIMEOUT="600000"
export READ_DB_KEEPALIVE_TIMEOUT="300000"
export READ_DB_MIN_POOL_SIZE="2"
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
export WRITE_DB_MIN_POOL_SIZE="36"
export WRITE_DB_MAX_POOL_SIZE="36"

# Flyway
export FLYWAY_DB_URL="jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true"
export FLYWAY_DB_USER="root"
export FLYWAY_DB_PASSWORD="password"

# Transfer Settings
export TRANSFER_RESERVATION_TIMEOUT_MS="15000"
export TRANSFER_EXPIRY_TIMEOUT_MS="30000"

java -cp "./*:./lib/*" -Dlog4j.configurationFile=classpath:mono-service-log4j2.xml org.mojave.mono.service.MonoServiceApplication