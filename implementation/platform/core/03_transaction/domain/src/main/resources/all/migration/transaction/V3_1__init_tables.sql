-- txn_transaction definition

CREATE TABLE `txn_transaction`
(
    `transaction_id` bigint      NOT NULL,
    `type`           varchar(32) NOT NULL,
    `phase`          varchar(32) NOT NULL,
    `open_at`        bigint      NOT NULL,
    `close_at`       bigint       DEFAULT NULL,
    `error`          varchar(255) DEFAULT NULL,
    `success`        tinyint(1)   DEFAULT 1,

    `rec_created_at` bigint       DEFAULT NULL,
    `rec_updated_at` bigint       DEFAULT NULL,
    `rec_version`    int          DEFAULT NULL,

    PRIMARY KEY (`transaction_id`),
    KEY `txn_transaction_type_phase_open_at_IDX` (`type`, `phase`, `open_at`),
    KEY `txn_transaction_type_phase_close_at_IDX` (`type`, `phase`, `close_at`),
    KEY `txn_transaction_open_at_IDX` (`open_at`),
    KEY `txn_transaction_close_at_IDX` (`close_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- txn_transaction_step definition

CREATE TABLE `txn_transaction_step`
(
    `step_id`        bigint       NOT NULL,
    `name`           varchar(255) NOT NULL,
    `context`        varchar(255) DEFAULT NULL,
    `phase`          varchar(32)  NOT NULL,
    `created_at`     bigint       NOT NULL,
    `transaction_id` bigint       NOT NULL,

    `rec_created_at` bigint       DEFAULT NULL,
    `rec_updated_at` bigint       DEFAULT NULL,
    `rec_version`    int          DEFAULT NULL,

    PRIMARY KEY (`step_id`),
    KEY `txn_transaction_step_transaction_id_IDX` (`transaction_id`),
    CONSTRAINT `txn_transaction_txn_transaction_step_FK` FOREIGN KEY (`transaction_id`) REFERENCES `txn_transaction` (`transaction_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- txn_step_param definition

CREATE TABLE `txn_step_param`
(
    `param_id`       bigint       NOT NULL,
    `param_name`     varchar(255) NOT NULL,
    `param_value`    varchar(255) NOT NULL,
    `step_id`        bigint       NOT NULL,

    `rec_created_at` bigint DEFAULT NULL,
    `rec_updated_at` bigint DEFAULT NULL,
    `rec_version`    int    DEFAULT NULL,

    PRIMARY KEY (`param_id`),
    KEY `txn_step_param_step_id_IDX` (`step_id`),
    CONSTRAINT `txn_transaction_step_txn_step_param_FK` FOREIGN KEY (`step_id`) REFERENCES `txn_transaction_step` (`step_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
