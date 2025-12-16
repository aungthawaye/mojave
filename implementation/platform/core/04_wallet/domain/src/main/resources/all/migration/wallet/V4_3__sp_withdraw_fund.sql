DELIMITER
$$

DROP PROCEDURE IF EXISTS sp_withdraw_fund $$
CREATE PROCEDURE sp_withdraw_fund(
    IN p_transaction_id BIGINT,
    IN p_transaction_at BIGINT,
    IN p_balance_update_id BIGINT,
    IN p_balance_id BIGINT,
    IN p_amount DECIMAL(34, 4),
    IN p_description VARCHAR(256)
)
proc_withdraw:
BEGIN
    /* -------- Variables -------- */
    DECLARE
        v_old_balance DECIMAL(34, 4);
    DECLARE
        v_new_balance DECIMAL(34, 4);
    DECLARE
        v_currency VARCHAR(3);
    DECLARE
        v_now BIGINT;

    DECLARE
        v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE
        EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;

            SELECT 'ERROR'             AS status,
                   p_balance_update_id AS balance_update_id,
                   p_balance_id        AS balance_id,
                   'WITHDRAW'          AS action,
                   p_transaction_id    AS transaction_id,
                   NULL                AS currency,
                   p_amount            AS amount,
                   0                   AS old_balance,
                   0                   AS new_balance,
                   p_transaction_at    AS transaction_at;
        END;

    DECLARE
        CONTINUE HANDLER FOR NOT FOUND SET v_not_found = TRUE;

    /* Use epoch seconds for timestamps */
    SET
        v_now = UNIX_TIMESTAMP();

    START TRANSACTION;

/* 1) Lock wallet row and capture snapshot */
    SELECT w.balance,
           w.currency
    INTO v_old_balance, v_currency
    FROM wlt_balance w
    WHERE w.balance_id = p_balance_id
        FOR
    UPDATE;

    IF
        v_not_found THEN
        ROLLBACK;

        SELECT 'ERROR'             AS status,
               p_balance_update_id AS balance_update_id,
               p_balance_id        AS balance_id,
               'WITHDRAW'          AS action,
               p_transaction_id    AS transaction_id,
               NULL                AS currency,
               p_amount            AS amount,
               0                   AS old_balance,
               0                   AS new_balance,
               p_transaction_at    AS transaction_at;

        LEAVE
            proc_withdraw;
    END IF;

    SET
        v_new_balance = v_old_balance - p_amount;

    /* 2) Check sufficient balance */
    IF
        v_new_balance < 0 THEN
        /* Not enough balances */
        ROLLBACK;
        SELECT 'INSUFFICIENT_BALANCE' AS status,
               p_balance_update_id    AS balance_update_id,
               p_balance_id           AS balance_id,
               'WITHDRAW'             AS action,
               p_transaction_id       AS transaction_id,
               v_currency             AS currency,
               p_amount               AS amount,
               v_old_balance          AS old_balance,
               v_new_balance          AS new_balance,
               v_now                  AS transaction_at;
        LEAVE
            proc_withdraw;
    END IF;

    /* 3) Apply deduction to balance */
    UPDATE wlt_balance
    SET balance = v_new_balance
    WHERE balance_id = p_balance_id;

/* 4) Insert balance update row */
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
                                    rec_created_at,
                                    rec_updated_at,
                                    rec_version)
    VALUES (p_balance_update_id,
            p_balance_id,
            'WITHDRAW',
            p_transaction_id,
            v_currency,
            p_amount,
            v_old_balance,
            v_new_balance,
            p_description,
            v_now,
            v_now,
            v_now,
            v_now,
            0);
    COMMIT;

/* 5) Return result */
    SELECT 'SUCCESS' AS status,
           bu.balance_update_id,
           bu.balance_id,
           bu.action,
           bu.transaction_id,
           bu.currency,
           bu.amount,
           bu.old_balance,
           bu.new_balance,
           bu.transaction_at
    FROM wlt_balance_update bu
    WHERE bu.balance_update_id = p_balance_update_id;
END $$

DELIMITER ;