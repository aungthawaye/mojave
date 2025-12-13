DELIMITER
$$
/*
 * Increase position stored procedure
 */
DROP PROCEDURE IF EXISTS sp_increase_position $$
CREATE PROCEDURE sp_increase_position(
    IN p_transaction_id BIGINT,
    IN p_transaction_at BIGINT,
    IN p_position_update_id BIGINT,
    IN p_position_id BIGINT,
    IN p_amount DECIMAL (34, 4),
    IN p_description VARCHAR (256)
) proc_increase:
BEGIN
    /* -------- Variables -------- */
    DECLARE
v_old_position DECIMAL(34, 4);
    DECLARE
v_new_position DECIMAL(34, 4);
    DECLARE
v_old_reserved DECIMAL(34, 4);
    DECLARE
v_ndc DECIMAL(34, 4);
    DECLARE
v_limit DECIMAL(34, 4);
    DECLARE
v_currency VARCHAR(3);
    DECLARE
v_now BIGINT;

    SET
v_now = UNIX_TIMESTAMP();

START TRANSACTION;

SELECT position,
       reserved,
       ndc,
       currency
INTO v_old_position, v_old_reserved, v_ndc, v_currency
FROM wlt_position
WHERE position_id = p_position_id FOR
    UPDATE;


SET
v_new_position = v_old_position + p_amount;
    SET
v_limit = v_old_position + v_new_position;

    IF
v_limit > v_ndc THEN
        ROLLBACK;
SELECT 'LIMIT_EXCEEDED'     AS status,
       p_position_update_id AS position_update_id,
       p_position_id        AS position_id,
       'INCREASE' AS action,
               p_transaction_id     AS transaction_id,
               v_currency           AS currency,
               p_amount             AS amount,
               v_old_position       AS old_position,
               v_new_position       AS new_position,
               v_old_reserved       AS old_reserved,
               v_old_reserved       AS new_reserved,
               v_ndc                AS ndc,
               p_transaction_at     AS transaction_at;
LEAVE
proc_increase;
END IF;

UPDATE wlt_position
SET position = v_new_position
WHERE position_id = p_position_id;

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
VALUES (p_position_update_id, p_position_id,
        'INCREASE', p_transaction_id, v_currency,
        p_amount, v_old_position, v_new_position,
        v_old_reserved, v_old_reserved, v_ndc,
        p_description, p_transaction_at, v_now, NULL,
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
       pu.ndc,
       pu.transaction_at
FROM wlt_position_update pu
WHERE pu.position_update_id = p_position_update_id;

END $$

DELIMITER ;
