-- ml_wallet.wlt_net_debit_cap_update definition

CREATE TABLE `wlt_net_debit_cap_update`
(
    `net_debit_cap_update_id` bigint         NOT NULL,
    `position_id`             bigint DEFAULT NULL,
    `transaction_id`          bigint DEFAULT NULL,
    `old_net_debit_cap`       decimal(34, 4) NOT NULL,
    `new_net_debit_cap`       decimal(34, 4) NOT NULL,
    `transaction_at`          bigint         NOT NULL,
    `rec_created_at`          bigint DEFAULT NULL,
    `rec_updated_at`          bigint DEFAULT NULL,
    `rec_version`             int    DEFAULT NULL,
    PRIMARY KEY (`net_debit_cap_update_id`),
    UNIQUE KEY `wlt_net_debit_cap_update_transaction_id_UK` (`transaction_id`),
    KEY `wlt_net_debit_cap_update_position_id_transaction_at_IDX` (`position_id`, `transaction_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_wallet.wlt_balance definition

CREATE TABLE `wlt_balance`
(
    `balance_id`      bigint         NOT NULL,
    `wallet_owner_id` bigint DEFAULT NULL,
    `currency`        varchar(3)     NOT NULL,
    `name`            varchar(64)    NOT NULL,
    `balance`         decimal(34, 4) NOT NULL,
    `created_at`      bigint DEFAULT NULL,
    `rec_created_at`  bigint DEFAULT NULL,
    `rec_updated_at`  bigint DEFAULT NULL,
    `rec_version`     int    DEFAULT NULL,
    PRIMARY KEY (`balance_id`),
    UNIQUE KEY `wlt_balance_owner_id_currency_UK` (`wallet_owner_id`, `currency`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_wallet.wlt_position definition

CREATE TABLE `wlt_position`
(
    `position_id`     bigint         NOT NULL,
    `wallet_owner_id` bigint DEFAULT NULL,
    `currency`        varchar(3)     NOT NULL,
    `name`            varchar(64)    NOT NULL,
    `position`        decimal(34, 4) NOT NULL,
    `reserved`        decimal(34, 4) NOT NULL,
    `ndc`             decimal(34, 4) NOT NULL,
    `created_at`      bigint DEFAULT NULL,
    `rec_created_at`  bigint DEFAULT NULL,
    `rec_updated_at`  bigint DEFAULT NULL,
    `rec_version`     int    DEFAULT NULL,
    PRIMARY KEY (`position_id`),
    UNIQUE KEY `wlt_balance_owner_id_currency_UK` (`wallet_owner_id`, `currency`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_wallet.wlt_balance_update definition

CREATE TABLE `wlt_balance_update`
(
    `balance_update_id` bigint         NOT NULL,
    `balance_id`        bigint       DEFAULT NULL,
    `action`            varchar(255) DEFAULT NULL,
    `transaction_id`    bigint       DEFAULT NULL,
    `currency`          varchar(255) DEFAULT NULL,
    `amount`            decimal(34, 4) NOT NULL,
    `old_balance`       decimal(34, 4) NOT NULL,
    `new_balance`       decimal(34, 4) NOT NULL,
    `description`       varchar(255) DEFAULT NULL,
    `transaction_at`    bigint       DEFAULT NULL,
    `created_at`        bigint       DEFAULT NULL,
    `reversal_id`       bigint       DEFAULT NULL,
    `rec_created_at`    bigint       DEFAULT NULL,
    `rec_updated_at`    bigint       DEFAULT NULL,
    `rec_version`       int          DEFAULT NULL,
    PRIMARY KEY (`balance_update_id`),
    UNIQUE KEY `wlt_balance_update_balance_id_action_transaction_id_UK` (`balance_id`, `action`, `transaction_id`),
    UNIQUE KEY `wlt_balance_update_reversal_id_UK` (`reversal_id`),
    KEY `wlt_balance_update_balance_id_action_transaction_at_idx` (`balance_id`, `action`, `transaction_at`),
    KEY `wlt_balance_update_transaction_at_idx` (`transaction_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_wallet.wlt_position_update definition

CREATE TABLE `wlt_position_update`
(
    `position_update_id` bigint         NOT NULL,
    `position_id`        bigint       DEFAULT NULL,
    `action`             varchar(255) DEFAULT NULL,
    `transaction_id`     bigint       DEFAULT NULL,
    `currency`           varchar(255) DEFAULT NULL,
    `amount`             decimal(34, 4) NOT NULL,
    `old_position`       decimal(34, 4) NOT NULL,
    `new_position`       decimal(34, 4) NOT NULL,
    `old_reserved`       decimal(34, 4) NOT NULL,
    `new_reserved`       decimal(34, 4) NOT NULL,
    `ndc`                decimal(34, 4) NOT NULL,
    `description`        varchar(255) DEFAULT NULL,
    `transaction_at`     bigint       DEFAULT NULL,
    `created_at`         bigint       DEFAULT NULL,
    `reservation_id`     bigint       DEFAULT NULL,
    `rec_created_at`     bigint       DEFAULT NULL,
    `rec_updated_at`     bigint       DEFAULT NULL,
    `rec_version`        int          DEFAULT NULL,
    PRIMARY KEY (`position_update_id`),
    UNIQUE KEY `wlt_position_update_position_id_action_transaction_id_UK` (`position_id`, `action`, `transaction_id`),
    UNIQUE KEY `wlt_balance_update_reversed_id_UK` (`reservation_id`),
    KEY `wlt_position_update_position_id_action_transaction_at_idx` (`position_id`, `action`, `transaction_at`),
    KEY `wlt_position_update_transaction_at_idx` (`transaction_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
