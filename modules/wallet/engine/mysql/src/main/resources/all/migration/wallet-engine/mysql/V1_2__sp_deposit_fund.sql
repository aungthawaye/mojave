DELIMITER
$$
DROP PROCEDURE IF EXISTS `sp_deposit_fund` $$
CREATE PROCEDURE sp_deposit_fund(
                                IN p_transaction_id    BIGINT,
                                IN p_transaction_at    BIGINT,
                                IN p_balance_update_id BIGINT,
                                IN p_balance_id        BIGINT,
                                IN p_amount            DECIMAL(34, 4),
                                IN p_description       VARCHAR(256))
proc_deposit:
BEGIN
    DECLARE v_old_balance DECIMAL(34, 4);
    DECLARE v_new_balance DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_now BIGINT;
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;

        SELECT 'ERROR'             AS result,
               p_balance_update_id AS balance_update_id,
               p_balance_id        AS balance_id,
               'DEPOSIT'           AS action,
               p_transaction_id    AS transaction_id,
               NULL                AS currency,
               p_amount            AS amount,
               0                   AS old_balance,
               0                   AS new_balance,
               p_transaction_at    AS transaction_at;
    END;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_not_found = TRUE;

    SET v_now = UNIX_TIMESTAMP();

    START TRANSACTION;

    SELECT w.balance, dw.currency
    INTO v_old_balance, v_currency
    FROM mwe_wallet w
             JOIN wlt_wallet dw ON dw.wallet_id = w.wallet_id
    WHERE w.wallet_id = p_balance_id FOR
    UPDATE;

    IF v_not_found THEN
        ROLLBACK;

        SELECT 'ERROR'             AS result,
               p_balance_update_id AS balance_update_id,
               p_balance_id        AS balance_id,
               'DEPOSIT'           AS action,
               p_transaction_id    AS transaction_id,
               NULL                AS currency,
               p_amount            AS amount,
               0                   AS old_balance,
               0                   AS new_balance,
               p_transaction_at    AS transaction_at;

        LEAVE proc_deposit;
    END IF;

    SET v_new_balance = v_old_balance + p_amount;

    UPDATE mwe_wallet
    SET balance = v_new_balance
    WHERE wallet_id = p_balance_id;

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
                                    rec_created_at,
                                    rec_updated_at,
                                    rec_version)
    VALUES (p_balance_update_id,
            p_balance_id,
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

    SELECT 'SUCCESS' AS result,
           bu.balance_update_id,
           bu.balance_id,
           bu.action,
           bu.transaction_id,
           bu.currency,
           bu.amount,
           bu.old_balance,
           bu.new_balance,
           bu.transaction_at
    FROM mwe_balance_update bu
    WHERE bu.balance_update_id = p_balance_update_id;
END $$


DELIMITER ;
