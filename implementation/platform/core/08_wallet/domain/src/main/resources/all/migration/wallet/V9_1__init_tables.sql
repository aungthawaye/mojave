-- ml_wallet.wlt_wallet definition

CREATE TABLE `wlt_wallet`
(
    `wallet_id`       bigint         NOT NULL,
    `wallet_owner_id` bigint         NOT NULL,
    `currency`        varchar(3)     NOT NULL,
    `name`            varchar(64)    NOT NULL,
    `balance`         decimal(34, 4) NOT NULL,
    `created_at`      bigint         NOT NULL,
    `rec_created_at`  bigint DEFAULT NULL,
    `rec_updated_at`  bigint DEFAULT NULL,
    `rec_version`     int    DEFAULT NULL,
    PRIMARY KEY (`wallet_id`),
    UNIQUE KEY `wlt_wallet_owner_id_currency_UK` (`wallet_owner_id`, `currency`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_wallet.wlt_balance_update definition

CREATE TABLE `wlt_balance_update`
(
    `balance_update_id` bigint         NOT NULL,
    `wallet_id`         bigint         NOT NULL,
    `action`            varchar(32)    NOT NULL,
    `transaction_id`    bigint         NOT NULL,
    `currency`          varchar(3)     NOT NULL,
    `amount`            decimal(34, 4) NOT NULL,

    `old_balance`       decimal(34, 4) NOT NULL,
    `new_balance`       decimal(34, 4) NOT NULL,

    `description`       varchar(256) DEFAULT NULL,
    `transaction_at`    bigint         NOT NULL,

    `created_at`        bigint         NOT NULL,
    `reversed_id`       bigint       DEFAULT NULL,
    `rec_created_at`    bigint       DEFAULT NULL,
    `rec_updated_at`    bigint       DEFAULT NULL,
    `rec_version`       int          DEFAULT NULL,

    PRIMARY KEY (`balance_update_id`),
    UNIQUE KEY `wlt_balance_update_wallet_id_action_transaction_id_UK` (`wallet_id`, `action`, `transaction_id`),
    UNIQUE KEY `wlt_balance_update_reversed_id_UK` (`reversed_id`),
    KEY `wlt_balance_update_wallet_id_action_transaction_at_IDX` (`wallet_id`, `action`, `transaction_at`),
    KEY `wlt_balance_update_transaction_at_IDX` (`transaction_at`),
    KEY `wlt_balance_update_wlt_wallet_FK` (`wallet_id`),
    CONSTRAINT `wlt_balance_update_wlt_wallet_FK` FOREIGN KEY (`wallet_id`) REFERENCES `wlt_wallet` (`wallet_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_wallet.wlt_position definition

CREATE TABLE `wlt_position`
(
    `position_id`     bigint         NOT NULL,
    `wallet_owner_id` bigint         NOT NULL,
    `currency`        varchar(3)     NOT NULL,
    `name`            varchar(64)    NOT NULL,
    `position`        decimal(34, 4) NOT NULL,
    `reserved`        decimal(34, 4) NOT NULL,
    `net_debit_cap`   decimal(34, 4) NOT NULL,
    `created_at`      bigint         NOT NULL,
    `rec_created_at`  bigint DEFAULT NULL,
    `rec_updated_at`  bigint DEFAULT NULL,
    `rec_version`     int    DEFAULT NULL,
    PRIMARY KEY (`position_id`),
    UNIQUE KEY `wlt_position_wallet_owner_id_currency_UK` (`wallet_owner_id`, `currency`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_wallet.wlt_position_update definition

CREATE TABLE `wlt_position_update`
(
    `position_update_id` bigint         NOT NULL,
    `position_id`        bigint         NOT NULL,
    `action`             varchar(32)    NOT NULL,
    `transaction_id`     bigint         NOT NULL,
    `currency`           varchar(3)     NOT NULL,
    `amount`             decimal(34, 4) NOT NULL,

    `old_position`       decimal(34, 4) NOT NULL,
    `new_position`       decimal(34, 4) NOT NULL,

    `description`        varchar(256) DEFAULT NULL,
    `transaction_at`     bigint         NOT NULL,

    `created_at`         bigint         NOT NULL,
    `reversed_id`        bigint       DEFAULT NULL,
    `rec_created_at`     bigint       DEFAULT NULL,
    `rec_updated_at`     bigint       DEFAULT NULL,
    `rec_version`        int          DEFAULT NULL,

    PRIMARY KEY (`position_update_id`),
    UNIQUE KEY `wlt_position_update_position_id_action_transaction_id_UK` (`position_id`, `action`, `transaction_id`),
    UNIQUE KEY `wlt_position_update_reversed_id_UK` (`reversed_id`),
    KEY `wlt_position_update_position_id_action_transaction_at_IDX` (`position_id`, `action`, `transaction_at`),
    KEY `wlt_position_update_transaction_at_IDX` (`transaction_at`),
    KEY `wlt_position_update_wlt_position_FK` (`position_id`),
    CONSTRAINT `wlt_position_update_wlt_position_FK` FOREIGN KEY (`position_id`) REFERENCES `wlt_position` (`position_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
