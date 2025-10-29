DELIMITER $$

DROP PROCEDURE IF EXISTS sp_deposit_fund $$
CREATE PROCEDURE sp_deposit_fund(
    IN p_transaction_id BIGINT,
    IN p_transaction_at BIGINT,
    IN p_balance_update_id BIGINT,
    IN p_wallet_id BIGINT,
    IN p_amount DECIMAL(34, 4),
    IN p_description VARCHAR(256)
)
BEGIN
    /* -------- Variables -------- */
    DECLARE v_old_balance DECIMAL(34, 4);
    DECLARE v_new_balance DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_now BIGINT;

    /* Use epoch seconds for timestamps */
    SET v_now = UNIX_TIMESTAMP();

    START TRANSACTION;

    /* 1) Lock wallet row and capture snapshot */
    SELECT w.balance,
           w.currency
    INTO v_old_balance, v_currency
    FROM wlt_wallet w
    WHERE w.wallet_id = p_wallet_id FOR
    UPDATE;

    /* 2) Compute and apply new balance */
    SET v_new_balance = v_old_balance + p_amount;

    UPDATE wlt_wallet
    SET balance = v_new_balance
    WHERE wallet_id = p_wallet_id;

    /* 3) Insert balance update row */
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
            'DEPOSIT',
            p_transaction_id,
            v_currency,
            p_amount,
            v_old_balance,
            v_new_balance,
            p_description,
            p_transaction_at,
            v_now,
            v_now,
            v_now,
            0);
    COMMIT;

    /* 4) Return result */
    SELECT 'SUCCESS' AS result,
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
