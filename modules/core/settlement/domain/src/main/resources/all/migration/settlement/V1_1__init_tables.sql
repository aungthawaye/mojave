CREATE TABLE `stm_settlement_definition`
(
    `settlement_definition_id` BIGINT       NOT NULL,
    `name`                     VARCHAR(100) NOT NULL,
    `payer_fsp_group_id`       BIGINT       NOT NULL,
    `payee_fsp_group_id`       BIGINT       NOT NULL,
    `currency`                 VARCHAR(3)   NOT NULL,
    `start_at`                 BIGINT       NOT NULL,
    `desired_provider_id`      BIGINT       NOT NULL,
    `activation_status`        VARCHAR(20)  NOT NULL,
    `rec_created_at`           BIGINT DEFAULT NULL,
    `rec_updated_at`           BIGINT DEFAULT NULL,
    `rec_version`              INT    DEFAULT NULL,
    PRIMARY KEY (`settlement_definition_id`),
    UNIQUE KEY `stm_settlement_definition_01_UK` (`payer_fsp_group_id`,
                                                  `payee_fsp_group_id`,
                                                  `currency`),
    UNIQUE KEY `stm_settlement_definition_02_UK` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `stm_settlement_record`
(
    `settlement_record_id` BIGINT         NOT NULL,
    `settlement_type`      VARCHAR(20)    NOT NULL,
    `settlement_id`        VARCHAR(255),
    `settlement_batch_id`  VARCHAR(255),
    `payer_fsp_id`         BIGINT         NOT NULL,
    `payee_fsp_id`         BIGINT         NOT NULL,
    `currency`             VARCHAR(3)     NOT NULL,
    `amount`               DECIMAL(34, 4) NOT NULL,
    `transfer_id`          BIGINT,
    `transaction_id`       BIGINT,
    `transaction_at`       BIGINT,
    `ssp_id`               BIGINT         NOT NULL,
    `initiated_at`         BIGINT,
    `prepared_at`          BIGINT,
    `completed_at`         BIGINT,
    `rec_created_at`       BIGINT DEFAULT NULL,
    `rec_updated_at`       BIGINT DEFAULT NULL,
    `rec_version`          INT    DEFAULT NULL,
    PRIMARY KEY (`settlement_record_id`),
    UNIQUE KEY `stm_settlement_record_01_UK` (`settlement_id`,
                                              `settlement_batch_id`,
                                              `ssp_id`),
    UNIQUE KEY `stm_settlement_record_02_UK` (`transaction_id`),
    UNIQUE KEY `stm_settlement_record_03_UK` (`transfer_id`),
    KEY `stm_settlement_record_01_IDX` (`payer_fsp_id`, `payee_fsp_id`, `currency`),
    KEY `stm_settlement_record_02_IDX` (`transaction_at`),
    KEY `stm_settlement_record_03_IDX` (`initiated_at`),
    KEY `stm_settlement_record_04_IDX` (`prepared_at`),
    KEY `stm_settlement_record_05_IDX` (`completed_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
