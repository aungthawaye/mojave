-- txn_fund_in_definition definition

CREATE TABLE `txn_fund_in_definition`
(
    `definition_id`      bigint       NOT NULL,
    `currency`           varchar(3)   NOT NULL,
    `name`               varchar(64)  NOT NULL,
    `description`        varchar(255) DEFAULT NULL,
    `activation_status`  varchar(32)  NOT NULL,
    `termination_status` varchar(32)  NOT NULL,

    `rec_created_at`     bigint       DEFAULT NULL,
    `rec_updated_at`     bigint       DEFAULT NULL,
    `rec_version`        int          DEFAULT NULL,

    PRIMARY KEY (`definition_id`),
    UNIQUE KEY `trn_fund_in_definition_currency_UK` (`currency`),
    UNIQUE KEY `trn_fund_in_definition_name_UK` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- txn_fund_in_posting definition

CREATE TABLE `txn_fund_in_posting`
(
    `posting_id`      bigint       NOT NULL,
    `for_owner`      varchar(32)  NOT NULL,
    `for_amount`     varchar(32)  NOT NULL,
    `side`           varchar(32)  NOT NULL,
    `chart_entry_id` bigint       NOT NULL,
    `description`    varchar(255) DEFAULT NULL,
    `definition_id`  bigint       NOT NULL,

    `rec_created_at` bigint       DEFAULT NULL,
    `rec_updated_at` bigint       DEFAULT NULL,
    `rec_version`    int          DEFAULT NULL,

    PRIMARY KEY (`posting_id`),
    UNIQUE KEY `trn_fund_in_posting_for_posting_UK` (`for_owner`, `for_amount`, `chart_entry_id`),
    KEY `txn_fund_in_posting_definition_id_IDX` (`definition_id`),
    CONSTRAINT `fund_in_posting_fund_in_definition_FK` FOREIGN KEY (`definition_id`) REFERENCES `txn_fund_in_definition` (`definition_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- txn_fund_out_definition definition

CREATE TABLE `txn_fund_out_definition`
(
    `definition_id`      bigint       NOT NULL,
    `currency`           varchar(3)   NOT NULL,
    `name`               varchar(64)  NOT NULL,
    `description`        varchar(255) DEFAULT NULL,
    `activation_status`  varchar(32)  NOT NULL,
    `termination_status` varchar(32)  NOT NULL,

    `rec_created_at`     bigint       DEFAULT NULL,
    `rec_updated_at`     bigint       DEFAULT NULL,
    `rec_version`        int          DEFAULT NULL,

    PRIMARY KEY (`definition_id`),
    UNIQUE KEY `trn_fund_out_definition_currency_UK` (`currency`),
    UNIQUE KEY `trn_fund_out_definition_name_UK` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- txn_fund_out_posting definition

CREATE TABLE `txn_fund_out_posting`
(
    `posting_id`      bigint       NOT NULL,
    `for_owner`      varchar(32)  NOT NULL,
    `for_amount`     varchar(32)  NOT NULL,
    `side`           varchar(32)  NOT NULL,
    `chart_entry_id` bigint       NOT NULL,
    `description`    varchar(255) DEFAULT NULL,
    `definition_id`  bigint       NOT NULL,

    `rec_created_at` bigint       DEFAULT NULL,
    `rec_updated_at` bigint       DEFAULT NULL,
    `rec_version`    int          DEFAULT NULL,

    PRIMARY KEY (`posting_id`),
    UNIQUE KEY `trn_fund_out_posting_for_posting_UK` (`for_owner`, `for_amount`, `chart_entry_id`),
    KEY `txn_fund_out_posting_definition_id_IDX` (`definition_id`),
    CONSTRAINT `fund_out_posting_fund_out_definition_FK` FOREIGN KEY (`definition_id`) REFERENCES `txn_fund_out_definition` (`definition_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- txn_fund_transfer_definition definition

CREATE TABLE `txn_fund_transfer_definition`
(
    `definition_id`      bigint       NOT NULL,
    `currency`           varchar(3)   NOT NULL,
    `name`               varchar(64)  NOT NULL,
    `description`        varchar(255) DEFAULT NULL,
    `activation_status`  varchar(32)  NOT NULL,
    `termination_status` varchar(32)  NOT NULL,

    `rec_created_at`     bigint       DEFAULT NULL,
    `rec_updated_at`     bigint       DEFAULT NULL,
    `rec_version`        int          DEFAULT NULL,

    PRIMARY KEY (`definition_id`),
    UNIQUE KEY `trn_fund_transfer_definition_currency_UK` (`currency`),
    UNIQUE KEY `trn_fund_transfer_definition_name_UK` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- txn_fund_transfer_posting definition

CREATE TABLE `txn_fund_transfer_posting`
(
    `posting_id`      bigint       NOT NULL,
    `for_owner`      varchar(32)  NOT NULL,
    `for_amount`     varchar(32)  NOT NULL,
    `side`           varchar(32)  NOT NULL,
    `chart_entry_id` bigint       NOT NULL,
    `description`    varchar(255) DEFAULT NULL,
    `definition_id`  bigint       NOT NULL,

    `rec_created_at` bigint       DEFAULT NULL,
    `rec_updated_at` bigint       DEFAULT NULL,
    `rec_version`    int          DEFAULT NULL,

    PRIMARY KEY (`posting_id`),
    UNIQUE KEY `trn_fund_in_transfer_for_posting_UK` (`for_owner`, `for_amount`, `chart_entry_id`),
    KEY `txn_fund_transfer_posting_definition_id_IDX` (`definition_id`),
    CONSTRAINT `fund_transfer_posting_fund_transfer_definition_FK` FOREIGN KEY (`definition_id`) REFERENCES `txn_fund_transfer_definition` (`definition_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
