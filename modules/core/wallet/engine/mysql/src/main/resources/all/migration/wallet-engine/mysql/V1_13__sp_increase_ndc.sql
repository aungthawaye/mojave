DELIMITER
$$

DROP PROCEDURE IF EXISTS `sp_increase_ndc` $$
CREATE PROCEDURE sp_increase_ndc(IN p_ndc_update_id BIGINT,
                                 IN p_transaction_id BIGINT,
                                 IN p_transaction_at BIGINT,
                                 IN p_wallet_id BIGINT,
                                 IN p_amount DECIMAL(34, 4),
                                 IN p_description VARCHAR(256))
proc_increase_ndc:
BEGIN
    DECLARE v_old_ndc DECIMAL(34, 4);
    DECLARE v_new_ndc DECIMAL(34, 4);
    DECLARE v_balance DECIMAL(34, 4);
    DECLARE v_position DECIMAL(34, 4);
    DECLARE v_reserved DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_now BIGINT;
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;

        SELECT 'ERROR'               AS status,
               p_ndc_update_id       AS ndc_update_id,
               p_wallet_id           AS wallet_id,
               p_transaction_id      AS transaction_id,
               NULL                  AS currency,
               p_amount              AS amount,
               0                     AS old_ndc,
               0                     AS new_ndc,
               p_transaction_at      AS transaction_at;
    END;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_not_found = TRUE;

    SET v_now = UNIX_TIMESTAMP();

    START TRANSACTION;

    SELECT mw.ndc,
           mw.balance,
           mw.position,
           mw.reserved,
           mw.currency
    INTO v_old_ndc, v_balance, v_position, v_reserved, v_currency
    FROM mwe_wallet mw
    WHERE mw.wallet_id = p_wallet_id FOR
    UPDATE;

    IF v_not_found THEN
        ROLLBACK;

        SELECT 'NOT_FOUND'           AS status,
               p_ndc_update_id       AS ndc_update_id,
               p_wallet_id           AS wallet_id,
               p_transaction_id      AS transaction_id,
               NULL                  AS currency,
               p_amount              AS amount,
               0                     AS old_ndc,
               0                     AS new_ndc,
               p_transaction_at      AS transaction_at;

        LEAVE proc_increase_ndc;
    END IF;

    SET v_new_ndc = v_old_ndc + p_amount;

    IF v_balance < v_new_ndc THEN
        ROLLBACK;

        SELECT 'BALANCE_LOWER_THAN_NEW_NDC' AS status,
               p_ndc_update_id              AS ndc_update_id,
               p_wallet_id                  AS wallet_id,
               p_transaction_id             AS transaction_id,
               v_currency                   AS currency,
               p_amount                     AS amount,
               v_old_ndc                    AS old_ndc,
               v_new_ndc                    AS new_ndc,
               v_balance                    AS balance,
               p_transaction_at             AS transaction_at;

        LEAVE proc_increase_ndc;
    END IF;

    UPDATE mwe_wallet
    SET ndc = v_new_ndc
    WHERE wallet_id = p_wallet_id;

    INSERT INTO mwe_ndc_update(ndc_update_id,
                               wallet_id,
                               transaction_id,
                               old_ndc,
                               new_ndc,
                               transaction_at,
                               rec_created_at,
                               rec_updated_at,
                               rec_version)
    VALUES (p_ndc_update_id,
            p_wallet_id,
            p_transaction_id,
            v_old_ndc,
            v_new_ndc,
            p_transaction_at,
            v_now,
            v_now,
            0);

    COMMIT;

    SELECT 'SUCCESS'          AS status,
           p_ndc_update_id    AS ndc_update_id,
           p_wallet_id        AS wallet_id,
           p_transaction_id   AS transaction_id,
           v_currency         AS currency,
           p_amount           AS amount,
           v_old_ndc          AS old_ndc,
           v_new_ndc          AS new_ndc,
           p_transaction_at   AS transaction_at;
END $$

DELIMITER ;
