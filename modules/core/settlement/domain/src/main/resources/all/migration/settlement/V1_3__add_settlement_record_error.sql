ALTER TABLE `stm_settlement_record`
    ADD COLUMN `error` VARCHAR(2048) NULL AFTER `completed_at`;
