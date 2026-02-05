CREATE TABLE `tfr_transfer`
(
    `transfer_id`                   bigint         NOT NULL,

    `transaction_id`                bigint         NOT NULL,
    `transaction_at`                bigint         NOT NULL,
    `udf_transfer_id`               varchar(48)    NOT NULL,

    `payer_fsp_id`                  bigint         NOT NULL,
    `payer_party_type`              varchar(32)    DEFAULT NULL,
    `payer_party_id`                varchar(48)    DEFAULT NULL,
    `payer_sub_id`                  varchar(48)    DEFAULT NULL,

    `payee_fsp_id`                  bigint         NOT NULL,
    `payee_party_type`              varchar(32)    DEFAULT NULL,
    `payee_party_id`                varchar(48)    DEFAULT NULL,
    `payee_sub_id`                  varchar(48)    DEFAULT NULL,
    `amount_type`                   varchar(32)    DEFAULT NULL,
    `scenario`                      varchar(32)    DEFAULT NULL,
    `sub_scenario`                  varchar(128)   DEFAULT NULL,

    `transfer_currency`             varchar(3)     NOT NULL,
    `transfer_amount`               decimal(34, 4) NOT NULL,

    `payee_fsp_fee_currency`        varchar(3)     NOT NULL,
    `payee_fsp_fee_amount`          decimal(34, 4) DEFAULT NULL,

    `payee_fsp_commission_currency` varchar(3)     NOT NULL,
    `payee_fsp_commission_amount`   decimal(34, 4) DEFAULT NULL,

    `payee_receive_currency`        varchar(3)     NOT NULL,
    `payee_receive_amount`          decimal(34, 4) DEFAULT NULL,

    `request_expiration`            bigint         DEFAULT NULL,

    `reservation_id`                bigint         DEFAULT NULL,
    `payer_commit_id`               bigint         DEFAULT NULL,
    `payee_commit_id`               bigint         DEFAULT NULL,
    `rollback_id`                   bigint         DEFAULT NULL,

    `status`                        varchar(32)    NOT NULL,
    `received_at`                   bigint         NOT NULL,
    `reserved_at`                   bigint         DEFAULT NULL,
    `committed_at`                  bigint         DEFAULT NULL,

    `aborted_at`                    bigint         DEFAULT NULL,
    `abort_reason`                  varchar(64)    DEFAULT NULL,

    `dispute`                       tinyint(1)     DEFAULT NULL,
    `dispute_at`                    bigint         DEFAULT NULL,
    `dispute_reason`                varchar(64)    DEFAULT NULL,

    `dispute_resolved`              tinyint(1)     DEFAULT NULL,
    `dispute_resolved_at`           bigint         DEFAULT NULL,

    `reservation_timeout_at`        bigint         DEFAULT NULL,
    `payee_completed_at`            bigint         DEFAULT NULL,

    `ilp_fulfilment`                varchar(48)    DEFAULT NULL,

    `rec_created_at`                bigint         DEFAULT NULL,
    `rec_updated_at`                bigint         DEFAULT NULL,
    `rec_version`                   int            DEFAULT NULL,

    PRIMARY KEY (`transfer_id`),

    UNIQUE KEY `tfr_transfer_01_UK` (`transaction_id`),
    UNIQUE KEY `tfr_transfer_02_UK` (`udf_transfer_id`),
    UNIQUE KEY `tfr_transfer_03_UK` (`reservation_id`),
    UNIQUE KEY `tfr_transfer_04_UK` (`payer_commit_id`),
    UNIQUE KEY `tfr_transfer_05_UK` (`payee_commit_id`),
    UNIQUE KEY `tfr_transfer_06_UK` (`rollback_id`),
    UNIQUE KEY `tfr_transfer_07_UK` (`ilp_fulfilment`),

    KEY `tfr_transfer_01_IDX` (`payer_fsp_id`, `payee_fsp_id`,
                               `udf_transfer_id`),
    KEY `tfr_transfer_02_IDX` (`payer_fsp_id`),
    KEY `tfr_transfer_03_IDX` (`payer_fsp_id`, `transaction_id`),
    KEY `tfr_transfer_04_IDX` (`payee_fsp_id`),
    KEY `tfr_transfer_05_IDX` (`payee_fsp_id`, `transaction_id`),
    KEY `tfr_transfer_06_IDX` (`payee_fsp_id`, `payer_fsp_id`),
    KEY `tfr_transfer_07_IDX` (`transaction_at`),
    KEY `tfr_transfer_08_IDX` (`payer_party_id`),
    KEY `tfr_transfer_09_IDX` (`payee_party_id`),
    KEY `tfr_transfer_10_IDX` (`transfer_currency`),
    KEY `tfr_transfer_11_IDX` (`reservation_timeout_at`),
    KEY `tfr_transfer_12_IDX` (`reserved_at`),
    KEY `tfr_transfer_13_IDX` (`committed_at`),
    KEY `tfr_transfer_14_IDX` (`aborted_at`),
    KEY `tfr_transfer_15_IDX` (`status`),
    KEY `tfr_transfer_16_IDX` (`amount_type`),
    KEY `tfr_transfer_17_IDX` (`scenario`),
    KEY `tfr_transfer_18_IDX` (`scenario`, `sub_scenario`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_transfer.tfr_transfer_extension definition

CREATE TABLE `tfr_transfer_extension`
(
    `transfer_extension_id` bigint       NOT NULL,
    `direction`             varchar(32)  NOT NULL,
    `x_key`                 varchar(64)  NOT NULL,
    `x_value`               varchar(255) NOT NULL,
    `transfer_id`           bigint       NOT NULL,

    `rec_created_at`        bigint DEFAULT NULL,
    `rec_updated_at`        bigint DEFAULT NULL,
    `rec_version`           int    DEFAULT NULL,

    PRIMARY KEY (`transfer_extension_id`),
    KEY `tfr_transfer_tfr_transfer_extension_FK_IDX` (`transfer_id`),
    CONSTRAINT `tfr_transfer_tfr_transfer_extension_FK` FOREIGN KEY (`transfer_id`) REFERENCES `tfr_transfer` (`transfer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_transfer.tfr_transfer_ilp_packet definition

CREATE TABLE `tfr_transfer_ilp_packet`
(
    `transfer_id`    bigint NOT NULL,
    `ilp_packet`     MEDIUMTEXT  DEFAULT NULL,
    `ilp_condition`  varchar(48) DEFAULT NULL,

    `rec_created_at` bigint      DEFAULT NULL,
    `rec_updated_at` bigint      DEFAULT NULL,
    `rec_version`    int         DEFAULT NULL,

    PRIMARY KEY (`transfer_id`),
    UNIQUE KEY `tfr_transfer_ilp_packet_01_UK` (`ilp_condition`),
    KEY `tfr_transfer_tfr_transfer_ilp_packet_FK_IDX` (`transfer_id`),
    CONSTRAINT `tfr_transfer_tfr_transfer_ilp_packet_FK` FOREIGN KEY (`transfer_id`) REFERENCES `tfr_transfer` (`transfer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

