ALTER TABLE `wlt_wallet`
    DROP INDEX `wlt_wallet_01_UK`,
    CHANGE COLUMN `scenario` `tag` varchar(64) NOT NULL,
    ADD UNIQUE KEY `wlt_wallet_01_UK` (`wallet_owner_id`, `currency`, `tag`);
