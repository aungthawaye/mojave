-- ml_quoting.qot_quote definition

CREATE TABLE `qot_quote`
(
    `quote_id`             bigint         NOT NULL,
    `payer_fsp_id`         bigint         NOT NULL,
    `payee_fsp_id`         bigint         NOT NULL,
    `udf_quote_id`         varchar(48)    NOT NULL,
    `currency`             varchar(3)     NOT NULL,
    `amount`               decimal(34, 4) NOT NULL,
    `fees`                 decimal(34, 4) DEFAULT NULL,
    `amount_type`          varchar(32)    NOT NULL,
    `scenario`             varchar(32)    NOT NULL,
    `sub_scenario`         varchar(64)    DEFAULT NULL,
    `initiator`            varchar(32)    NOT NULL,
    `initiator_type`       varchar(32)    NOT NULL,
    `request_expiration`   bigint         DEFAULT NULL,

    `payer_party_type`     varchar(32)    NOT NULL,
    `payer_party_id`       varchar(48)    NOT NULL,
    `payer_sub_id`         varchar(48)    DEFAULT NULL,

    `payee_party_type`     varchar(32)    NOT NULL,
    `payee_party_id`       varchar(48)    NOT NULL,
    `payee_sub_id`         varchar(48)    DEFAULT NULL,

    `response_expiration`  bigint         DEFAULT NULL,
    `transfer_amount`      decimal(34, 4) DEFAULT NULL,
    `payee_fsp_fee`        decimal(34, 4) DEFAULT NULL,
    `payee_fsp_commission` decimal(34, 4) DEFAULT NULL,
    `payee_receive_amount` decimal(34, 4) DEFAULT NULL,

    `requested_at`         bigint         NOT NULL,
    `responded_at`         bigint         DEFAULT NULL,
    `stage`                varchar(32)    NOT NULL,
    `error`                varchar(255)   DEFAULT NULL,

    `rec_created_at`       bigint         DEFAULT NULL,
    `rec_updated_at`       bigint         DEFAULT NULL,
    `rec_version`          int            DEFAULT NULL,

    PRIMARY KEY (`quote_id`),
    UNIQUE KEY `qot_quote_udf_quote_id_UK` (`udf_quote_id`),

    KEY                    `qot_quote_requested_at_IDX` (`requested_at`),
    KEY                    `qot_quote_responded_at_IDX` (`responded_at`),
    KEY                    `qot_quote_currency_IDX` (`currency`),
    KEY                    `qot_quote_amount_type_IDX` (`amount_type`),
    KEY                    `qot_quote_payer_fsp_id_payee_fsp_id_IDX` (`payer_fsp_id`, `payee_fsp_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_quoting.qot_quote_extension definition

CREATE TABLE `qot_quote_extension`
(
    `quote_extension_id` bigint       NOT NULL,
    `direction`          varchar(32)  NOT NULL,
    `x_key`              varchar(64)  NOT NULL,
    `x_value`            varchar(256) NOT NULL,
    `quote_id`           bigint       NOT NULL,

    `rec_created_at`     bigint DEFAULT NULL,
    `rec_updated_at`     bigint DEFAULT NULL,
    `rec_version`        int    DEFAULT NULL,

    PRIMARY KEY (`quote_extension_id`),
    KEY                  `qot_quote_extension_quote_id_IDX` (`quote_id`),
    CONSTRAINT `quote_extension_quote_FK` FOREIGN KEY (`quote_id`) REFERENCES `qot_quote` (`quote_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ml_quoting.qot_quote_ilp_packet definition

CREATE TABLE `qot_quote_ilp_packet`
(
    `quote_id`       bigint NOT NULL,
    `ilp_packet`     MEDIUMTEXT  DEFAULT NULL,
    `ilp_condition`  varchar(48) DEFAULT NULL,

    `rec_created_at` bigint      DEFAULT NULL,
    `rec_updated_at` bigint      DEFAULT NULL,
    `rec_version`    int         DEFAULT NULL,

    PRIMARY KEY (`quote_id`),
    KEY              `qot_quote_ilp_packet_quote_id_IDX` (`quote_id`),
    CONSTRAINT `quote_ilp_packet_quote_FK` FOREIGN KEY (`quote_id`) REFERENCES `qot_quote` (`quote_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
