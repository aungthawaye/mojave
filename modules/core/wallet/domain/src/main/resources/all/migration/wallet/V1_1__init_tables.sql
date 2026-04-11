-- ml_wallet.wlt_wallet definition

CREATE TABLE `wlt_wallet`
(
    `wallet_id`       bigint      NOT NULL,
    `wallet_owner_id` bigint      NOT NULL,
    `currency`        varchar(3)  NOT NULL,
    `purpose`        varchar(64) NOT NULL,
    `name`            varchar(64) NOT NULL,
    `created_at`      bigint DEFAULT NULL,
    `rec_created_at`  bigint DEFAULT NULL,
    `rec_updated_at`  bigint DEFAULT NULL,
    `rec_version`     int    DEFAULT NULL,
    PRIMARY KEY (`wallet_id`),
    UNIQUE KEY `wlt_wallet_01_UK` (`wallet_owner_id`, `currency`, `purpose`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
