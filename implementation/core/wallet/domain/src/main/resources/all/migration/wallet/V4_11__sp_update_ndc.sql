DELIMITER
$$

DROP PROCEDURE IF EXISTS `sp_update_ndc` $$
CREATE PROCEDURE sp_update_ndc(IN p_position_id BIGINT, IN p_new_ndc BIGINT)
proc_update_ndc:
BEGIN
    /* -------- Variables -------- */
    DECLARE v_position DECIMAL(34, 4);
    DECLARE v_reserved DECIMAL(34, 4);
    DECLARE v_current_total DECIMAL(34, 4);

    START TRANSACTION;

    SELECT position, reserved
    INTO v_position, v_reserved
    FROM wlt_position
    WHERE position_id = p_position_id FOR
    UPDATE;

    SET v_current_total = v_position + v_reserved;

    IF v_current_total > p_new_ndc THEN
        ROLLBACK;
        SELECT 'FAILED'        AS status,
               p_position_id   AS position_id,
               p_new_ndc       AS ndc,
               v_current_total AS current_total;
        LEAVE proc_update_ndc;
    END IF;

    UPDATE wlt_position
    SET ndc = p_new_ndc
    WHERE position_id = p_position_id; COMMIT;

    SELECT 'SUCCESS'       AS status,
           p_position_id   AS position_id,
           p_new_ndc       AS ndc,
           v_current_total AS current_total;

END$$
DELIMITER ;