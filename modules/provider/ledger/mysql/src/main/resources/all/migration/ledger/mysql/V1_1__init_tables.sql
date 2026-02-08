CREATE TABLE `lgr_ledger_balance`
(
    `account_id`      bigint      NOT NULL,
    `currency`        varchar(3)  NOT NULL,
    `scale`           int         NOT NULL,
    `nature`          varchar(32) NOT NULL,
    `posted_debits`   decimal(34, 4) DEFAULT NULL,
    `posted_credits`  decimal(34, 4) DEFAULT NULL,
    `overdraft_mode`  varchar(32) NOT NULL,
    `overdraft_limit` decimal(34, 4) DEFAULT NULL,
    `created_at`      bigint      NOT NULL,
    `rec_created_at`  bigint         DEFAULT NULL,
    `rec_updated_at`  bigint         DEFAULT NULL,
    `rec_version`     int            DEFAULT NULL,
    PRIMARY KEY (`account_id`),
    KEY `lgr_account_lgr_ledger_balance_FK_IDX` (`account_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `lgr_ledger_movement`
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
    UNIQUE KEY `lgr_ledger_movement_01_UK` (`account_id`, `side`, `transaction_id`),
    KEY `lgr_ledger_movement_01_IDX` (`transaction_id`),
    KEY `lgr_ledger_movement_02_IDX` (`transaction_at`),
    KEY `lgr_ledger_movement_03_IDX` (`account_id`, `transaction_at`),
    KEY `lgr_ledger_movement_04_IDX` (`account_id`),
    KEY `lgr_account_lgr_ledger_movement_FK_IDX` (`account_id`),
    CONSTRAINT `lgr_ledger_balance_lgr_ledger_movement_FK` FOREIGN KEY (`account_id`) REFERENCES `lgr_ledger_balance` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
