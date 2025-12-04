DELIMITER $$

DROP PROCEDURE IF EXISTS sp_post_ledger_batch_with_movements $$
CREATE PROCEDURE sp_post_ledger_batch_with_movements(
    IN p_lines_json JSON -- [{ledgerMovementId, step, accountId, side, amount, transactionId, transactionAt}, ...]
)
proc_posting:
BEGIN
    /* ---------------- Vars ---------------- */
    DECLARE v_ledger_movement_id BIGINT;
    DECLARE v_step INT;
    DECLARE v_account_id BIGINT;
    DECLARE v_side VARCHAR(16);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_amount DECIMAL(34, 4);
    DECLARE v_txn_id BIGINT;
    DECLARE v_txn_at BIGINT;
    DECLARE v_txn_type VARCHAR(32);
    DECLARE v_flow_definition_id BIGINT;
    DECLARE v_posting_definition_id BIGINT;
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
    DECLARE v_error_code VARCHAR(32);
    DECLARE v_err_account_id BIGINT;
    DECLARE v_err_side VARCHAR(32);
    DECLARE v_err_currency VARCHAR(3);
    DECLARE v_err_amount DECIMAL(34, 4);
    DECLARE v_err_debits DECIMAL(34, 4);
    DECLARE v_err_credits DECIMAL(34, 4);

    /* ---------------- Cursor over tmp_lines ---------------- */
    DECLARE c_lines CURSOR FOR
        SELECT ledger_movement_id,
               step,
               account_id,
               side,
               currency,
               amount,
               transaction_id,
               transaction_at,
               transaction_type,
               flow_definition_id,
               posting_definition_id
        FROM tmp_lines
        ORDER BY step;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    /* ---------------- Early no-op ---------------- */
    IF p_lines_json IS NULL OR JSON_TYPE(p_lines_json) <> 'ARRAY' OR
       JSON_LENGTH(p_lines_json) = 0 THEN
        SELECT 'IGNORED' AS status;
        LEAVE proc_posting;
    END IF;

    -- Make sure you drop the tables because when the Hikari connection pool reuses the same
    -- connection, sometimes you will get an error that says "tmp_lines already exists."
    DROP TABLE IF EXISTS tmp_lines;
    DROP TABLE IF EXISTS tmp_movements;

    /* ---------------- Temp staging ---------------- */
    CREATE TEMPORARY TABLE tmp_lines
    (
        ledger_movement_id    BIGINT         NOT NULL,
        step                  INT            NOT NULL,
        account_id            BIGINT         NOT NULL,
        side                  VARCHAR(32)    NOT NULL, -- 'DEBIT' | 'CREDIT'
        currency              VARCHAR(3)     NOT NULL,
        amount                DECIMAL(34, 4) NOT NULL,
        transaction_id        BIGINT         NOT NULL,
        transaction_at        BIGINT         NOT NULL,
        transaction_type      VARCHAR(32)    NOT NULL,
        flow_definition_id    BIGINT         NOT NULL,
        posting_definition_id BIGINT         NOT NULL
    ) ENGINE = MEMORY;

    CREATE TEMPORARY TABLE tmp_movements
    (
        ledger_movement_id    BIGINT         NOT NULL,
        step                  INT            NOT NULL,
        account_id            BIGINT         NOT NULL,
        side                  VARCHAR(32)    NOT NULL,
        currency              VARCHAR(3)     NOT NULL,
        amount                DECIMAL(34, 4) NOT NULL,
        old_debits            DECIMAL(34, 4) NOT NULL,
        old_credits           DECIMAL(34, 4) NOT NULL,
        new_debits            DECIMAL(34, 4) NOT NULL,
        new_credits           DECIMAL(34, 4) NOT NULL,
        transaction_id        BIGINT         NOT NULL,
        transaction_at        BIGINT         NOT NULL,
        transaction_type      VARCHAR(32)    NOT NULL,
        flow_definition_id    BIGINT         NOT NULL,
        posting_definition_id BIGINT         NOT NULL,
        movement_stage        VARCHAR(32)    NOT NULL,
        movement_result       VARCHAR(32)    NOT NULL,
        created_at            BIGINT         NOT NULL
    ) ENGINE = MEMORY;

    -- Initially copy all the JSON rows to tmp_lines.
    INSERT INTO tmp_lines
    SELECT jt.ledgerMovementId,
           jt.step,
           jt.accountId,
           jt.side,
           jt.currency,
           jt.amount,
           jt.transactionId,
           jt.transactionAt,
           jt.transactionType,
           jt.flowDefinitionId,
           jt.postingDefinitionId
    FROM JSON_TABLE(p_lines_json, '$[*]'
                    COLUMNS (
                        ledgerMovementId BIGINT PATH '$.ledgerMovementId',
                        step INT PATH '$.step',
                        accountId BIGINT PATH '$.accountId',
                        side VARCHAR(32) PATH '$.side',
                        currency VARCHAR(3) PATH '$.currency',
                        amount DECIMAL(34, 4) PATH '$.amount',
                        transactionId BIGINT PATH '$.transactionId',
                        transactionAt BIGINT PATH '$.transactionAt',
                        transactionType VARCHAR(32) PATH '$.transactionType',
                        flowDefinitionId BIGINT PATH '$.flowDefinitionId',
                        postingDefinitionId BIGINT PATH '$.postingDefinitionId'
                        )
         ) AS jt
    ORDER BY jt.step;

    /* ---------------- INITIATE the movements (with handlers) ---------------- */
    /* ---------- Detect duplicates before inserting ---------- */
    IF EXISTS (SELECT 1
               FROM acc_ledger_movement m
                        JOIN tmp_lines t
                             ON m.account_id = t.account_id
                                 AND m.side = t.side
                                 AND m.transaction_id = t.transaction_id) THEN
        /* Capture which row(s) conflict */
        SELECT t.step,
               t.account_id,
               t.side,
               t.transaction_id,
               t.amount,
               t.currency,
               m.ledger_movement_id AS existing_ledger_movement_id
        INTO
            v_step, v_err_account_id, v_err_side, v_txn_id, v_err_amount, v_err_currency, v_ledger_movement_id
        FROM acc_ledger_movement m
                 JOIN tmp_lines t
                      ON m.account_id = t.account_id
                          AND m.side = t.side
                          AND m.transaction_id = t.transaction_id
        LIMIT 1; -- If multiple duplicates, just take the first one

        SET v_error = 1;
        SET v_error_code = 'DUPLICATE_POSTING';

        SELECT 'ERROR'                 AS status,
               v_error_code            AS err_code,
               v_err_account_id        AS err_account_id,
               v_err_side              AS err_side,
               v_err_currency          AS err_currency,
               v_err_amount            AS err_amount,
               '0'                     AS err_debits,
               '0'                     AS err_credits,
               NULL                    AS ledger_movement_id,
               '0'                     AS step,
               NULL                    AS account_id,
               NULL                    AS side,
               NULL                    AS currency,
               NULL                    AS amount,
               NULL                    AS old_debits,
               NULL                    AS old_credits,
               NULL                    AS new_debits,
               NULL                    AS new_credits,
               NULL                    AS transaction_id,
               NULL                    AS transaction_at,
               NULL                    AS transaction_type,
               v_flow_definition_id    AS flow_definition_id,
               v_posting_definition_id AS posting_definition_id,
               'DEBIT_CREDIT'          AS movement_stage,
               'PENDING'               AS movement_result,
               UNIX_TIMESTAMP()        AS created_at;

        LEAVE proc_posting;
    END IF;

    INSERT INTO acc_ledger_movement (ledger_movement_id, step, account_id, side,
                                     currency, amount,
                                     old_debits, old_credits, new_debits,
                                     new_credits,
                                     transaction_id, transaction_at,
                                     transaction_type, flow_definition_id,
                                     posting_definition_id, movement_stage,
                                     movement_result,
                                     created_at, rec_created_at, rec_updated_at,
                                     rec_version)
    SELECT ledger_movement_id,
           step,
           account_id,
           side,
           currency,
           amount,
           0,
           0,
           0,
           0,
           transaction_id,
           transaction_at,
           transaction_type,
           flow_definition_id,
           posting_definition_id,
           'INITIATED',
           'PENDING',
           UNIX_TIMESTAMP(),
           UNIX_TIMESTAMP(),
           UNIX_TIMESTAMP(),
           1
    FROM tmp_lines;
    COMMIT;


    -- Iterate and post each line in its own transaction
    OPEN c_lines;

    post_loop:
    LOOP
        FETCH c_lines INTO v_ledger_movement_id, v_step, v_account_id, v_side, v_currency, v_amount, v_txn_id, v_txn_at, v_txn_type, v_flow_definition_id, v_posting_definition_id;
        IF done = 1 THEN
            LEAVE post_loop;
        END IF;

        -- T1: lock/update/stage
        BEGIN
            DECLARE EXIT HANDLER FOR SQLEXCEPTION
                BEGIN
                    /* Any SQL problem → mark and bail */
                    ROLLBACK;

                    SET v_error_code = COALESCE(v_error_code, 'SQL_ERROR');

                    SELECT 'ERROR'                 AS status,
                           v_error_code            AS err_code,
                           v_err_account_id        AS err_account_id,
                           v_err_side              AS err_side,
                           v_err_currency          AS err_currency,
                           v_err_amount            AS err_amount,
                           v_err_debits            AS err_debits,
                           v_err_credits           AS err_credits,
                           NULL                    AS ledger_movement_id,
                           v_step                  AS step,
                           v_account_id            AS account_id,
                           NULL                    AS side,
                           NULL                    AS currency,
                           NULL                    AS amount,
                           NULL                    AS old_debits,
                           NULL                    AS old_credits,
                           NULL                    AS new_debits,
                           NULL                    AS new_credits,
                           NULL                    AS transaction_id,
                           NULL                    AS transaction_at,
                           NULL                    AS transaction_type,
                           v_flow_definition_id    AS flow_definition_id,
                           v_posting_definition_id AS posting_definition_id,
                           'DEBIT_CREDIT'          AS movement_stage,
                           'PENDING'               AS movement_result,
                           UNIX_TIMESTAMP()        AS created_at;
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
                SET v_error_code = 'INSUFFICIENT_BALANCE';
                SET v_err_account_id = v_account_id;
                SET v_err_side = v_side;
                SET v_err_currency = v_currency;
                SET v_err_amount = v_amount;
                SET v_err_debits = v_dr_curr;
                SET v_err_credits = v_cr_curr;
                LEAVE post_loop;
            END IF;

            IF v_mode = 'LIMITED' AND v_signed_after < -v_limit THEN
                ROLLBACK;
                SET v_error = 1;
                SET v_error_code = 'OVERDRAFT_EXCEEDED';
                SET v_err_account_id = v_account_id;
                SET v_err_side = v_side;
                SET v_err_currency = v_currency;
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

            -- Update the movement together. So that, something goes wrong, we can know which movement is missing.
            UPDATE acc_ledger_movement
            SET old_debits      = v_dr_curr,
                old_credits     = v_cr_curr,
                new_debits      = v_dr_new,
                new_credits     = v_cr_new,
                movement_stage  = 'DEBIT_CREDIT',
                movement_result = 'SUCCESS'
            WHERE ledger_movement_id = v_ledger_movement_id;
            COMMIT;

            -- Stage movement row
            INSERT INTO tmp_movements (ledger_movement_id, step, account_id,
                                       side,
                                       currency, amount,
                                       old_debits, old_credits, new_debits,
                                       new_credits,
                                       transaction_id, transaction_at,
                                       transaction_type, flow_definition_id,
                                       posting_definition_id, movement_stage,
                                       movement_result, created_at)
            VALUES (v_ledger_movement_id,
                    v_step,
                    v_account_id,
                    v_side,
                    v_currency,
                    v_amount,
                    v_dr_curr,
                    v_cr_curr,
                    v_dr_new,
                    v_cr_new,
                    v_txn_id,
                    v_txn_at,
                    v_txn_type,
                    v_flow_definition_id,
                    v_posting_definition_id,
                    'DEBIT_CREDIT',
                    'SUCCESS',
                    UNIX_TIMESTAMP());
        END;
    END LOOP post_loop;
    CLOSE c_lines;
    -- end T1

    -- Is there any error?
    IF v_error <> 0 THEN
        -- There is an error. The last movement's ledger_movement_id must be marked as an error.
        START TRANSACTION;
        UPDATE acc_ledger_movement
        SET movement_stage  = 'DEBIT_CREDIT',
            movement_result = v_error_code
        WHERE ledger_movement_id = v_ledger_movement_id;
        COMMIT;

        -- Restore the balance of the successful rows which are prior to the error.
        BEGIN
            DECLARE r_done INT DEFAULT 0;
            DECLARE r_ledger_movement_id BIGINT;
            DECLARE r_step INT;
            DECLARE r_account_id BIGINT;
            DECLARE r_side VARCHAR(32);
            DECLARE r_currency VARCHAR(3);
            DECLARE r_amount DECIMAL(34, 4);
            DECLARE r_debits DECIMAL(34, 4);
            DECLARE r_credits DECIMAL(34, 4);

            DECLARE cur_rev CURSOR FOR
                SELECT ledger_movement_id,
                       step,
                       account_id,
                       side,
                       currency,
                       amount,
                       old_debits,
                       old_credits
                FROM tmp_movements
                ORDER BY step;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET r_done = 1;

            OPEN cur_rev;
            rev_loop:
            LOOP
                FETCH cur_rev INTO r_ledger_movement_id, r_step, r_account_id, r_side, r_currency, r_amount, r_debits, r_credits;
                IF r_done = 1 THEN
                    -- Nothing to rollback
                    LEAVE rev_loop;
                END IF;

                BEGIN
                    DECLARE EXIT HANDLER FOR SQLEXCEPTION
                        BEGIN
                            SELECT 'ERROR'              AS status,
                                   'RESTORE_FAILED'     AS err_code,
                                   r_account_id         AS err_account_id,
                                   r_side               AS err_side,
                                   r_currency           AS err_currency,
                                   r_amount             AS err_amount,
                                   r_debits             AS err_debits,
                                   r_credits            AS err_credits,
                                   r_ledger_movement_id AS ledger_movement_id,
                                   NULL                 AS step,
                                   NULL                 AS account_id,
                                   NULL                 AS side,
                                   NULL                 AS currency,
                                   NULL                 AS amount,
                                   NULL                 AS old_debits,
                                   NULL                 AS old_credits,
                                   NULL                 AS new_debits,
                                   NULL                 AS new_credits,
                                   NULL                 AS transaction_id,
                                   NULL                 AS transaction_at,
                                   NULL                 AS transaction_type,
                                   NULL                 AS flow_definition_id,
                                   NULL                 AS posting_definition_id,
                                   'DEBIT_CREDIT'       AS movement_stage,
                                   'PENDING'            AS movement_result,
                                   UNIX_TIMESTAMP()     AS created_at;
                            ROLLBACK;
                        END;
                    START TRANSACTION;

                    -- Previously we increased the debits or credits based on the side.
                    -- Now we decrease it to restore.
                    IF r_side = 'DEBIT' THEN
                        -- Restore for DEBIT
                        UPDATE acc_ledger_balance
                        SET posted_debits = posted_debits - r_amount
                        WHERE ledger_balance_id = r_account_id;
                    ELSEIF r_side = 'CREDIT' THEN
                        -- Restore for CREDIT
                        UPDATE acc_ledger_balance
                        SET posted_credits = posted_credits - r_amount
                        WHERE ledger_balance_id = r_account_id;
                    END IF;

                    -- After successfully reversed the dr/cr
                    -- Update the ledger movement with the result REVERSED
                    UPDATE acc_ledger_movement
                    SET movement_stage  = 'DEBIT_CREDIT',
                        movement_result = 'REVERSED'
                    WHERE ledger_movement_id = r_ledger_movement_id;
                    COMMIT;
                END;
            END LOOP rev_loop;
            CLOSE cur_rev;
        END;

        SELECT 'ERROR'          AS status,
               v_error_code     AS err_code,
               v_err_account_id AS err_account_id,
               v_err_side       AS err_side,
               v_err_currency   AS err_currency,
               v_err_amount     AS err_amount,
               v_err_debits     AS err_debits,
               v_err_credits    AS err_credits,
               NULL             AS ledger_movement_id,
               NULL             AS step,
               NULL             AS account_id,
               NULL             AS side,
               NULL             AS currency,
               NULL             AS amount,
               NULL             AS old_debits,
               NULL             AS old_credits,
               NULL             AS new_debits,
               NULL             AS new_credits,
               NULL             AS transaction_id,
               NULL             AS transaction_at,
               NULL             AS transaction_type,
               NULL             AS flow_definition_id,
               NULL             AS posting_definition_id,
               'DEBIT_CREDIT'   AS movement_stage,
               v_error_code     AS movement_result,
               UNIX_TIMESTAMP() AS created_at;

    ELSE
        -- There is no error.
        -- Update all the movements' stage to COMMIT
        START TRANSACTION;
        UPDATE acc_ledger_movement
        SET movement_stage  = 'COMMIT',
            movement_result = 'SUCCESS'
        WHERE transaction_id = v_txn_id;
        COMMIT;

        -- Return the movements.
        SELECT 'SUCCESS' AS status,
               NULL      AS err_code,
               NULL      AS err_account_id,
               NULL      AS err_side,
               NULL      AS err_currency,
               NULL      AS err_amount,
               NULL      AS err_debits,
               NULL      AS err_credits,
               ledger_movement_id,
               step,
               account_id,
               side,
               currency,
               amount,
               old_debits,
               old_credits,
               new_debits,
               new_credits,
               transaction_id,
               transaction_at,
               transaction_type,
               flow_definition_id,
               posting_definition_id,
               movement_stage,
               movement_result,
               created_at
        FROM acc_ledger_movement
        WHERE transaction_id = v_txn_id
        ORDER BY ledger_movement_id;
    END IF;

END proc_posting $$
DELIMITER ;
