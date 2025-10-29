DELIMITER $$
/*
 * Decrease position stored procedure
 */
DROP PROCEDURE IF EXISTS sp_decrease_position $$
CREATE PROCEDURE sp_decrease_position(
    IN p_transaction_id BIGINT,
    IN p_transaction_at BIGINT,
    IN p_position_update_id BIGINT,
    IN p_position_id BIGINT,
    IN p_amount DECIMAL(34, 4),
    IN p_description VARCHAR(256)
)
BEGIN
    /* -------- Variables -------- */
    DECLARE v_old_position DECIMAL(34, 4);
    DECLARE v_new_position DECIMAL(34, 4);
    DECLARE v_old_reserved DECIMAL(34, 4);
    DECLARE v_net_debit_cap DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_now BIGINT;

    SET v_now = UNIX_TIMESTAMP();

    START TRANSACTION;

    SELECT position,
           reserved,
           net_debit_cap,
           currency
    INTO v_old_position, v_old_reserved, v_net_debit_cap, v_currency
    FROM wlt_position
    WHERE position_id = p_position_id FOR
    UPDATE;

    SET v_new_position = v_old_position - p_amount;

    UPDATE wlt_position SET position = v_new_position WHERE position_id = p_position_id;

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
                                     net_debit_cap,
                                     description,
                                     transaction_at,
                                     created_at,
                                     reservation_id,
                                     rec_created_at,
                                     rec_updated_at,
                                     rec_version)
    VALUES (p_position_update_id, p_position_id,
            'DECREASE', p_transaction_id, v_currency,
            p_amount, v_old_position, v_new_position,
            v_old_reserved, v_old_reserved, v_net_debit_cap, p_description,
            p_transaction_at, v_now, NULL,
            v_now, v_now, 0);
    COMMIT;

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
           pu.net_debit_cap,
           pu.transaction_at
    FROM wlt_position_update pu
    WHERE pu.position_update_id = p_position_update_id;

END $$

DELIMITER ;
