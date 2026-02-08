-- ml_accounting.acc_chart definition

CREATE TABLE `acc_chart`
(
    `chart_id`       bigint      NOT NULL,
    `name`           varchar(64) NOT NULL,
    `created_at`     bigint      NOT NULL,
    `rec_created_at` bigint DEFAULT NULL,
    `rec_updated_at` bigint DEFAULT NULL,
    `rec_version`    int    DEFAULT NULL,
    PRIMARY KEY (`chart_id`),
    UNIQUE KEY `acc_chart_01_UK` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_accounting.acc_chart_entry definition

CREATE TABLE `acc_chart_entry`
(
    `chart_entry_id`   bigint       NOT NULL,
    `category`         varchar(32)  NOT NULL,
    `chart_entry_code` varchar(32)  NOT NULL,
    `name`             varchar(64)  NOT NULL,
    `description`      varchar(255) NOT NULL,
    `account_type`     varchar(32)  NOT NULL,
    `created_at`       bigint       NOT NULL,
    `chart_id`         bigint       NOT NULL,
    `rec_created_at`   bigint DEFAULT NULL,
    `rec_updated_at`   bigint DEFAULT NULL,
    `rec_version`      int    DEFAULT NULL,
    PRIMARY KEY (`chart_entry_id`),
    UNIQUE KEY `acc_chart_entry_01_UK` (`chart_entry_code`),
    KEY `acc_chart_acc_chart_entry_FK_IDX` (`chart_id`),
    CONSTRAINT `acc_chart_acc_chart_entry_FK` FOREIGN KEY (`chart_id`) REFERENCES `acc_chart` (`chart_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_accounting.acc_account definition

CREATE TABLE `acc_account`
(
    `account_id`         bigint      NOT NULL,
    `owner_id`           bigint      NOT NULL,
    `type`               varchar(32) NOT NULL,
    `currency`           varchar(3)  NOT NULL,
    `code`               varchar(32) NOT NULL,
    `name`               varchar(64) NOT NULL,
    `description`        varchar(255) DEFAULT NULL,
    `created_at`         bigint      NOT NULL,
    `activation_status`  varchar(32) NOT NULL,
    `termination_status` varchar(32) NOT NULL,
    `chart_entry_id`     bigint      NOT NULL,
    `rec_created_at`     bigint       DEFAULT NULL,
    `rec_updated_at`     bigint       DEFAULT NULL,
    `rec_version`        int          DEFAULT NULL,
    PRIMARY KEY (`account_id`),
    UNIQUE KEY `acc_account_01_UK` (`owner_id`, `currency`, `chart_entry_id`),
    KEY `acc_account_01_IDX` (`owner_id`),
    KEY `acc_account_02_IDX` (`currency`),
    KEY `acc_chart_entry_acc_account_FK_IDX` (`chart_entry_id`),
    CONSTRAINT `acc_chart_entry_acc_account_FK` FOREIGN KEY (`chart_entry_id`) REFERENCES `acc_chart_entry` (`chart_entry_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE `acc_ledger_balance`
(
    `ledger_balance_id` bigint      NOT NULL,
    `currency`          varchar(3)  NOT NULL,
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
    KEY `acc_account_acc_ledger_balance_FK_IDX` (`ledger_balance_id`),
    CONSTRAINT `acc_account_acc_ledger_balance_FK` FOREIGN KEY (`ledger_balance_id`) REFERENCES `acc_account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `acc_ledger_movement`
(
    `ledger_movement_id`    bigint         NOT NULL,
    `step`                  int            NOT NULL,
    `account_id`            bigint         NOT NULL,
    `side`                  varchar(32)    NOT NULL,
    `currency`              varchar(3)     NOT NULL,
    `amount`                decimal(34, 4) NOT NULL,
    `old_debits`            decimal(34, 4) DEFAULT NULL,
    `old_credits`           decimal(34, 4) DEFAULT NULL,
    `new_debits`            decimal(34, 4) DEFAULT NULL,
    `new_credits`           decimal(34, 4) DEFAULT NULL,
    `transaction_id`        bigint         NOT NULL,
    `transaction_at`        bigint         NOT NULL,
    `transaction_type`      varchar(32)    NOT NULL,
    `flow_definition_id`    bigint         NOT NULL,
    `posting_definition_id` bigint         NOT NULL,
    `movement_stage`        varchar(32)    NOT NULL,
    `movement_result`       varchar(32)    NOT NULL,
    `created_at`            bigint         NOT NULL,
    `rec_created_at`        bigint         DEFAULT NULL,
    `rec_updated_at`        bigint         DEFAULT NULL,
    `rec_version`           int            DEFAULT NULL,
    PRIMARY KEY (`ledger_movement_id`),
    UNIQUE KEY `acc_ledger_movement_01_UK` (`account_id`, `side`, `transaction_id`),
    KEY `acc_ledger_movement_01_IDX` (`transaction_id`),
    KEY `acc_ledger_movement_02_IDX` (`transaction_at`),
    KEY `acc_ledger_movement_03_IDX` (`account_id`, `transaction_at`),
    KEY `acc_ledger_movement_04_IDX` (`account_id`),
    KEY `acc_account_acc_ledger_movement_FK_IDX` (`account_id`),
    CONSTRAINT `acc_account_acc_ledger_movement_FK` FOREIGN KEY (`account_id`) REFERENCES `acc_account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_accounting.acc_flow_definition definition

CREATE TABLE `acc_flow_definition`
(
    `flow_definition_id` bigint      NOT NULL,
    `transaction_type`   varchar(32) NOT NULL,
    `currency`           varchar(3)  NOT NULL,
    `name`               varchar(64) NOT NULL,
    `description`        varchar(255) DEFAULT NULL,
    `activation_status`  varchar(32) NOT NULL,
    `termination_status` varchar(32) NOT NULL,
    `rec_created_at`     bigint       DEFAULT NULL,
    `rec_updated_at`     bigint       DEFAULT NULL,
    `rec_version`        int          DEFAULT NULL,
    PRIMARY KEY (`flow_definition_id`),
    UNIQUE KEY `acc_flow_definition_01_UK` (`transaction_type`, `currency`),
    UNIQUE KEY `acc_flow_definition_02_UK` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_accounting.acc_posting_definition definition

CREATE TABLE `acc_posting_definition`
(
    `posting_definition_id` bigint      NOT NULL,
    `participant`           varchar(64)          DEFAULT NULL,
    `amount_name`           varchar(64) NOT NULL,
    `side`                  varchar(32) NOT NULL,
    `posting_channel`       varchar(32) NOT NULL,
    `posting_channel_id`    bigint      NOT NULL,
    `description`           varchar(255)         DEFAULT NULL,
    `step`                  int         NOT NULL DEFAULT 0,
    `definition_id`         bigint      NOT NULL,
    `rec_created_at`        bigint               DEFAULT NULL,
    `rec_updated_at`        bigint               DEFAULT NULL,
    `rec_version`           int                  DEFAULT NULL,
    PRIMARY KEY (`posting_definition_id`),
    UNIQUE KEY `acc_posting_definition_01_UK` (`definition_id`,
                                               `participant`,
                                               `amount_name`, `side`,
                                               `posting_channel`,
                                               `posting_channel_id`),
    UNIQUE KEY `acc_posting_definition_02_UK` (`definition_id`, `step`),
    KEY `acc_flow_definition_acc_posting_definition_FK_IDX` (`definition_id`),
    CONSTRAINT `acc_flow_definition_acc_posting_definition_FK` FOREIGN KEY (`definition_id`) REFERENCES `acc_flow_definition` (`flow_definition_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
