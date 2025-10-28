DELIMITER $$

DROP PROCEDURE IF EXISTS sp_reverse_fund $$
CREATE PROCEDURE sp_reverse_fund(
    IN p_reversal_id BIGINT,
    IN p_balance_update_id BIGINT
)
proc_reverse:
BEGIN
    /* -------- Variables -------- */
    DECLARE v_wallet_id BIGINT;
    DECLARE v_action VARCHAR(32);
    DECLARE v_amount DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_transaction_id BIGINT;
    DECLARE v_description VARCHAR(256);

    DECLARE v_now BIGINT;
    DECLARE v_old_balance DECIMAL(34, 4);
    DECLARE v_new_balance DECIMAL(34, 4);

    /* Use epoch seconds for timestamps */
    SET v_now = UNIX_TIMESTAMP();

    /* 1) Fetch the original balance update row */
    SELECT bu.wallet_id,
           bu.action,
           bu.amount,
           bu.currency,
           bu.transaction_id,
           bu.description
    INTO
        v_wallet_id, v_action,
        v_amount,
        v_currency,
        v_transaction_id,
        v_description
    FROM wlt_balance_update bu
    WHERE bu.balance_update_id = p_reversal_id;

    /* 3) Branch by action */
    IF v_action = 'WITHDRAW' THEN

        START TRANSACTION;

        /* Reverse the withdrawal by crediting back the amount */
        SELECT w.balance
        INTO v_old_balance
        FROM wlt_wallet w
        WHERE w.wallet_id = v_wallet_id FOR
        UPDATE;

        SET v_new_balance = v_old_balance + v_amount;

        UPDATE wlt_wallet
        SET balance = v_new_balance
        WHERE wallet_id = v_wallet_id;

        /* Insert a new balance update row flagged as REVERSED */
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
                                        reversal_id,
                                        rec_created_at,
                                        rec_updated_at,
                                        rec_version)
        VALUES (p_balance_update_id,
                v_wallet_id,
                'REVERSED',
                v_transaction_id,
                v_currency,
                v_amount,
                v_old_balance,
                v_new_balance,
                v_description,
                v_now,
                v_now,
                p_reversal_id,
                v_now,
                v_now,
                0);
        COMMIT;

        /* 3.c) Return the reversal row */
        SELECT 'SUCCESS' AS status,
               bu.balance_update_id,
               bu.wallet_id,
               bu.action,
               bu.transaction_id,
               bu.currency,
               bu.amount,
               bu.old_balance,
               bu.new_balance,
               bu.transaction_at,
               bu.reversal_id
        FROM wlt_balance_update bu
        WHERE bu.balance_update_id = p_balance_update_id;

    ELSEIF v_action = 'DEPOSIT' THEN
        SELECT 'REVERSAL_FAILED' AS status,
               bu.balance_update_id,
               bu.wallet_id,
               bu.action,
               bu.transaction_id,
               bu.currency,
               bu.amount,
               bu.old_balance,
               bu.new_balance,
               bu.transaction_at,
               NULL              AS reversal_id
        FROM wlt_balance_update bu
        WHERE bu.balance_update_id = p_reversal_id;
    ELSE
        SELECT 'REVERSAL_FAILED' AS status,
               bu.balance_update_id,
               bu.wallet_id,
               bu.action,
               bu.transaction_id,
               bu.currency,
               bu.amount,
               bu.old_balance,
               bu.new_balance,
               bu.transaction_at,
               NULL              AS reversal_id
        FROM wlt_balance_update bu
        WHERE bu.balance_update_id = p_reversal_id;
    END IF;
END $$

DELIMITER ;
