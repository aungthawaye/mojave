DELIMITER $$

DROP PROCEDURE IF EXISTS sp_post_ledger_batch_with_movements $$
CREATE PROCEDURE sp_post_ledger_batch_with_movements(
    IN p_lines_json JSON -- [{ledgerMovementId, accountId, side, amount, transactionId, transactionAt}, ...]
)
proc_end:
BEGIN
    /* ---------------- Vars ---------------- */
    DECLARE v_idx INT;
    DECLARE v_ledger_movement_id BIGINT;
    DECLARE v_account_id BIGINT;
    DECLARE v_side VARCHAR(16);
    DECLARE v_amount DECIMAL(34, 4);
    DECLARE v_txn_id BIGINT;
    DECLARE v_txn_at BIGINT;
    DECLARE done INT DEFAULT 0;

    -- Locked balance snapshot
    DECLARE v_dr_curr DECIMAL(34, 4);
    DECLARE v_cr_curr DECIMAL(34, 4);
    DECLARE v_nature VARCHAR(16);
    DECLARE v_mode VARCHAR(16);
    DECLARE v_limit DECIMAL(34, 4);
    DECLARE v_signed_after DECIMAL(34, 4);

    -- Computed/apply
    DECLARE v_dr_new DECIMAL(34, 4);
    DECLARE v_cr_new DECIMAL(34, 4);

    -- Error code
    DECLARE v_error INT DEFAULT 0; -- 0 = no error
    DECLARE v_status_code VARCHAR(32);
    DECLARE v_err_account_id BIGINT;
    DECLARE v_err_side VARCHAR(32);
    DECLARE v_err_amount DECIMAL(34, 4);
    DECLARE v_err_debits DECIMAL(34, 4);
    DECLARE v_err_credits DECIMAL(34, 4);

    /* ---------------- Cursor ---------------- */
    DECLARE c_lines CURSOR FOR
        SELECT jt.idx,
               jt.ledgerMovementId,
               jt.accountId,
               UPPER(jt.side) AS side,
               jt.amount,
               jt.transactionId,
               jt.transactionAt
        FROM JSON_TABLE(p_lines_json, '$[*]'
                        COLUMNS (
                            idx FOR ORDINALITY,
                            ledgerMovementId BIGINT PATH '$.ledgerMovementId',
                            accountId BIGINT PATH '$.accountId',
                            side VARCHAR(32) PATH '$.side',
                            amount DECIMAL(34, 4) PATH '$.amount',
                            transactionId BIGINT PATH '$.transactionId',
                            transactionAt BIGINT PATH '$.transactionAt'
                            )
             ) AS jt;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    /* ---------------- Early no-op ---------------- */
    IF p_lines_json IS NULL OR JSON_TYPE(p_lines_json) <> 'ARRAY' OR JSON_LENGTH(p_lines_json) = 0 THEN
        SELECT 'IGNORED' AS status;
        LEAVE proc_end;
    END IF;

    /* ---------------- Temp staging ---------------- */
    CREATE TEMPORARY TABLE IF NOT EXISTS tmp_movements
    (
        ledger_movement_id BIGINT         NOT NULL,
        account_id         BIGINT         NOT NULL,
        side               VARCHAR(32)    NOT NULL,
        amount             DECIMAL(34, 4) NOT NULL,
        old_debits         DECIMAL(34, 4) NOT NULL,
        old_credits        DECIMAL(34, 4) NOT NULL,
        new_debits         DECIMAL(34, 4) NOT NULL,
        new_credits        DECIMAL(34, 4) NOT NULL,
        transaction_id     BIGINT,
        transaction_at     BIGINT,
        created_at         BIGINT         NOT NULL
    ) ENGINE = MEMORY;

    TRUNCATE TABLE tmp_movements;


    -- Iterate and post each line in its own transaction
    OPEN c_lines;

    post_loop:
    LOOP
        FETCH c_lines INTO v_idx, v_ledger_movement_id, v_account_id, v_side, v_amount, v_txn_id, v_txn_at;
        IF done = 1 THEN
            LEAVE post_loop;
        END IF;

        -- T1: lock/update/stage
        BEGIN
            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                BEGIN
                    /* Any SQL problem → mark and bail */
                    ROLLBACK;
                    SELECT 'ERROR'          AS status,
                           v_status_code    AS code,
                           v_err_account_id AS account_id,
                           v_err_side       AS side,
                           v_err_amount     AS amount,
                           v_err_debits     AS debits,
                           v_err_credits    AS credits;
                END;

            START TRANSACTION;
            -- Lock the balance row + overdraft policy
            SELECT posted_debits,
                   posted_credits,
                   nature,
                   overdraft_mode,
                   COALESCE(overdraft_limit, 0)
            INTO
                v_dr_curr, v_cr_curr, v_nature, v_mode, v_limit
            FROM acc_ledger_balance
            WHERE ledger_balance_id = v_account_id FOR
            UPDATE;

            -- Compute new totals
            IF v_side = 'DEBIT' THEN
                SET v_dr_new = v_dr_curr + v_amount;
                SET v_cr_new = v_cr_curr;
            ELSEIF v_side = 'CREDIT' THEN
                SET v_dr_new = v_dr_curr;
                SET v_cr_new = v_cr_curr + v_amount;
            END IF;

            -- Signed-after and overdraft checks
            IF v_nature = 'DEBIT' THEN
                SET v_signed_after = v_dr_new - v_cr_new;
            ELSE
                SET v_signed_after = v_cr_new - v_dr_new;
            END IF;

            IF v_mode = 'FORBID' AND v_signed_after < 0 THEN
                ROLLBACK;
                SET v_error = 1;
                SET v_status_code = 'INSUFFICIENT_BALANCE';
                SET v_err_account_id = v_account_id;
                SET v_err_side = v_side;
                SET v_err_amount = v_amount;
                SET v_err_debits = v_dr_curr;
                SET v_err_credits = v_cr_curr;
                LEAVE post_loop;
            END IF;

            IF v_mode = 'LIMITED' AND v_signed_after < -v_limit THEN
                ROLLBACK;
                SET v_error = 1;
                SET v_status_code = 'OVERDRAFT_EXCEEDED';
                SET v_err_account_id = v_account_id;
                SET v_err_side = v_side;
                SET v_err_amount = v_amount;
                SET v_err_debits = v_dr_curr;
                SET v_err_credits = v_cr_curr;
                LEAVE post_loop;
            END IF;

            -- Apply update
            UPDATE acc_ledger_balance
            SET posted_debits  = v_dr_new,
                posted_credits = v_cr_new
            WHERE ledger_balance_id = v_account_id;
            -- Unlock the balance row and commit asap.
            COMMIT;

            -- Stage movement row
            INSERT INTO tmp_movements (ledger_movement_id, account_id, side, amount,
                                       old_debits, old_credits, new_debits, new_credits,
                                       transaction_id, transaction_at, created_at)
            VALUES (v_ledger_movement_id, v_account_id, v_side, v_amount,
                    v_dr_curr, v_cr_curr,
                    v_dr_new, v_cr_new,
                    v_txn_id, v_txn_at, UNIX_TIMESTAMP());
        END;
    END LOOP post_loop;
    CLOSE c_lines;
    -- end T1

    -- Is there any error?
    IF v_error <> 0 THEN
        -- There is an error. Restore the balance row.
        BEGIN
            DECLARE r_done INT DEFAULT 0;
            DECLARE r_account_id BIGINT;
            DECLARE r_side VARCHAR(32);
            DECLARE r_amount DECIMAL(34, 4);
            DECLARE r_debits DECIMAL(34, 4);
            DECLARE r_credits DECIMAL(34, 4);

            DECLARE r_lock INT DEFAULT 0;

            DECLARE cur_rev CURSOR FOR
                SELECT account_id, side, amount, old_debits, old_credits FROM tmp_movements;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET r_done = 1;

            OPEN cur_rev;
            rev_loop:
            LOOP
                FETCH cur_rev INTO r_account_id, r_side, r_amount, r_debits, r_credits;
                IF r_done = 1 THEN
                    -- Nothing to rollback
                    LEAVE rev_loop;
                END IF;

                BEGIN
                    DECLARE EXIT HANDLER FOR SQLEXCEPTION
                        BEGIN
                            SELECT 'ERROR'          AS status,
                                   'RESTORE_FAILED' AS code,
                                   r_account_id     AS account_id,
                                   r_side           AS side,
                                   r_amount         AS amount,
                                   r_debits         AS debits,
                                   r_credits        AS credits;
                            ROLLBACK;
                        END;
                    START TRANSACTION;
                    -- Previously we increased the debits or credits based on the side.
                    -- Now we decrease it to restore.
                    IF r_side = 'DEBIT' THEN
                        UPDATE acc_ledger_balance
                        SET posted_debits = posted_debits - r_amount
                        WHERE ledger_balance_id = r_account_id;
                    ELSEIF r_side = 'CREDIT' THEN
                        UPDATE acc_ledger_balance
                        SET posted_credits = posted_credits - r_amount
                        WHERE ledger_balance_id = r_account_id;
                    END IF;
                    COMMIT;
                END;
            END LOOP rev_loop;
            CLOSE cur_rev;
        END;

        SELECT 'ERROR'          AS status,
               v_status_code    AS code,
               v_err_account_id AS account_id,
               v_err_side       AS side,
               v_err_amount     AS amount,
               v_err_debits     AS debits,
               v_err_credits    AS credits;

    ELSE
        -- There is no error. Insert the movements.
        -- Return the movements.
        START TRANSACTION;

        INSERT INTO acc_ledger_movement (id, account_id, side, amount,
                                         old_debits, old_credits, new_debits, new_credits,
                                         transaction_id, transaction_at, created_at)
        SELECT ledger_movement_id,
               account_id,
               side,
               amount,
               old_debits,
               old_credits,
               new_debits,
               new_credits,
               transaction_id,
               transaction_at,
               created_at
        FROM tmp_movements;
        COMMIT; -- end T2

        SELECT 'SUCCESS' AS status,
               ledger_movement_id,
               account_id,
               side,
               amount,
               old_debits,
               old_credits,
               new_debits,
               new_credits,
               transaction_id,
               transaction_at,
               created_at
        FROM tmp_movements;
    END IF;

END proc_end $$
DELIMITER ;
