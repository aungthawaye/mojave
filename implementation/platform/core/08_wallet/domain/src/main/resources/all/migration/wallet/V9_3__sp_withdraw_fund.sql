DELIMITER $$

DROP PROCEDURE IF EXISTS sp_withdraw_fund $$
CREATE PROCEDURE sp_withdraw_fund(
    IN p_transaction_id BIGINT,
    IN p_balance_update_id BIGINT,
    IN p_wallet_id BIGINT,
    IN p_amount DECIMAL(34, 4),
    IN p_description VARCHAR(256)
)
proc_withdraw:
BEGIN
    /* -------- Variables -------- */
    DECLARE v_old_balance DECIMAL(34, 4);
    DECLARE v_new_balance DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_now BIGINT;

    /* Use epoch millis for timestamps */
    SET v_now = CAST(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000 AS UNSIGNED);

    START TRANSACTION;

    /* 1) Lock wallet row and capture snapshot */
    SELECT w.balance,
           w.currency
    INTO v_old_balance, v_currency
    FROM wlt_wallet w
    WHERE w.wallet_id = p_wallet_id FOR UPDATE;

    SET v_new_balance = v_old_balance - p_amount;

    /* 2) Check sufficient balance */
    IF v_new_balance < 0 THEN
        /* Not enough balances */
        ROLLBACK;
        SELECT 'INSUFFICIENT_BALANCE' AS status,
               p_balance_update_id AS balance_update_id,
               p_wallet_id AS wallet_id,
               'WITHDRAW' AS action,
               p_transaction_id AS transaction_id,
               v_currency AS currency,
               p_amount AS amount,
               v_old_balance AS old_balance,
               v_new_balance AS new_balance,
               p_description AS description,
               v_now AS transaction_at,
               v_now AS created_at;
        LEAVE proc_withdraw;
    END IF;

    /* 3) Apply deduction to balance */
    UPDATE wlt_wallet
    SET balance = v_new_balance
    WHERE wallet_id = p_wallet_id;

    /* 4) Insert balance update row */
    INSERT INTO wlt_balance_update (balance_update_id,
                                    wallet_id,
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
            p_wallet_id,
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
           bu.wallet_id,
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