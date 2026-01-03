DELIMITER
$$

DROP PROCEDURE IF EXISTS sp_reverse_fund $$
CREATE PROCEDURE sp_reverse_fund(
                                IN p_reversing_balance_update_id BIGINT,
                                IN p_balance_update_id           BIGINT)
proc_reverse:
BEGIN
    /* -------- Variables -------- */
    DECLARE v_balance_id BIGINT;
    DECLARE v_action VARCHAR(32);
    DECLARE v_amount DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_transaction_id BIGINT;
    DECLARE v_description VARCHAR(256);

    DECLARE v_now BIGINT;
    DECLARE v_old_balance DECIMAL(34, 4);
    DECLARE v_new_balance DECIMAL(34, 4);

    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN
        ROLLBACK;

        SELECT 'ERROR'                       AS status,
               p_balance_update_id           AS balance_update_id,
               NULL                          AS balance_id,
               'REVERSE_WITHDRAW'            AS action,
               NULL                          AS transaction_id,
               NULL                          AS currency,
               0                             AS amount,
               0                             AS old_balance,
               0                             AS new_balance,
               NULL                          AS transaction_at,
               p_reversing_balance_update_id AS withdraw_id;
    END;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_not_found = TRUE;

    /* Use epoch seconds for timestamps */
    SET v_now = UNIX_TIMESTAMP();

    /* 1) Fetch the original balance update row */
    SELECT bu.balance_id,
           bu.action,
           bu.amount,
           bu.currency,
           bu.transaction_id,
           bu.description
    INTO v_balance_id, v_action, v_amount, v_currency, v_transaction_id, v_description
    FROM wlt_balance_update bu
    WHERE bu.balance_update_id = p_reversing_balance_update_id
      AND bu.action = 'WITHDRAW';

    IF v_not_found THEN

        SELECT 'REVERSAL_FAILED'             AS status,
               p_balance_update_id           AS balance_update_id,
               NULL                          AS balance_id,
               'REVERSE_WITHDRAW'            AS action,
               NULL                          AS transaction_id,
               NULL                          AS currency,
               0                             AS amount,
               0                             AS old_balance,
               0                             AS new_balance,
               NULL                          AS transaction_at,
               p_reversing_balance_update_id AS withdraw_id;

        LEAVE proc_reverse;
    END IF;

    /* 3) Branch by action */
    IF v_action = 'WITHDRAW' THEN

        START TRANSACTION;

        SET v_not_found = FALSE;

        /* Reverse the withdrawal by crediting back the amount */
        SELECT w.balance
        INTO v_old_balance
        FROM wlt_balance w
        WHERE w.balance_id = v_balance_id FOR
        UPDATE;

        SET v_new_balance = v_old_balance + v_amount;

        IF v_not_found THEN

            ROLLBACK;

            SELECT 'REVERSAL_FAILED'             AS status,
                   p_balance_update_id           AS balance_update_id,
                   NULL                          AS balance_id,
                   'REVERSE_WITHDRAW'            AS action,
                   NULL                          AS transaction_id,
                   NULL                          AS currency,
                   0                             AS amount,
                   0                             AS old_balance,
                   0                             AS new_balance,
                   NULL                          AS transaction_at,
                   p_reversing_balance_update_id AS withdraw_id;

            LEAVE proc_reverse;

        END IF;

        UPDATE wlt_balance
        SET balance = v_new_balance
        WHERE balance_id = v_balance_id;

        /* Insert a new balance update row flagged as REVERSED */
        INSERT INTO wlt_balance_update (balance_update_id,
                                        balance_id,
                                        action,
                                        transaction_id,
                                        currency,
                                        amount,
                                        old_balance,
                                        new_balance,
                                        description,
                                        transaction_at,
                                        created_at,
                                        withdraw_id,
                                        rec_created_at,
                                        rec_updated_at,
                                        rec_version)
        VALUES (p_balance_update_id,
                v_balance_id,
                'REVERSE_WITHDRAW',
                v_transaction_id,
                v_currency,
                v_amount,
                v_old_balance,
                v_new_balance,
                v_description,
                v_now,
                v_now,
                p_reversing_balance_update_id,
                v_now,
                v_now,
                0); COMMIT;

        /* 3.c) Return the reversal row */
        SELECT 'SUCCESS' AS status,
               bu.balance_update_id,
               bu.balance_id,
               bu.action,
               bu.transaction_id,
               bu.currency,
               bu.amount,
               bu.old_balance,
               bu.new_balance,
               bu.transaction_at,
               bu.withdraw_id
        FROM wlt_balance_update bu
        WHERE bu.balance_update_id = p_balance_update_id;

    ELSE

        SELECT 'REVERSAL_FAILED'             AS status,
               p_balance_update_id           AS balance_update_id,
               NULL                          AS balance_id,
               'REVERSE_WITHDRAW'            AS action,
               NULL                          AS transaction_id,
               NULL                          AS currency,
               0                             AS amount,
               0                             AS old_balance,
               0                             AS new_balance,
               NULL                          AS transaction_at,
               p_reversing_balance_update_id AS withdraw_id;

    END IF;
END $$

DELIMITER ;
