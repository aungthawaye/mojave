-- ml_wallet.mwe_wallet definition

CREATE TABLE `mwe_wallet`
(
    `wallet_id`       bigint         NOT NULL,
    `wallet_owner_id` bigint         NOT NULL,
    `name`            varchar(64)    NOT NULL,
    `balance`         decimal(34, 4) NOT NULL DEFAULT 0,
    `position`        decimal(34, 4) NOT NULL DEFAULT 0,
    `reserved`        decimal(34, 4) NOT NULL DEFAULT 0,
    `ndc`             decimal(34, 4) NOT NULL DEFAULT 0,
    `created_at`      bigint DEFAULT NULL,
    `rec_created_at`  bigint DEFAULT NULL,
    `rec_updated_at`  bigint DEFAULT NULL,
    `rec_version`     int    DEFAULT NULL,
    PRIMARY KEY (`wallet_id`),
    KEY `mwe_wallet_01_IDX` (`wallet_owner_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ml_wallet.mwe_ndc_update definition

CREATE TABLE `mwe_ndc_update`
(
    `ndc_update_id`  bigint         NOT NULL,
    `wallet_id`      bigint         NOT NULL,
    `transaction_id` bigint         NOT NULL,
    `old_ndc`        decimal(34, 4) NOT NULL,
    `new_ndc`        decimal(34, 4) NOT NULL,
    `transaction_at` bigint         NOT NULL,
    `rec_created_at` bigint DEFAULT NULL,
    `rec_updated_at` bigint DEFAULT NULL,
    `rec_version`    int    DEFAULT NULL,
    PRIMARY KEY (`ndc_update_id`),
    UNIQUE KEY `mwe_ndc_update_01_UK` (`transaction_id`),
    KEY `mwe_ndc_update_01_IDX` (`wallet_id`, `transaction_at`),
    KEY `mwe_wallet_mwe_ndc_update_FK_IDX` (`wallet_id`),
    CONSTRAINT `mwe_wallet_mwe_ndc_update_FK` FOREIGN KEY (`wallet_id`) REFERENCES `mwe_wallet` (`wallet_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ml_wallet.mwe_balance_update definition

CREATE TABLE `mwe_balance_update`
(
    `balance_update_id` bigint         NOT NULL,
    `balance_id`        bigint         NOT NULL,
    `action`            varchar(32)    NOT NULL,
    `transaction_id`    bigint         NOT NULL,
    `currency`          varchar(3)     NOT NULL,
    `amount`            decimal(34, 4) NOT NULL,
    `old_balance`       decimal(34, 4) NOT NULL,
    `new_balance`       decimal(34, 4) NOT NULL,
    `description`       varchar(255) DEFAULT NULL,
    `transaction_at`    bigint         NOT NULL,
    `created_at`        bigint         NOT NULL,
    `withdraw_id`       bigint       DEFAULT NULL,
    `rec_created_at`    bigint       DEFAULT NULL,
    `rec_updated_at`    bigint       DEFAULT NULL,
    `rec_version`       int          DEFAULT NULL,
    PRIMARY KEY (`balance_update_id`),
    UNIQUE KEY `mwe_balance_update_01_UK` (`balance_id`, `action`, `transaction_id`),
    UNIQUE KEY `mwe_balance_update_02_UK` (`withdraw_id`),
    KEY `mwe_balance_update_01_IDX` (`balance_id`, `action`, `transaction_at`),
    KEY `mwe_balance_update_02_IDX` (`transaction_at`),
    KEY `mwe_wallet_mwe_balance_update_FK_IDX` (`balance_id`),
    CONSTRAINT `mwe_wallet_mwe_balance_update_FK` FOREIGN KEY (`balance_id`) REFERENCES `mwe_wallet` (`wallet_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ml_wallet.mwe_position_update definition

CREATE TABLE `mwe_position_update`
(
    `position_update_id` bigint         NOT NULL,
    `position_id`        bigint         NOT NULL,
    `action`             varchar(32)    NOT NULL,
    `transaction_id`     bigint         NOT NULL,
    `currency`           varchar(3)     NOT NULL,
    `amount`             decimal(34, 4) NOT NULL,
    `old_position`       decimal(34, 4) NOT NULL,
    `new_position`       decimal(34, 4) NOT NULL,
    `old_reserved`       decimal(34, 4) NOT NULL,
    `new_reserved`       decimal(34, 4) NOT NULL,
    `ndc`                decimal(34, 4) NOT NULL,
    `description`        varchar(255) DEFAULT NULL,
    `transaction_at`     bigint         NOT NULL,
    `created_at`         bigint         NOT NULL,
    `reservation_id`     bigint       DEFAULT NULL,
    `rec_created_at`     bigint       DEFAULT NULL,
    `rec_updated_at`     bigint       DEFAULT NULL,
    `rec_version`        int          DEFAULT NULL,
    PRIMARY KEY (`position_update_id`),
    UNIQUE KEY `mwe_position_update_01_UK` (`position_id`, `action`, `transaction_id`),
    UNIQUE KEY `mwe_position_update_02_UK` (`reservation_id`),
    KEY `mwe_position_update_01_IDX` (`position_id`, `action`, `transaction_at`),
    KEY `mwe_position_update_02_IDX` (`transaction_at`),
    KEY `mwe_wallet_mwe_position_update_FK_IDX` (`position_id`),
    CONSTRAINT `mwe_wallet_mwe_position_update_FK` FOREIGN KEY (`position_id`) REFERENCES `mwe_wallet` (`wallet_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
