CREATE TABLE `tfr_transfer`
(
    `transfer_id`            bigint         NOT NULL,

    `transaction_id`         bigint         NOT NULL,
    `transaction_at`         bigint         NOT NULL,
    `udf_transfer_id`        varchar(48)    NOT NULL,

    `payer_fsp`              varchar(32)    NOT NULL,
    `payer_party_type`       varchar(32)  DEFAULT NULL,
    `payer_party_id`         varchar(48)  DEFAULT NULL,
    `payer_sub_id`           varchar(48)  DEFAULT NULL,

    `payee_fsp`              varchar(32)    NOT NULL,
    `payee_party_type`       varchar(32)  DEFAULT NULL,
    `payee_party_id`         varchar(48)  DEFAULT NULL,
    `payee_sub_id`           varchar(48)  DEFAULT NULL,

    `currency`               varchar(3)     NOT NULL,
    `transfer_amount`        decimal(34, 4) NOT NULL,

    `request_expiration`     bigint       DEFAULT NULL,
    `ilp_condition`          varchar(48)  DEFAULT NULL,
    `ilp_fulfilment`         varchar(48)  DEFAULT NULL,

    `reservation_id`         bigint       DEFAULT NULL,
    `payer_commit_id`        bigint       DEFAULT NULL,
    `payee_commit_id`        bigint       DEFAULT NULL,
    `rollback_id`            bigint       DEFAULT NULL,

    `state`                  varchar(32)    NOT NULL,
    `received_at`            bigint         NOT NULL,
    `reserved_at`            bigint       DEFAULT NULL,
    `committed_at`           bigint       DEFAULT NULL,
    `error`                  varchar(255) DEFAULT NULL,

    `reservation_timeout_at` bigint       DEFAULT NULL,
    `payee_completed_at`     bigint       DEFAULT NULL,

    `rec_created_at`         bigint       DEFAULT NULL,
    `rec_updated_at`         bigint       DEFAULT NULL,
    `rec_version`            int          DEFAULT NULL,

    PRIMARY KEY (`transfer_id`),

    UNIQUE KEY `tfr_transfer_transaction_id_UK` (`transaction_id`),
    UNIQUE KEY `tfr_transfer_udf_transfer_id_UK` (`udf_transfer_id`),
    UNIQUE KEY `tfr_transfer_reservation_id_UK` (`reservation_id`),
    UNIQUE KEY `tfr_transfer_payer_commit_id_UK` (`payer_commit_id`),
    UNIQUE KEY `tfr_transfer_payee_commit_id_UK` (`payee_commit_id`),
    UNIQUE KEY `tfr_transfer_rollback_id_UK` (`rollback_id`),

    KEY `tfr_transfer_payer_fsp_IDX` (`payer_fsp`),
    KEY `tfr_transfer_payer_fsp_transaction_id_IDX` (`payer_fsp`, `transaction_id`),
    KEY `tfr_transfer_payee_fsp_IDX` (`payee_fsp`),
    KEY `tfr_transfer_payee_fsp_transaction_IDX` (`payee_fsp`, `transaction_id`),
    KEY `tfr_transfer_payee_fsp_payer_fsp_IDX` (`payee_fsp`, `payer_fsp`),
    KEY `tfr_transfer_transaction_at_IDX` (`transaction_at`),
    KEY `tfr_transfer_payer_party_id` (`payer_party_id`),
    KEY `tfr_transfer_payee_party_id` (`payee_party_id`),
    KEY `tfr_transfer_reservation_timeout_at` (`reservation_timeout_at`)
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

