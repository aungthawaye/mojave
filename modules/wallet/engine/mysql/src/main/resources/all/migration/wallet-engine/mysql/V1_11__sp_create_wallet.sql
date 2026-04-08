DELIMITER
$$

DROP PROCEDURE IF EXISTS `sp_create_wallet` $$
CREATE PROCEDURE sp_create_wallet(IN p_wallet_id BIGINT,
                                  IN p_currency VARCHAR(3),
                                  IN p_scale INT,
                                  IN p_scenario VARCHAR(64))
proc_create_wallet:
BEGIN
    DECLARE v_now BIGINT;

    SET v_now = UNIX_TIMESTAMP();

    START TRANSACTION;

    IF EXISTS(SELECT 1 FROM mwe_wallet WHERE wallet_id = p_wallet_id) THEN
        ROLLBACK;

        SELECT 'WALLET_ALREADY_EXISTS' AS status,
               p_wallet_id             AS wallet_id;

        LEAVE proc_create_wallet;
    END IF;

    INSERT INTO mwe_wallet(wallet_id,
                           wallet_owner_id,
                           name,
                           balance,
                           position,
                           reserved,
                           ndc,
                           created_at,
                           rec_created_at,
                           rec_updated_at,
                           rec_version)
    SELECT wallet_id,
           wallet_owner_id,
           name,
           0,
           0,
           0,
           0,
           COALESCE(created_at, v_now),
           v_now,
           v_now,
           0
    FROM wlt_wallet
    WHERE wallet_id = p_wallet_id;

    IF ROW_COUNT() = 0 THEN
        ROLLBACK;

        SELECT 'WALLET_NOT_FOUND' AS status,
               p_wallet_id        AS wallet_id;

        LEAVE proc_create_wallet;
    END IF;

    COMMIT;

    SELECT 'SUCCESS'   AS status,
           p_wallet_id AS wallet_id;
END $$

DELIMITER ;
