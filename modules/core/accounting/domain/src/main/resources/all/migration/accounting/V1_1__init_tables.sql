-- ml_accounting.acc_coa definition

CREATE TABLE `acc_coa`
(
    `coa_id`         bigint      NOT NULL,
    `name`           varchar(64) NOT NULL,
    `created_at`     bigint      NOT NULL,
    `rec_created_at` bigint DEFAULT NULL,
    `rec_updated_at` bigint DEFAULT NULL,
    `rec_version`    int    DEFAULT NULL,
    PRIMARY KEY (`coa_id`),
    UNIQUE KEY `acc_coa_01_UK` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_accounting.acc_coa_entry definition

CREATE TABLE `acc_coa_entry`
(
    `coa_entry_id`   bigint       NOT NULL,
    `category`       varchar(32)  NOT NULL,
    `coa_entry_code` varchar(64)  NOT NULL,
    `name`           varchar(64)  NOT NULL,
    `description`    varchar(255) NOT NULL,
    `account_type`   varchar(32)  NOT NULL,
    `created_at`     bigint       NOT NULL,
    `coa_id`         bigint       NOT NULL,
    `rec_created_at` bigint DEFAULT NULL,
    `rec_updated_at` bigint DEFAULT NULL,
    `rec_version`    int    DEFAULT NULL,
    PRIMARY KEY (`coa_entry_id`),
    UNIQUE KEY `acc_coa_entry_01_UK` (`coa_entry_code`),
    KEY `acc_coa_acc_coa_entry_FK_IDX` (`coa_id`),
    CONSTRAINT `acc_coa_acc_coa_entry_FK` FOREIGN KEY (`coa_id`) REFERENCES `acc_coa` (`coa_id`) ON DELETE CASCADE ON UPDATE CASCADE
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
    `code`               varchar(64) NOT NULL,
    `name`               varchar(64) NOT NULL,
    `description`        varchar(255) DEFAULT NULL,
    `created_at`         bigint      NOT NULL,
    `activation_status`  varchar(32) NOT NULL,
    `termination_status` varchar(32) NOT NULL,
    `coa_entry_id`       bigint      NOT NULL,
    `rec_created_at`     bigint       DEFAULT NULL,
    `rec_updated_at`     bigint       DEFAULT NULL,
    `rec_version`        int          DEFAULT NULL,
    PRIMARY KEY (`account_id`),
    UNIQUE KEY `acc_account_01_UK` (`owner_id`, `currency`, `coa_entry_id`),
    KEY `acc_account_01_IDX` (`owner_id`),
    KEY `acc_account_02_IDX` (`currency`),
    KEY `acc_coa_entry_acc_account_FK_IDX` (`coa_entry_id`),
    CONSTRAINT `acc_coa_entry_acc_account_FK` FOREIGN KEY (`coa_entry_id`) REFERENCES `acc_coa_entry` (`coa_entry_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ml_accounting.acc_flow_definition definition

CREATE TABLE `acc_flow_definition`
(
    `flow_definition_id` bigint      NOT NULL,
    `scenario`           varchar(32) NOT NULL,
    `currency`           varchar(3)  NOT NULL,
    `name`               varchar(64) NOT NULL,
    `description`        varchar(255) DEFAULT NULL,
    `activation_status`  varchar(32) NOT NULL,
    `termination_status` varchar(32) NOT NULL,
    `rec_created_at`     bigint       DEFAULT NULL,
    `rec_updated_at`     bigint       DEFAULT NULL,
    `rec_version`        int          DEFAULT NULL,
    PRIMARY KEY (`flow_definition_id`),
    UNIQUE KEY `acc_flow_definition_01_UK` (`scenario`, `currency`),
    UNIQUE KEY `acc_flow_definition_02_UK` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_accounting.acc_flow_definition_line definition

CREATE TABLE `acc_flow_definition_line`
(
    `flow_definition_line_id`   bigint      NOT NULL,
    `participant`    varchar(64)          DEFAULT NULL,
    `amount_name`    varchar(64) NOT NULL,
    `side`           varchar(32) NOT NULL,
    `coa_entry_id`   bigint      NOT NULL,
    `description`    varchar(255)         DEFAULT NULL,
    `step`           int         NOT NULL DEFAULT 0,
    `definition_id`  bigint      NOT NULL,
    `rec_created_at` bigint               DEFAULT NULL,
    `rec_updated_at` bigint               DEFAULT NULL,
    `rec_version`    int                  DEFAULT NULL,
    PRIMARY KEY (`flow_definition_line_id`),
    UNIQUE KEY `acc_flow_definition_line_01_UK` (`definition_id`,
                                      `participant`,
                                      `amount_name`,
                                      `side`,
                                      `coa_entry_id`),
    UNIQUE KEY `acc_flow_definition_line_02_UK` (`definition_id`, `step`),
    KEY `acc_coa_entry_acc_flow_definition_line_FK_IDX` (`coa_entry_id`),
    KEY `acc_flow_definition_acc_flow_definition_line_FK_IDX` (`definition_id`),
    CONSTRAINT `acc_coa_entry_acc_flow_definition_line_FK` FOREIGN KEY (`coa_entry_id`) REFERENCES `acc_coa_entry` (`coa_entry_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `acc_flow_definition_acc_flow_definition_line_FK` FOREIGN KEY (`definition_id`) REFERENCES `acc_flow_definition` (`flow_definition_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
