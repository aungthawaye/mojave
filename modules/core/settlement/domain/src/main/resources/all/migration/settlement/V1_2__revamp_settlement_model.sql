ALTER TABLE `stm_settlement_definition`
    ADD COLUMN `end_at` BIGINT NULL AFTER `start_at`;

ALTER TABLE `stm_settlement_definition`
    DROP INDEX `stm_settlement_definition_01_UK`;

ALTER TABLE `stm_settlement_definition`
    DROP INDEX `stm_settlement_definition_02_UK`;

ALTER TABLE `stm_settlement_definition`
    ADD UNIQUE KEY `stm_settlement_definition_01_UK` (`name`);

ALTER TABLE `stm_settlement_record`
    ADD COLUMN `party_fsp_id` BIGINT NULL AFTER `payee_fsp_id`,
    ADD COLUMN `liquidity_direction` VARCHAR(20) NULL AFTER `party_fsp_id`,
    ADD COLUMN `amount_type` VARCHAR(20) NULL AFTER `liquidity_direction`,
    ADD COLUMN `line_no` INT NULL AFTER `amount_type`;

UPDATE `stm_settlement_record`
SET `party_fsp_id` = `payer_fsp_id`
WHERE `party_fsp_id` IS NULL;

UPDATE `stm_settlement_record`
SET `liquidity_direction` = 'DECREASE'
WHERE `liquidity_direction` IS NULL;

UPDATE `stm_settlement_record`
SET `amount_type` = 'TRANSFER_AMOUNT'
WHERE `amount_type` IS NULL;

UPDATE `stm_settlement_record`
SET `line_no` = 1
WHERE `line_no` IS NULL;

ALTER TABLE `stm_settlement_record`
    MODIFY COLUMN `party_fsp_id` BIGINT NOT NULL,
    MODIFY COLUMN `liquidity_direction` VARCHAR(20) NOT NULL,
    MODIFY COLUMN `amount_type` VARCHAR(20) NOT NULL,
    MODIFY COLUMN `line_no` INT NOT NULL;

ALTER TABLE `stm_settlement_record`
    DROP INDEX `stm_settlement_record_01_UK`;

ALTER TABLE `stm_settlement_record`
    DROP INDEX `stm_settlement_record_02_UK`;

ALTER TABLE `stm_settlement_record`
    DROP INDEX `stm_settlement_record_03_UK`;

ALTER TABLE `stm_settlement_record`
    DROP INDEX `stm_settlement_record_01_IDX`;

ALTER TABLE `stm_settlement_record`
    DROP INDEX `stm_settlement_record_02_IDX`;

ALTER TABLE `stm_settlement_record`
    DROP INDEX `stm_settlement_record_03_IDX`;

ALTER TABLE `stm_settlement_record`
    DROP INDEX `stm_settlement_record_04_IDX`;

ALTER TABLE `stm_settlement_record`
    DROP INDEX `stm_settlement_record_05_IDX`;

ALTER TABLE `stm_settlement_record`
    ADD UNIQUE KEY `stm_settlement_record_01_UK` (`transaction_id`, `line_no`),
    ADD KEY `stm_settlement_record_01_IDX` (`transaction_id`),
    ADD KEY `stm_settlement_record_02_IDX` (`transfer_id`),
    ADD KEY `stm_settlement_record_03_IDX` (`payer_fsp_id`, `payee_fsp_id`, `currency`),
    ADD KEY `stm_settlement_record_04_IDX` (`transaction_at`),
    ADD KEY `stm_settlement_record_05_IDX` (`initiated_at`),
    ADD KEY `stm_settlement_record_06_IDX` (`prepared_at`),
    ADD KEY `stm_settlement_record_07_IDX` (`completed_at`);
