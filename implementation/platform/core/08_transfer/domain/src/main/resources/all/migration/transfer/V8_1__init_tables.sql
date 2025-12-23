CREATE TABLE `tfr_transfer`
(
    `transfer_id`                   bigint         NOT NULL,

    `transaction_id`                bigint         NOT NULL,
    `transaction_at`                bigint         NOT NULL,
    `udf_transfer_id`               varchar(48)    NOT NULL,

    `payer_fsp`                     varchar(32)    NOT NULL,
    `payer_party_type`              varchar(32)    DEFAULT NULL,
    `payer_party_id`                varchar(48)    DEFAULT NULL,
    `payer_sub_id`                  varchar(48)    DEFAULT NULL,

    `payee_fsp`                     varchar(32)    NOT NULL,
    `payee_party_type`              varchar(32)    DEFAULT NULL,
    `payee_party_id`                varchar(48)    DEFAULT NULL,
    `payee_sub_id`                  varchar(48)    DEFAULT NULL,

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

    `status`                        varchar(32)    NOT NULL,
    `received_at`                   bigint         NOT NULL,
    `reserved_at`                   bigint         DEFAULT NULL,
    `committed_at`                  bigint         DEFAULT NULL,

    `aborted_at`                    bigint         DEFAULT NULL,
    `abort_reason`                  varchar(32)    DEFAULT NULL,

    `dispute`                       tinyint(1)     DEFAULT NULL,
    `dispute_at`                    bigint         DEFAULT NULL,
    `dispute_reason`                varchar(32)    DEFAULT NULL,

    `dispute_resolved`              tinyint(1)     DEFAULT NULL,
    `dispute_resolved_at`           bigint         DEFAULT NULL,

    `reservation_timeout_at`        bigint         DEFAULT NULL,
    `payee_completed_at`            bigint         DEFAULT NULL,

    `ilp_fulfilment`                varchar(48)    DEFAULT NULL,

    `rec_created_at`                bigint         DEFAULT NULL,
    `rec_updated_at`                bigint         DEFAULT NULL,
    `rec_version`                   int            DEFAULT NULL,

    PRIMARY KEY (`transfer_id`),

    UNIQUE KEY `tfr_transfer_transaction_id_UK` (`transaction_id`),
    UNIQUE KEY `tfr_transfer_udf_transfer_id_UK` (`udf_transfer_id`),
    UNIQUE KEY `tfr_transfer_reservation_id_UK` (`reservation_id`),

    KEY `tfr_transfer_payer_fsp_IDX` (`payer_fsp`),
    KEY `tfr_transfer_payer_fsp_transaction_id_IDX` (`payer_fsp`, `transaction_id`),
    KEY `tfr_transfer_payee_fsp_IDX` (`payee_fsp`),
    KEY `tfr_transfer_payee_fsp_transaction_IDX` (`payee_fsp`, `transaction_id`),
    KEY `tfr_transfer_payee_fsp_payer_fsp_IDX` (`payee_fsp`, `payer_fsp`),
    KEY `tfr_transfer_transaction_at_IDX` (`transaction_at`),
    KEY `tfr_transfer_payer_party_id_IDX` (`payer_party_id`),
    KEY `tfr_transfer_payee_party_id_IDX` (`payee_party_id`),
    KEY `tfr_transfer_reservation_timeout_at_IDX` (`reservation_timeout_at`),
    KEY `tfr_transfer_reserved_at_IDX` (`reserved_at`),
    KEY `tfr_transfer_committed_at_IDX` (`committed_at`),
    KEY `tfr_transfer_aborted_at_IDX` (`aborted_at`),
    KEY `tfr_transfer_status_IDX` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- tfr_transfer_extension definition

CREATE TABLE `tfr_transfer_extension`
(
    `transfer_extension_id` bigint      NOT NULL,
    `direction`             varchar(32) NOT NULL,
    `x_key`                 varchar(64) NOT NULL,
    `x_value`               varchar(255) DEFAULT NULL,
    `transfer_id`           bigint      NOT NULL,

    `rec_created_at`        bigint       DEFAULT NULL,
    `rec_updated_at`        bigint       DEFAULT NULL,
    `rec_version`           int          DEFAULT NULL,

    PRIMARY KEY (`transfer_extension_id`),
    KEY `tfr_transfer_extension_transfer_id_IDX` (`transfer_id`),
    CONSTRAINT `transfer_extension_transfer_FK` FOREIGN KEY (`transfer_id`) REFERENCES `tfr_transfer` (`transfer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- tfr_transfer_ilp_packet definition

CREATE TABLE `tfr_transfer_ilp_packet`
(
    `transfer_id`    bigint NOT NULL,
    `ilp_packet`     MEDIUMTEXT  DEFAULT NULL,
    `ilp_condition`  varchar(48) DEFAULT NULL,

    `rec_created_at` bigint      DEFAULT NULL,
    `rec_updated_at` bigint      DEFAULT NULL,
    `rec_version`    int         DEFAULT NULL,

    PRIMARY KEY (`transfer_id`),
    UNIQUE KEY `tfr_transfer_ilp_packet_ilp_condition_UK` (`ilp_condition`),
    CONSTRAINT `transfer_ilp_packet_transfer_FK` FOREIGN KEY (`transfer_id`) REFERENCES `tfr_transfer` (`transfer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

