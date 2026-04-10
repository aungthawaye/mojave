DELIMITER
$$
DROP PROCEDURE IF EXISTS `sp_fulfil_positions` $$
CREATE PROCEDURE sp_fulfil_positions(
                                    IN p_reservation_id        BIGINT,
                                    IN p_reservation_commit_id BIGINT,
                                    IN p_position_decrement_id BIGINT,
                                    IN p_payee_wallet_id     BIGINT,
                                    IN p_description           VARCHAR(256))
proc_fulfil:
BEGIN
    DECLARE v_payer_wallet_id BIGINT;
    DECLARE v_action VARCHAR(32);
    DECLARE v_amount DECIMAL(34, 4);
    DECLARE v_transaction_id BIGINT;
    DECLARE v_transaction_at BIGINT;
    DECLARE v_description VARCHAR(255);
    DECLARE v_now BIGINT;
    DECLARE v_payer_old_position DECIMAL(34, 4);
    DECLARE v_payer_new_position DECIMAL(34, 4);
    DECLARE v_payer_old_reserved DECIMAL(34, 4);
    DECLARE v_payer_new_reserved DECIMAL(34, 4);
    DECLARE v_payer_ndc DECIMAL(34, 4);
    DECLARE v_payer_currency VARCHAR(3);
    DECLARE v_payee_old_position DECIMAL(34, 4);
    DECLARE v_payee_new_position DECIMAL(34, 4);
    DECLARE v_payee_old_reserved DECIMAL(34, 4);
    DECLARE v_payee_new_reserved DECIMAL(34, 4);
    DECLARE v_payee_ndc DECIMAL(34, 4);
    DECLARE v_payee_currency VARCHAR(3);
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;

        SELECT 'ERROR'         AS status,
               'FULFIL_FAILED' AS err_error_code,
               NULL            AS payer_commit_id,
               NULL            AS payee_commit_id;
    END;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_not_found = TRUE;

    SET v_now = UNIX_TIMESTAMP();

    SELECT position_id,
           amount,
           transaction_id,
           transaction_at,
           action,
           description
    INTO v_payer_wallet_id, v_amount, v_transaction_id, v_transaction_at, v_action, v_description
    FROM mwe_position_update
    WHERE position_update_id = p_reservation_id;

    IF v_not_found OR v_action != 'RESERVE' THEN
        SELECT 'ERROR'         AS status,
               'COMMIT_FAILED' AS err_error_code,
               NULL            AS payer_commit_id,
               NULL            AS payee_commit_id;

        LEAVE proc_fulfil;
    END IF;

    START TRANSACTION;

    SET v_not_found = FALSE;

    SELECT position,
           reserved,
           ndc,
           mw.currency
    INTO v_payer_old_position, v_payer_old_reserved, v_payer_ndc, v_payer_currency
    FROM mwe_wallet mw
    WHERE mw.wallet_id = v_payer_wallet_id FOR
    UPDATE;

    IF v_not_found THEN
        ROLLBACK;

        SELECT 'ERROR'                    AS status,
               'PAYER_POSITION_NOT_FOUND' AS err_error_code,
               NULL                       AS payer_commit_id,
               NULL                       AS payee_commit_id;

        LEAVE proc_fulfil;
    END IF;

    SET v_payer_new_reserved = v_payer_old_reserved - v_amount;
    SET v_payer_new_position = v_payer_old_position + v_amount;

    UPDATE mwe_wallet
    SET position = v_payer_new_position, reserved = v_payer_new_reserved
    WHERE wallet_id = v_payer_wallet_id;

    SET v_not_found = FALSE;

    SELECT position,
           reserved,
           ndc,
           mw.currency
    INTO v_payee_old_position, v_payee_old_reserved, v_payee_ndc, v_payee_currency
    FROM mwe_wallet mw
    WHERE mw.wallet_id = p_payee_wallet_id FOR
    UPDATE;

    IF v_not_found THEN
        ROLLBACK;

        SELECT 'ERROR'                    AS status,
               'PAYEE_POSITION_NOT_FOUND' AS err_error_code,
               NULL                       AS payer_commit_id,
               NULL                       AS payee_commit_id;

        LEAVE proc_fulfil;
    END IF;

    IF v_payee_currency != v_payer_currency THEN
        ROLLBACK;

        SELECT 'ERROR'                 AS status,
               'CURRENCIES_MISMATCHED' AS err_error_code,
               NULL                    AS payer_commit_id,
               NULL                    AS payee_commit_id;

        LEAVE proc_fulfil;
    END IF;

    SET v_payee_new_position = v_payee_old_position - v_amount;
    SET v_payee_new_reserved = v_payee_old_reserved;

    UPDATE mwe_wallet
    SET position = v_payee_new_position
    WHERE wallet_id = p_payee_wallet_id;

    INSERT INTO mwe_position_update (position_update_id,
                                     position_id,
                                     action,
                                     transaction_id,
                                     currency,
                                     amount,
                                     old_position,
                                     new_position,
                                     old_reserved,
                                     new_reserved,
                                     ndc,
                                     description,
                                     transaction_at,
                                     created_at,
                                     reservation_id,
                                     rec_created_at,
                                     rec_updated_at,
                                     rec_version)
    VALUES (p_reservation_commit_id,
            v_payer_wallet_id,
            'COMMIT',
            v_transaction_id,
            v_payer_currency,
            v_amount,
            v_payer_old_position,
            v_payer_new_position,
            v_payer_old_reserved,
            v_payer_new_reserved,
            v_payer_ndc,
            p_description,
            v_transaction_at,
            v_now,
            p_reservation_id,
            v_now,
            v_now,
            0);

    INSERT INTO mwe_position_update (position_update_id,
                                     position_id,
                                     action,
                                     transaction_id,
                                     currency,
                                     amount,
                                     old_position,
                                     new_position,
                                     old_reserved,
                                     new_reserved,
                                     ndc,
                                     description,
                                     transaction_at,
                                     created_at,
                                     reservation_id,
                                     rec_created_at,
                                     rec_updated_at,
                                     rec_version)
    VALUES (p_position_decrement_id,
            p_payee_wallet_id,
            'DECREASE',
            v_transaction_id,
            v_payee_currency,
            v_amount,
            v_payee_old_position,
            v_payee_new_position,
            v_payee_old_reserved,
            v_payee_new_reserved,
            v_payee_ndc,
            p_description,
            v_transaction_at,
            v_now,
            NULL,
            v_now,
            v_now,
            0);

    COMMIT;

    SELECT 'SUCCESS'               AS status,
           NULL                    AS err_error_code,
           p_reservation_commit_id AS payer_commit_id,
           p_position_decrement_id AS payee_commit_id;
END $$


DELIMITER ;
