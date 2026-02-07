CREATE TABLE `pcp_fsp_group`
(
    `fsp_group_id`    bigint      NOT NULL,
    `name`            varchar(64) NOT NULL,
    `rec_created_at`  bigint DEFAULT NULL,
    `rec_updated_at`  bigint DEFAULT NULL,
    `rec_version`     int    DEFAULT NULL,
    PRIMARY KEY (`fsp_group_id`),
    UNIQUE KEY `pcp_fsp_group_01_UK` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `pcp_fsp_group_item`
(
    `fsp_group_item_id` bigint NOT NULL,
    `fsp_group_id`      bigint NOT NULL,
    `fsp_id`            bigint NOT NULL,
    `rec_created_at`    bigint DEFAULT NULL,
    `rec_updated_at`    bigint DEFAULT NULL,
    `rec_version`       int    DEFAULT NULL,
    PRIMARY KEY (`fsp_group_item_id`),
    UNIQUE KEY `pcp_fsp_group_item_01_UK` (`fsp_group_id`, `fsp_id`),
    KEY `pcp_fsp_group_pcp_fsp_group_item_FK_IDX` (`fsp_group_id`),
    KEY `pcp_fsp_pcp_fsp_group_item_FK_IDX` (`fsp_id`),
    CONSTRAINT `pcp_fsp_group_pcp_fsp_group_item_FK` FOREIGN KEY (`fsp_group_id`) REFERENCES `pcp_fsp_group` (`fsp_group_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `pcp_fsp_pcp_fsp_group_item_FK` FOREIGN KEY (`fsp_id`) REFERENCES `pcp_fsp` (`fsp_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
