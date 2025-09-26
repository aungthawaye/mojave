-- ml_account.acc_chart definition

CREATE TABLE `acc_chart`
(
    `chart_id`       bigint      NOT NULL,
    `name`           varchar(64) NOT NULL,
    `created_at`     bigint      NOT NULL,
    `rec_created_at` bigint DEFAULT NULL,
    `rec_updated_at` bigint DEFAULT NULL,
    `rec_version`    int    DEFAULT NULL,
    PRIMARY KEY (`chart_id`),
    UNIQUE KEY `acc_chart_name_UK` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_account.acc_chart_entry definition

CREATE TABLE `acc_chart_entry`
(
    `chart_entry_id`   bigint       NOT NULL,
    `chart_entry_code` varchar(32)  NOT NULL,
    `name`             varchar(64)  NOT NULL,
    `description`      varchar(256) NOT NULL,
    `account_type`     varchar(32)  NOT NULL,
    `created_at`       bigint       NOT NULL,
    `chart_id`         bigint       NOT NULL,
    `rec_created_at`   bigint DEFAULT NULL,
    `rec_updated_at`   bigint DEFAULT NULL,
    `rec_version`      int    DEFAULT NULL,
    PRIMARY KEY (`chart_entry_id`),
    UNIQUE KEY `acc_chart_entry_chart_entry_code_UK` (`chart_entry_code`),
    KEY `acc_chart_entry_acc_chart_FK` (`chart_id`),
    CONSTRAINT `acc_chart_entry_acc_chart_FK` FOREIGN KEY (`chart_id`) REFERENCES `acc_chart` (`chart_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_account.acc_account definition

CREATE TABLE `acc_account`
(
    `account_id`         bigint      NOT NULL,
    `owner_id`           bigint      NOT NULL,
    `type`               varchar(32) NOT NULL,
    `currency`           varchar(3)  NOT NULL,
    `code`               varchar(32) NOT NULL,
    `name`               varchar(64) NOT NULL,
    `description`        varchar(256) DEFAULT NULL,
    `created_at`         bigint      NOT NULL,
    `activation_status`  varchar(32) NOT NULL,
    `termination_status` varchar(32) NOT NULL,
    `chart_entry_id`     bigint      NOT NULL,
    `rec_created_at`     bigint       DEFAULT NULL,
    `rec_updated_at`     bigint       DEFAULT NULL,
    `rec_version`        int          DEFAULT NULL,
    PRIMARY KEY (`account_id`),
    UNIQUE KEY `acc_account_owner_id_currency_chart_entry_id_UK` (`owner_id`, `currency`, `chart_entry_id`),
    KEY `acc_account_owner_id_IDX` (`owner_id`),
    KEY `acc_account_currency_IDX` (`currency`),
    KEY `acc_account_acc_chart_entry_FK` (`chart_entry_id`),
    CONSTRAINT `acc_account_acc_chart_entry_FK` FOREIGN KEY (`chart_entry_id`) REFERENCES `acc_chart_entry` (`chart_entry_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_account.acc_ledger_balance definition

CREATE TABLE `acc_ledger_balance`
(
    `ledger_balance_id` bigint      NOT NULL,
    `currency`          varchar(32) NOT NULL,
    `scale`             int         NOT NULL,
    `nature`            varchar(32) NOT NULL,
    `posted_debits`     decimal(34, 4) DEFAULT NULL,
    `posted_credits`    decimal(34, 4) DEFAULT NULL,
    `overdraft_mode`    varchar(32) NOT NULL,
    `overdraft_limit`   decimal(34, 4) DEFAULT NULL,
    `created_at`        bigint      NOT NULL,
    `rec_created_at`    bigint         DEFAULT NULL,
    `rec_updated_at`    bigint         DEFAULT NULL,
    `rec_version`       int            DEFAULT NULL,
    PRIMARY KEY (`ledger_balance_id`),
    KEY `ledger_balance_account_FK` (`ledger_balance_id`),
    CONSTRAINT `ledger_balance_account_FK` FOREIGN KEY (`ledger_balance_id`) REFERENCES `acc_account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_account.acc_ledger_movement definition

CREATE TABLE `acc_ledger_movement`
(
    `id`               bigint         NOT NULL,
    `account_id`       bigint         NOT NULL,
    `side`             varchar(32)    NOT NULL,
    `amount`           decimal(34, 4) NOT NULL,
    `old_debits`       decimal(34, 4) DEFAULT NULL,
    `old_credits`      decimal(34, 4) DEFAULT NULL,
    `new_debits`       decimal(34, 4) DEFAULT NULL,
    `new_credits`      decimal(34, 4) DEFAULT NULL,
    `transaction_id`   bigint         NOT NULL,
    `transaction_at`   bigint         NOT NULL,
    `transaction_type` varchar(32)    NOT NULL,
    `created_at`       bigint         NOT NULL,
    `rec_created_at`   bigint         DEFAULT NULL,
    `rec_updated_at`   bigint         DEFAULT NULL,
    `rec_version`      int            DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `acc_account_account_id_side_transaction_id_UK` (`account_id`, `side`, `transaction_id`),
    KEY `acc_account_transaction_id_IDX` (`transaction_id`),
    KEY `acc_account_transaction_at_IDX` (`transaction_at`),
    KEY `acc_account_account_id_transaction_at_IDX` (`account_id`, `transaction_at`),
    KEY `acc_account_account_id` (`account_id`),
    CONSTRAINT `acc_ledger_movement_acc_account_FK` FOREIGN KEY (`account_id`) REFERENCES `acc_account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
