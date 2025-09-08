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
    UNIQUE KEY `uk_fsp_code` (`fsp_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_participant.pcp_fsp_endpoint definition

CREATE TABLE `pcp_fsp_endpoint`
(
    `fsp_endpoint_id`   bigint                                                       NOT NULL,
    `type`              varchar(24)                                                  NOT NULL,
    `base_url`          varchar(2048)                                                NOT NULL,
    `activation_status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `fsp_id`            bigint                                                       NOT NULL,
    `created_at`        bigint                                                       NOT NULL,
    `rec_created_at`    bigint DEFAULT NULL,
    `rec_updated_at`    bigint DEFAULT NULL,
    `rec_version`       int    DEFAULT NULL,
    PRIMARY KEY (`fsp_endpoint_id`),
    UNIQUE KEY `uk_fsp_endpoint` (`fsp_endpoint_id`, `type`),
    KEY `pcp_fsp_endpoint_pcp_fsp_FK` (`fsp_id`),
    CONSTRAINT `pcp_fsp_endpoint_pcp_fsp_FK` FOREIGN KEY (`fsp_id`) REFERENCES `pcp_fsp` (`fsp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_participant.pcp_fsp_currency definition

CREATE TABLE `pcp_fsp_currency`
(
    `fsp_currency_id`   bigint      NOT NULL,
    `currency`          varchar(3)  NOT NULL,
    `activation_status` varchar(24) NOT NULL,
    `created_at`        bigint      NOT NULL,
    `fsp_id`            bigint      NOT NULL,
    `rec_created_at`    bigint DEFAULT NULL,
    `rec_updated_at`    bigint DEFAULT NULL,
    `rec_version`       int    DEFAULT NULL,
    PRIMARY KEY (`fsp_currency_id`),
    UNIQUE KEY `uk_fsp_currency` (`fsp_currency_id`, `currency`),
    KEY `pcp_fsp_currency_pcp_fsp_FK` (`fsp_id`),
    CONSTRAINT `pcp_fsp_currency_pcp_fsp_FK` FOREIGN KEY (`fsp_id`) REFERENCES `pcp_fsp` (`fsp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_participant.pcp_oracle definition

CREATE TABLE `pcp_oracle`
(
    `oracle_id`          bigint        NOT NULL,
    `type`               varchar(24)   NOT NULL,
    `name`               varchar(64)   NOT NULL,
    `baseUrl`            varchar(2048) NOT NULL,
    `activation_status`  varchar(24)   NOT NULL,
    `termination_status` varchar(24)   NOT NULL,
    `created_at`         bigint        NOT NULL,
    `rec_created_at`     bigint DEFAULT NULL,
    `rec_updated_at`     bigint DEFAULT NULL,
    `rec_version`        int    DEFAULT NULL,
    PRIMARY KEY (`oracle_id`),
    UNIQUE KEY `uk_oracle` (`type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_participant.pcp_hub definition

CREATE TABLE `pcp_hub`
(
    `hub_id`         bigint      NOT NULL,
    `name`           varchar(64) NOT NULL,
    `created_at`     bigint      NOT NULL,
    `rec_created_at` bigint DEFAULT NULL,
    `rec_updated_at` bigint DEFAULT NULL,
    `rec_version`    int    DEFAULT NULL,
    PRIMARY KEY (`hub_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_participant.pcp_hub_currency definition

CREATE TABLE `pcp_hub_currency`
(
    `hub_currency_id`   bigint      NOT NULL,
    `currency`          varchar(3)  NOT NULL,
    `activation_status` varchar(24) NOT NULL,
    `created_at`        bigint      NOT NULL,
    `hub_id`            bigint      NOT NULL,
    `rec_created_at`    bigint DEFAULT NULL,
    `rec_updated_at`    bigint DEFAULT NULL,
    `rec_version`       int    DEFAULT NULL,
    PRIMARY KEY (`hub_currency_id`),
    UNIQUE KEY `uk_hub_currency` (`hub_currency_id`, `currency`),
    KEY `pcp_hub_currency_pcp_hub_FK` (`hub_id`),
    CONSTRAINT `pcp_hub_currency_pcp_hub_FK` FOREIGN KEY (`hub_id`) REFERENCES `pcp_hub` (`hub_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
