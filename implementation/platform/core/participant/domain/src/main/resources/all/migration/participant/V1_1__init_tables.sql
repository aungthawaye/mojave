-- ml_participant.pcp_fsp definition

CREATE TABLE `pcp_fsp`
(
    `fsp_id`             bigint      NOT NULL,
    `fsp_code`           varchar(24) NOT NULL,
    `name`               varchar(64) NOT NULL,
    `activation_status`  varchar(24) NOT NULL,
    `termination_status` varchar(24) NOT NULL,
    `created_at`         bigint      NOT NULL,
    `rec_created_at`     bigint DEFAULT NULL,
    `rec_updated_at`     bigint DEFAULT NULL,
    `rec_version`        int    DEFAULT NULL,
    PRIMARY KEY (`fsp_id`),
    UNIQUE KEY `pcp_fsp_unique1` (`fsp_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_participant.pcp_endpoint definition

CREATE TABLE `pcp_endpoint`
(
    `endpoint_id`       bigint                                                       NOT NULL,
    `type`              varchar(24)                                                  NOT NULL,
    `host`              varchar(256)                                                 NOT NULL,
    `activation_status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `fsp_id`            bigint                                                       NOT NULL,
    `created_at`        bigint                                                       NOT NULL,
    `rec_created_at`    bigint DEFAULT NULL,
    `rec_updated_at`    bigint DEFAULT NULL,
    `rec_version`       int    DEFAULT NULL,
    PRIMARY KEY (`endpoint_id`),
    UNIQUE KEY `pcp_endpoint_unique1` (`type`, `fsp_id`),
    KEY `pcp_endpoint_pcp_fsp_FK` (`fsp_id`),
    CONSTRAINT `pcp_endpoint_pcp_fsp_FK` FOREIGN KEY (`fsp_id`) REFERENCES `pcp_fsp` (`fsp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_participant.pcp_supported_currency definition

CREATE TABLE `pcp_supported_currency`
(
    `supported_currency_id` bigint      NOT NULL,
    `currency`              varchar(3)  NOT NULL,
    `activation_status`     varchar(24) NOT NULL,
    `created_at`            bigint      NOT NULL,
    `fsp_id`                bigint      NOT NULL,
    `rec_created_at`        bigint DEFAULT NULL,
    `rec_updated_at`        bigint DEFAULT NULL,
    `rec_version`           int    DEFAULT NULL,
    PRIMARY KEY (`supported_currency_id`),
    UNIQUE KEY `pcp_supported_currency_unique1` (`currency`, `fsp_id`),
    KEY `pcp_supported_currency_pcp_fsp_FK` (`fsp_id`),
    CONSTRAINT `pcp_supported_currency_pcp_fsp_FK` FOREIGN KEY (`fsp_id`) REFERENCES `pcp_fsp` (`fsp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;