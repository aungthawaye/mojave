DELIMITER
$$

DROP PROCEDURE IF EXISTS `sp_rollback_position` $$
CREATE PROCEDURE sp_rollback_position(
                                     IN p_reservation_id     BIGINT,
                                     IN p_position_update_id BIGINT)
proc_rollback:
BEGIN
    /* -------- Variables -------- */
    DECLARE v_position_id BIGINT;
    DECLARE v_action VARCHAR(32);
    DECLARE v_amount DECIMAL(34, 4);
    DECLARE v_transaction_id BIGINT;
    DECLARE v_transaction_at BIGINT;
    DECLARE v_description VARCHAR(255);
    DECLARE v_now BIGINT;
    DECLARE v_old_position DECIMAL(34, 4);
    DECLARE v_old_reserved DECIMAL(34, 4);
    DECLARE v_new_reserved DECIMAL(34, 4);
    DECLARE v_ndc DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);

    SET v_now = UNIX_TIMESTAMP();

    SELECT position_id,
           amount,
           transaction_id,
           transaction_at,
           action,
           description
    INTO v_position_id, v_amount, v_transaction_id, v_transaction_at, v_action, v_description
    FROM wlt_position_update
    WHERE position_update_id = p_reservation_id;

    IF v_action != 'RESERVE' THEN
        SELECT 'ROLLBACK_FAILED' AS status,
               p_reservation_id  AS position_update_id,
               v_position_id     AS position_id,
               'ROLLBACK'        AS action,
               v_transaction_id  AS transaction_id,
               v_currency        AS currency,
               v_amount          AS amount,
               v_old_position    AS old_position,
               v_old_position    AS new_position,
               v_old_reserved    AS old_reserved,
               v_new_reserved    AS new_reserved,
               v_ndc             AS ndc,
               v_transaction_at  AS transaction_at;
        LEAVE proc_rollback;
    END IF;

    START TRANSACTION;

    SELECT position,
           reserved,
           ndc,
           currency
    INTO v_old_position, v_old_reserved, v_ndc, v_currency
    FROM wlt_position
    WHERE position_id = v_position_id FOR
    UPDATE;

    SET v_new_reserved = v_old_reserved - v_amount;

    UPDATE wlt_position
    SET reserved = v_new_reserved
    WHERE position_id = v_position_id;

    INSERT INTO wlt_position_update (position_update_id,
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
    VALUES (p_position_update_id,
            v_position_id,
            'ROLLBACK',
            v_transaction_id,
            v_currency,
            v_amount,
            v_old_position,
            v_old_position,
            v_old_reserved,
            v_new_reserved,
            v_ndc,
            v_description,
            v_transaction_at,
            v_now,
            p_reservation_id,
            v_now,
            v_now,
            0); COMMIT;

    SELECT 'SUCCESS' AS status,
           pu.position_update_id,
           pu.position_id,
           pu.action,
           pu.transaction_id,
           pu.currency,
           pu.amount,
           pu.old_position,
           pu.new_position,
           pu.old_reserved,
           pu.new_reserved,
           pu.ndc,
           pu.transaction_at
    FROM wlt_position_update pu
    WHERE pu.position_update_id = p_position_update_id;

END$$
DELIMITER ;