DELIMITER
$$
DROP PROCEDURE IF EXISTS `sp_reverse_fund` $$
CREATE PROCEDURE sp_reverse_fund(
                                IN p_reversing_balance_update_id BIGINT,
                                IN p_balance_update_id           BIGINT)
proc_reverse:
BEGIN
    DECLARE v_wallet_id BIGINT;
    DECLARE v_action VARCHAR(32);
    DECLARE v_amount DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_transaction_id BIGINT;
    DECLARE v_description VARCHAR(256);
    DECLARE v_now BIGINT;
    DECLARE v_old_balance DECIMAL(34, 4);
    DECLARE v_new_balance DECIMAL(34, 4);
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
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

    SET v_now = UNIX_TIMESTAMP();

    SELECT bu.balance_id,
           bu.action,
           bu.amount,
           bu.currency,
           bu.transaction_id,
           bu.description
    INTO v_wallet_id, v_action, v_amount, v_currency, v_transaction_id, v_description
    FROM mwe_balance_update bu
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

    IF v_action != 'WITHDRAW' THEN
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

    IF v_currency IS NULL THEN
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

    START TRANSACTION;

    SET v_not_found = FALSE;

    SELECT w.balance
    INTO v_old_balance
    FROM mwe_wallet w
    WHERE w.wallet_id = v_wallet_id FOR
    UPDATE;

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

    SET v_new_balance = v_old_balance + v_amount;

    UPDATE mwe_wallet
    SET balance = v_new_balance
    WHERE wallet_id = v_wallet_id;

    INSERT INTO mwe_balance_update (balance_update_id,
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
            v_wallet_id,
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
            0);

    COMMIT;

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
    FROM mwe_balance_update bu
    WHERE bu.balance_update_id = p_balance_update_id;
END $$


DELIMITER ;
