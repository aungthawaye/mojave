-- ml_settlement.stm_filter_group definition

CREATE TABLE `stm_filter_group`
(
    `filter_group_id` bigint      NOT NULL,
    `name`            varchar(64) NOT NULL,
    `rec_created_at`  bigint DEFAULT NULL,
    `rec_updated_at`  bigint DEFAULT NULL,
    `rec_version`     int    DEFAULT NULL,
    PRIMARY KEY (`filter_group_id`),
    UNIQUE KEY `stm_filter_group_01_UK` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_settlement.stm_filter_item definition

CREATE TABLE `stm_filter_item`
(
    `filter_item_id`  bigint NOT NULL,
    `fsp_id`          bigint NOT NULL,
    `filter_group_id` bigint NOT NULL,
    `rec_created_at`  bigint DEFAULT NULL,
    `rec_updated_at`  bigint DEFAULT NULL,
    `rec_version`     int    DEFAULT NULL,
    PRIMARY KEY (`filter_item_id`),
    UNIQUE KEY `stm_filter_item_01_UK` (`filter_group_id`, `fsp_id`),
    KEY `stm_filter_group_stm_filter_item_FK_IDX` (`filter_group_id`),
    CONSTRAINT `stm_filter_group_stm_filter_item_FK` FOREIGN KEY (`filter_group_id`) REFERENCES `stm_filter_group` (`filter_group_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_settlement.stm_settlement_definition definition

CREATE TABLE `stm_settlement_definition`
(
    `settlement_definition_id` bigint      NOT NULL,
    `name`                     varchar(64) NOT NULL,
    `payer_filter_group_id`    bigint      NOT NULL,
    `payee_filter_group_id`    bigint      NOT NULL,
    `currency`                 varchar(3)  NOT NULL,
    `start_at`                 bigint      NOT NULL,
    `desired_provider_id`      bigint      NOT NULL,
    `activation_status`        varchar(32) NOT NULL,
    `rec_created_at`           bigint DEFAULT NULL,
    `rec_updated_at`           bigint DEFAULT NULL,
    `rec_version`              int    DEFAULT NULL,
    PRIMARY KEY (`settlement_definition_id`),
    UNIQUE KEY `stm_settlement_definition_01_UK` (`payer_filter_group_id`,
                                                  `payee_filter_group_id`,
                                                  `currency`),
    UNIQUE KEY `stm_settlement_definition_02_UK` (`name`),
    KEY `stm_filter_group_stm_settlement_definition_payer_FK_IDX` (`payer_filter_group_id`),
    KEY `stm_filter_group_stm_settlement_definition_payee_FK_IDX` (`payee_filter_group_id`),
    CONSTRAINT `stm_filter_group_stm_settlement_definition_payer_FK` FOREIGN KEY (`payer_filter_group_id`) REFERENCES `stm_filter_group` (`filter_group_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `stm_filter_group_stm_settlement_definition_payee_FK` FOREIGN KEY (`payee_filter_group_id`) REFERENCES `stm_filter_group` (`filter_group_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_settlement.stm_settlement_record definition

CREATE TABLE `stm_settlement_record`
(
    `settlement_record_id`   bigint         NOT NULL,
    `settlement_type`        varchar(32)    NOT NULL,
    `settlement_id`          bigint DEFAULT NULL,
    `settlement_batch_id`    bigint DEFAULT NULL,
    `payer_fsp_id`           bigint         NOT NULL,
    `payee_fsp_id`           bigint         NOT NULL,
    `currency`               varchar(3)     NOT NULL,
    `amount`                 decimal(34, 4) NOT NULL,
    `transfer_id`            bigint DEFAULT NULL,
    `transaction_id`         bigint DEFAULT NULL,
    `transaction_at`         bigint DEFAULT NULL,
    `settlement_provider_id` bigint         NOT NULL,
    `initiated_at`           bigint DEFAULT NULL,
    `prepared_at`            bigint DEFAULT NULL,
    `completed_at`           bigint DEFAULT NULL,
    `rec_created_at`         bigint DEFAULT NULL,
    `rec_updated_at`         bigint DEFAULT NULL,
    `rec_version`            int    DEFAULT NULL,
    PRIMARY KEY (`settlement_record_id`),
    KEY `stm_settlement_record_01_IDX` (`transaction_id`, `transfer_id`),
    KEY `stm_settlement_record_02_IDX` (`settlement_id`, `settlement_batch_id`),
    KEY `stm_settlement_record_03_IDX` (`payer_fsp_id`, `payee_fsp_id`, `currency`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
