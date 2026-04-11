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
    `activation_status` varchar(32) NOT NULL,
    `created_at`        bigint      NOT NULL,
    `hub_id`            bigint      NOT NULL,
    `rec_created_at`    bigint DEFAULT NULL,
    `rec_updated_at`    bigint DEFAULT NULL,
    `rec_version`       int    DEFAULT NULL,
    PRIMARY KEY (`hub_currency_id`),
    UNIQUE KEY `pcp_hub_currency_01_UK` (`hub_id`, `currency`),
    KEY `pcp_hub_pcp_hub_currency_FK_IDX` (`hub_id`),
    CONSTRAINT `pcp_hub_pcp_hub_currency_FK` FOREIGN KEY (`hub_id`) REFERENCES `pcp_hub` (`hub_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `pcp_fsp_group`
(
    `fsp_group_id`   bigint      NOT NULL,
    `name`           varchar(64) NOT NULL,
    `rec_created_at` bigint DEFAULT NULL,
    `rec_updated_at` bigint DEFAULT NULL,
    `rec_version`    int    DEFAULT NULL,
    PRIMARY KEY (`fsp_group_id`),
    UNIQUE KEY `pcp_fsp_group_01_UK` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ml_participant.pcp_fsp definition

CREATE TABLE `pcp_fsp`
(
    `fsp_id`             bigint      NOT NULL,
    `code`               varchar(32) NOT NULL,
    `name`               varchar(64) NOT NULL,
    `activation_status`  varchar(32) NOT NULL,
    `termination_status` varchar(32) NOT NULL,
    `created_at`         bigint      NOT NULL,
    `hub_id`             bigint      NOT NULL,
    `fsp_group_id`       bigint DEFAULT NULL,
    `rec_created_at`     bigint DEFAULT NULL,
    `rec_updated_at`     bigint DEFAULT NULL,
    `rec_version`        int    DEFAULT NULL,
    PRIMARY KEY (`fsp_id`),
    UNIQUE KEY `pcp_fsp_01_UK` (`code`),
    KEY `pcp_hub_pcp_fsp_FK_IDX` (`hub_id`),
    KEY `pcp_fsp_group_pcp_fsp_FK_IDX` (`fsp_group_id`),
    CONSTRAINT `pcp_hub_pcp_fsp_FK` FOREIGN KEY (`hub_id`) REFERENCES `pcp_hub` (`hub_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `pcp_fsp_group_pcp_fsp_FK` FOREIGN KEY (`fsp_group_id`) REFERENCES `pcp_fsp_group` (`fsp_group_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_participant.pcp_fsp_currency definition

CREATE TABLE `pcp_fsp_currency`
(
    `fsp_currency_id`   bigint      NOT NULL,
    `currency`          varchar(3)  NOT NULL,
    `activation_status` varchar(32) NOT NULL,
    `created_at`        bigint      NOT NULL,
    `fsp_id`            bigint      NOT NULL,
    `rec_created_at`    bigint DEFAULT NULL,
    `rec_updated_at`    bigint DEFAULT NULL,
    `rec_version`       int    DEFAULT NULL,
    PRIMARY KEY (`fsp_currency_id`),
    UNIQUE KEY `pcp_fsp_currency_01_UK` (`fsp_id`, `currency`),
    KEY `pcp_fsp_pcp_fsp_currency_FK_IDX` (`fsp_id`),
    CONSTRAINT `pcp_fsp_pcp_fsp_currency_FK` FOREIGN KEY (`fsp_id`) REFERENCES `pcp_fsp` (`fsp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_participant.pcp_fsp_endpoint definition

CREATE TABLE `pcp_fsp_endpoint`
(
    `fsp_endpoint_id`   bigint       NOT NULL,
    `type`              varchar(32)  NOT NULL,
    `base_url`          varchar(255) NOT NULL,
    `activation_status` varchar(32)  NOT NULL,
    `fsp_id`            bigint       NOT NULL,
    `created_at`        bigint       NOT NULL,
    `rec_created_at`    bigint DEFAULT NULL,
    `rec_updated_at`    bigint DEFAULT NULL,
    `rec_version`       int    DEFAULT NULL,
    PRIMARY KEY (`fsp_endpoint_id`),
    UNIQUE KEY `pcp_fsp_endpoint_01_UK` (`fsp_id`, `type`),
    KEY `pcp_fsp_pcp_fsp_endpoint_FK_IDX` (`fsp_id`),
    CONSTRAINT `pcp_fsp_pcp_fsp_endpoint_FK` FOREIGN KEY (`fsp_id`) REFERENCES `pcp_fsp` (`fsp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_participant.pcp_oracle definition

CREATE TABLE `pcp_oracle`
(
    `oracle_id`          bigint       NOT NULL,
    `type`               varchar(32)  NOT NULL,
    `name`               varchar(64)  NOT NULL,
    `base_url`           varchar(255) NOT NULL,
    `activation_status`  varchar(32)  NOT NULL,
    `termination_status` varchar(32)  NOT NULL,
    `created_at`         bigint       NOT NULL,
    `rec_created_at`     bigint DEFAULT NULL,
    `rec_updated_at`     bigint DEFAULT NULL,
    `rec_version`        int    DEFAULT NULL,
    PRIMARY KEY (`oracle_id`),
    UNIQUE KEY `pcp_oracle_01_UK` (`type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_participant.pcp_ssp definition

CREATE TABLE `pcp_ssp`
(
    `ssp_id`             bigint       NOT NULL,
    `code`               varchar(32)  NOT NULL,
    `name`               varchar(64)  NOT NULL,
    `base_url`           varchar(255) NOT NULL,
    `activation_status`  varchar(32)  NOT NULL,
    `termination_status` varchar(32)  NOT NULL,
    `created_at`         bigint       NOT NULL,
    `hub_id`             bigint       NOT NULL,
    `rec_created_at`     bigint DEFAULT NULL,
    `rec_updated_at`     bigint DEFAULT NULL,
    `rec_version`        int    DEFAULT NULL,
    PRIMARY KEY (`ssp_id`),
    UNIQUE KEY `pcp_ssp_01_UK` (`code`),
    KEY `pcp_hub_pcp_ssp_FK_IDX` (`hub_id`),
    CONSTRAINT `pcp_hub_pcp_ssp_FK` FOREIGN KEY (`hub_id`) REFERENCES `pcp_hub` (`hub_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- ml_participant.pcp_ssp_currency definition

CREATE TABLE `pcp_ssp_currency`
(
    `ssp_currency_id`   bigint      NOT NULL,
    `currency`          varchar(3)  NOT NULL,
    `activation_status` varchar(32) NOT NULL,
    `created_at`        bigint      NOT NULL,
    `ssp_id`            bigint      NOT NULL,
    `rec_created_at`    bigint DEFAULT NULL,
    `rec_updated_at`    bigint DEFAULT NULL,
    `rec_version`       int    DEFAULT NULL,
    PRIMARY KEY (`ssp_currency_id`),
    UNIQUE KEY `pcp_ssp_currency_01_UK` (`ssp_id`, `currency`),
    KEY `pcp_ssp_pcp_ssp_currency_FK_IDX` (`ssp_id`),
    CONSTRAINT `pcp_ssp_pcp_ssp_currency_FK` FOREIGN KEY (`ssp_id`) REFERENCES `pcp_ssp` (`ssp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
