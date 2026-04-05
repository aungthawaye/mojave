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

    SELECT w.balance, w.currency
    INTO v_old_balance, v_currency
    FROM wlt_balance w
    WHERE w.balance_id = p_balance_id FOR
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

    UPDATE wlt_balance
    SET balance = v_new_balance
    WHERE balance_id = p_balance_id;

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
    FROM wlt_balance_update bu
    WHERE bu.balance_update_id = p_balance_update_id;
END $$

DROP PROCEDURE IF EXISTS `sp_withdraw_fund` $$
CREATE PROCEDURE sp_withdraw_fund(
                                 IN p_transaction_id    BIGINT,
                                 IN p_transaction_at    BIGINT,
                                 IN p_balance_update_id BIGINT,
                                 IN p_balance_id        BIGINT,
                                 IN p_amount            DECIMAL(34, 4),
                                 IN p_description       VARCHAR(256))
proc_withdraw:
BEGIN
    DECLARE v_old_balance DECIMAL(34, 4);
    DECLARE v_new_balance DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_now BIGINT;
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
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

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_not_found = TRUE;

    SET v_now = UNIX_TIMESTAMP();

    START TRANSACTION;

    SELECT w.balance, w.currency
    INTO v_old_balance, v_currency
    FROM wlt_balance w
    WHERE w.balance_id = p_balance_id FOR
    UPDATE;

    IF v_not_found THEN
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

        LEAVE proc_withdraw;
    END IF;

    SET v_new_balance = v_old_balance - p_amount;

    IF v_new_balance < 0 THEN
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

        LEAVE proc_withdraw;
    END IF;

    UPDATE wlt_balance
    SET balance = v_new_balance
    WHERE balance_id = p_balance_id;

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

DROP PROCEDURE IF EXISTS `sp_reverse_fund` $$
CREATE PROCEDURE sp_reverse_fund(
                                IN p_reversing_balance_update_id BIGINT,
                                IN p_balance_update_id           BIGINT)
proc_reverse:
BEGIN
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

    START TRANSACTION;

    SET v_not_found = FALSE;

    SELECT w.balance
    INTO v_old_balance
    FROM wlt_balance w
    WHERE w.balance_id = v_balance_id FOR
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

    UPDATE wlt_balance
    SET balance = v_new_balance
    WHERE balance_id = v_balance_id;

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
    FROM wlt_balance_update bu
    WHERE bu.balance_update_id = p_balance_update_id;
END $$

DROP PROCEDURE IF EXISTS `sp_decrease_position` $$
CREATE PROCEDURE sp_decrease_position(
                                     IN p_transaction_id     BIGINT,
                                     IN p_transaction_at     BIGINT,
                                     IN p_position_update_id BIGINT,
                                     IN p_position_id        BIGINT,
                                     IN p_amount             DECIMAL(34, 4),
                                     IN p_description        VARCHAR(256))
proc_decrease:
BEGIN
    DECLARE v_old_position DECIMAL(34, 4);
    DECLARE v_new_position DECIMAL(34, 4);
    DECLARE v_old_reserved DECIMAL(34, 4);
    DECLARE v_ndc DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_now BIGINT;
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;

        SELECT 'ERROR'              AS status,
               p_position_update_id AS position_update_id,
               p_position_id        AS position_id,
               'DECREASE'           AS action,
               p_transaction_id     AS transaction_id,
               NULL                 AS currency,
               p_amount             AS amount,
               0                    AS old_position,
               0                    AS new_position,
               0                    AS old_reserved,
               0                    AS new_reserved,
               0                    AS ndc,
               p_transaction_at     AS transaction_at;
    END;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_not_found = TRUE;

    SET v_now = UNIX_TIMESTAMP();

    START TRANSACTION;

    SELECT position,
           reserved,
           ndc,
           currency
    INTO v_old_position, v_old_reserved, v_ndc, v_currency
    FROM wlt_position
    WHERE position_id = p_position_id FOR
    UPDATE;

    IF v_not_found THEN
        ROLLBACK;

        SELECT 'ERROR'              AS status,
               p_position_update_id AS position_update_id,
               p_position_id        AS position_id,
               'DECREASE'           AS action,
               p_transaction_id     AS transaction_id,
               NULL                 AS currency,
               p_amount             AS amount,
               0                    AS old_position,
               0                    AS new_position,
               0                    AS old_reserved,
               0                    AS new_reserved,
               0                    AS ndc,
               p_transaction_at     AS transaction_at;

        LEAVE proc_decrease;
    END IF;

    SET v_new_position = v_old_position - p_amount;

    UPDATE wlt_position
    SET position = v_new_position
    WHERE position_id = p_position_id;

    INSERT INTO wlt_position_update (position_update_id,
                                     position_id,
                                     action,
                                     transaction_id,
                                     currency,
                                     amount,
                                     old_position,
                                     new_position,
                                     old_reserved,
                                     new_reserved,
                                     ndc,
                                     description,
                                     transaction_at,
                                     created_at,
                                     reservation_id,
                                     rec_created_at,
                                     rec_updated_at,
                                     rec_version)
    VALUES (p_position_update_id,
            p_position_id,
            'DECREASE',
            p_transaction_id,
            v_currency,
            p_amount,
            v_old_position,
            v_new_position,
            v_old_reserved,
            v_old_reserved,
            v_ndc,
            p_description,
            p_transaction_at,
            v_now,
            NULL,
            v_now,
            v_now,
            0);

    COMMIT;

    SELECT 'SUCCESS' AS status,
           pu.position_update_id,
           pu.position_id,
           pu.action,
           pu.transaction_id,
           pu.currency,
           pu.amount,
           pu.old_position,
           pu.new_position,
           pu.old_reserved,
           pu.new_reserved,
           pu.ndc,
           pu.transaction_at
    FROM wlt_position_update pu
    WHERE pu.position_update_id = p_position_update_id;
END $$

DROP PROCEDURE IF EXISTS `sp_increase_position` $$
CREATE PROCEDURE sp_increase_position(
                                     IN p_transaction_id     BIGINT,
                                     IN p_transaction_at     BIGINT,
                                     IN p_position_update_id BIGINT,
                                     IN p_position_id        BIGINT,
                                     IN p_amount             DECIMAL(34, 4),
                                     IN p_description        VARCHAR(256))
proc_increase:
BEGIN
    DECLARE v_old_position DECIMAL(34, 4);
    DECLARE v_new_position DECIMAL(34, 4);
    DECLARE v_old_reserved DECIMAL(34, 4);
    DECLARE v_ndc DECIMAL(34, 4);
    DECLARE v_limit DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_now BIGINT;
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;

        SELECT 'ERROR'              AS status,
               p_position_update_id AS position_update_id,
               p_position_id        AS position_id,
               'INCREASE'           AS action,
               p_transaction_id     AS transaction_id,
               NULL                 AS currency,
               p_amount             AS amount,
               0                    AS old_position,
               0                    AS new_position,
               0                    AS old_reserved,
               0                    AS new_reserved,
               0                    AS ndc,
               p_transaction_at     AS transaction_at;
    END;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_not_found = TRUE;

    SET v_now = UNIX_TIMESTAMP();

    START TRANSACTION;

    SELECT position,
           reserved,
           ndc,
           currency
    INTO v_old_position, v_old_reserved, v_ndc, v_currency
    FROM wlt_position
    WHERE position_id = p_position_id FOR
    UPDATE;

    IF v_not_found THEN
        ROLLBACK;

        SELECT 'ERROR'              AS status,
               p_position_update_id AS position_update_id,
               p_position_id        AS position_id,
               'INCREASE'           AS action,
               p_transaction_id     AS transaction_id,
               NULL                 AS currency,
               p_amount             AS amount,
               0                    AS old_position,
               0                    AS new_position,
               0                    AS old_reserved,
               0                    AS new_reserved,
               0                    AS ndc,
               p_transaction_at     AS transaction_at;

        LEAVE proc_increase;
    END IF;

    SET v_new_position = v_old_position + p_amount;
    SET v_limit = v_old_position + v_new_position;

    IF v_limit > v_ndc THEN
        ROLLBACK;

        SELECT 'LIMIT_EXCEEDED'     AS status,
               p_position_update_id AS position_update_id,
               p_position_id        AS position_id,
               'INCREASE'           AS action,
               p_transaction_id     AS transaction_id,
               v_currency           AS currency,
               p_amount             AS amount,
               v_old_position       AS old_position,
               v_new_position       AS new_position,
               v_old_reserved       AS old_reserved,
               v_old_reserved       AS new_reserved,
               v_ndc                AS ndc,
               p_transaction_at     AS transaction_at;

        LEAVE proc_increase;
    END IF;

    UPDATE wlt_position
    SET position = v_new_position
    WHERE position_id = p_position_id;

    INSERT INTO wlt_position_update (position_update_id,
                                     position_id,
                                     action,
                                     transaction_id,
                                     currency,
                                     amount,
                                     old_position,
                                     new_position,
                                     old_reserved,
                                     new_reserved,
                                     ndc,
                                     description,
                                     transaction_at,
                                     created_at,
                                     reservation_id,
                                     rec_created_at,
                                     rec_updated_at,
                                     rec_version)
    VALUES (p_position_update_id,
            p_position_id,
            'INCREASE',
            p_transaction_id,
            v_currency,
            p_amount,
            v_old_position,
            v_new_position,
            v_old_reserved,
            v_old_reserved,
            v_ndc,
            p_description,
            p_transaction_at,
            v_now,
            NULL,
            v_now,
            v_now,
            0);

    COMMIT;

    SELECT 'SUCCESS' AS status,
           pu.position_update_id,
           pu.position_id,
           pu.action,
           pu.transaction_id,
           pu.currency,
           pu.amount,
           pu.old_position,
           pu.new_position,
           pu.old_reserved,
           pu.new_reserved,
           pu.ndc,
           pu.transaction_at
    FROM wlt_position_update pu
    WHERE pu.position_update_id = p_position_update_id;
END $$

DROP PROCEDURE IF EXISTS `sp_reserve_position` $$
CREATE PROCEDURE sp_reserve_position(
                                    IN p_transaction_id     BIGINT,
                                    IN p_transaction_at     BIGINT,
                                    IN p_position_update_id BIGINT,
                                    IN p_position_id        BIGINT,
                                    IN p_amount             DECIMAL(34, 4),
                                    IN p_description        VARCHAR(256))
proc_reserve:
BEGIN
    DECLARE v_old_position DECIMAL(34, 4);
    DECLARE v_old_reserved DECIMAL(34, 4);
    DECLARE v_new_reserved DECIMAL(34, 4);
    DECLARE v_ndc DECIMAL(34, 4);
    DECLARE v_limit DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_now BIGINT;
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;

        SELECT 'ERROR'              AS status,
               p_position_update_id AS position_update_id,
               p_position_id        AS position_id,
               'RESERVE'            AS action,
               p_transaction_id     AS transaction_id,
               NULL                 AS currency,
               p_amount             AS amount,
               0                    AS old_position,
               0                    AS new_position,
               0                    AS old_reserved,
               0                    AS new_reserved,
               0                    AS ndc,
               p_transaction_at     AS transaction_at;
    END;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_not_found = TRUE;

    SET v_now = UNIX_TIMESTAMP();

    START TRANSACTION;

    SELECT position,
           reserved,
           ndc,
           currency
    INTO v_old_position, v_old_reserved, v_ndc, v_currency
    FROM wlt_position
    WHERE position_id = p_position_id FOR
    UPDATE;

    IF v_not_found THEN
        ROLLBACK;

        SELECT 'ERROR'              AS status,
               p_position_update_id AS position_update_id,
               p_position_id        AS position_id,
               'RESERVE'            AS action,
               p_transaction_id     AS transaction_id,
               NULL                 AS currency,
               p_amount             AS amount,
               0                    AS old_position,
               0                    AS new_position,
               0                    AS old_reserved,
               0                    AS new_reserved,
               0                    AS ndc,
               p_transaction_at     AS transaction_at;

        LEAVE proc_reserve;
    END IF;

    SET v_new_reserved = v_old_reserved + p_amount;
    SET v_limit = v_old_position + v_new_reserved;

    IF v_limit > v_ndc THEN
        ROLLBACK;

        SELECT 'LIMIT_EXCEEDED'     AS status,
               p_position_update_id AS position_update_id,
               p_position_id        AS position_id,
               'RESERVE'            AS action,
               p_transaction_id     AS transaction_id,
               v_currency           AS currency,
               p_amount             AS amount,
               v_old_position       AS old_position,
               v_old_position       AS new_position,
               v_old_reserved       AS old_reserved,
               v_new_reserved       AS new_reserved,
               v_ndc                AS ndc,
               p_transaction_at     AS transaction_at;

        LEAVE proc_reserve;
    END IF;

    UPDATE wlt_position
    SET reserved = v_new_reserved
    WHERE position_id = p_position_id;

    INSERT INTO wlt_position_update (position_update_id,
                                     position_id,
                                     action,
                                     transaction_id,
                                     currency,
                                     amount,
                                     old_position,
                                     new_position,
                                     old_reserved,
                                     new_reserved,
                                     ndc,
                                     description,
                                     transaction_at,
                                     created_at,
                                     reservation_id,
                                     rec_created_at,
                                     rec_updated_at,
                                     rec_version)
    VALUES (p_position_update_id,
            p_position_id,
            'RESERVE',
            p_transaction_id,
            v_currency,
            p_amount,
            v_old_position,
            v_old_position,
            v_old_reserved,
            v_new_reserved,
            v_ndc,
            p_description,
            p_transaction_at,
            v_now,
            NULL,
            v_now,
            v_now,
            0);

    COMMIT;

    SELECT 'SUCCESS' AS status,
           pu.position_update_id,
           pu.position_id,
           pu.action,
           pu.transaction_id,
           pu.currency,
           pu.amount,
           pu.old_position,
           pu.new_position,
           pu.old_reserved,
           pu.new_reserved,
           pu.ndc,
           pu.transaction_at
    FROM wlt_position_update pu
    WHERE pu.position_update_id = p_position_update_id;
END $$

DROP PROCEDURE IF EXISTS `sp_rollback_position` $$
CREATE PROCEDURE sp_rollback_position(
                                     IN p_reservation_id     BIGINT,
                                     IN p_position_update_id BIGINT)
proc_rollback:
BEGIN
    DECLARE v_position_id BIGINT;
    DECLARE v_action VARCHAR(32);
    DECLARE v_amount DECIMAL(34, 4);
    DECLARE v_transaction_id BIGINT;
    DECLARE v_transaction_at BIGINT;
    DECLARE v_description VARCHAR(255);
    DECLARE v_now BIGINT;
    DECLARE v_old_position DECIMAL(34, 4);
    DECLARE v_old_reserved DECIMAL(34, 4);
    DECLARE v_new_reserved DECIMAL(34, 4);
    DECLARE v_ndc DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;

        SELECT 'ROLLBACK_FAILED' AS status,
               p_reservation_id  AS position_update_id,
               v_position_id     AS position_id,
               'ROLLBACK'        AS action,
               v_transaction_id  AS transaction_id,
               v_currency        AS currency,
               v_amount          AS amount,
               v_old_position    AS old_position,
               v_old_position    AS new_position,
               v_old_reserved    AS old_reserved,
               v_new_reserved    AS new_reserved,
               v_ndc             AS ndc,
               v_transaction_at  AS transaction_at;
    END;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_not_found = TRUE;

    SET v_now = UNIX_TIMESTAMP();

    SELECT position_id,
           amount,
           transaction_id,
           transaction_at,
           action,
           description
    INTO v_position_id, v_amount, v_transaction_id, v_transaction_at, v_action, v_description
    FROM wlt_position_update
    WHERE position_update_id = p_reservation_id;

    IF v_not_found OR v_action != 'RESERVE' THEN
        SELECT 'ROLLBACK_FAILED' AS status,
               p_reservation_id  AS position_update_id,
               v_position_id     AS position_id,
               'ROLLBACK'        AS action,
               v_transaction_id  AS transaction_id,
               v_currency        AS currency,
               v_amount          AS amount,
               v_old_position    AS old_position,
               v_old_position    AS new_position,
               v_old_reserved    AS old_reserved,
               v_new_reserved    AS new_reserved,
               v_ndc             AS ndc,
               v_transaction_at  AS transaction_at;

        LEAVE proc_rollback;
    END IF;

    START TRANSACTION;

    SET v_not_found = FALSE;

    SELECT position,
           reserved,
           ndc,
           currency
    INTO v_old_position, v_old_reserved, v_ndc, v_currency
    FROM wlt_position
    WHERE position_id = v_position_id FOR
    UPDATE;

    IF v_not_found THEN
        ROLLBACK;

        SELECT 'ROLLBACK_FAILED' AS status,
               p_reservation_id  AS position_update_id,
               v_position_id     AS position_id,
               'ROLLBACK'        AS action,
               v_transaction_id  AS transaction_id,
               v_currency        AS currency,
               v_amount          AS amount,
               v_old_position    AS old_position,
               v_old_position    AS new_position,
               v_old_reserved    AS old_reserved,
               v_new_reserved    AS new_reserved,
               v_ndc             AS ndc,
               v_transaction_at  AS transaction_at;

        LEAVE proc_rollback;
    END IF;

    SET v_new_reserved = v_old_reserved - v_amount;

    UPDATE wlt_position
    SET reserved = v_new_reserved
    WHERE position_id = v_position_id;

    INSERT INTO wlt_position_update (position_update_id,
                                     position_id,
                                     action,
                                     transaction_id,
                                     currency,
                                     amount,
                                     old_position,
                                     new_position,
                                     old_reserved,
                                     new_reserved,
                                     ndc,
                                     description,
                                     transaction_at,
                                     created_at,
                                     reservation_id,
                                     rec_created_at,
                                     rec_updated_at,
                                     rec_version)
    VALUES (p_position_update_id,
            v_position_id,
            'ROLLBACK',
            v_transaction_id,
            v_currency,
            v_amount,
            v_old_position,
            v_old_position,
            v_old_reserved,
            v_new_reserved,
            v_ndc,
            v_description,
            v_transaction_at,
            v_now,
            p_reservation_id,
            v_now,
            v_now,
            0);

    COMMIT;

    SELECT 'SUCCESS' AS status,
           pu.position_update_id,
           pu.position_id,
           pu.action,
           pu.transaction_id,
           pu.currency,
           pu.amount,
           pu.old_position,
           pu.new_position,
           pu.old_reserved,
           pu.new_reserved,
           pu.ndc,
           pu.transaction_at
    FROM wlt_position_update pu
    WHERE pu.position_update_id = p_position_update_id;
END $$

DROP PROCEDURE IF EXISTS `sp_commit_position` $$
CREATE PROCEDURE sp_commit_position(
                                   IN p_reservation_id     BIGINT,
                                   IN p_position_update_id BIGINT)
proc_commit:
BEGIN
    DECLARE v_position_id BIGINT;
    DECLARE v_action VARCHAR(32);
    DECLARE v_amount DECIMAL(34, 4);
    DECLARE v_transaction_id BIGINT;
    DECLARE v_transaction_at BIGINT;
    DECLARE v_description VARCHAR(255);
    DECLARE v_now BIGINT;
    DECLARE v_old_position DECIMAL(34, 4);
    DECLARE v_new_position DECIMAL(34, 4);
    DECLARE v_old_reserved DECIMAL(34, 4);
    DECLARE v_new_reserved DECIMAL(34, 4);
    DECLARE v_ndc DECIMAL(34, 4);
    DECLARE v_currency VARCHAR(3);
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;

        SELECT 'COMMIT_FAILED'  AS status,
               p_reservation_id AS position_update_id,
               v_position_id    AS position_id,
               'COMMIT'         AS action,
               v_transaction_id AS transaction_id,
               v_currency       AS currency,
               v_amount         AS amount,
               v_old_position   AS old_position,
               v_old_position   AS new_position,
               v_old_reserved   AS old_reserved,
               v_new_reserved   AS new_reserved,
               v_ndc            AS ndc,
               v_transaction_at AS transaction_at;
    END;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_not_found = TRUE;

    SET v_now = UNIX_TIMESTAMP();

    SELECT position_id,
           amount,
           transaction_id,
           transaction_at,
           action,
           description
    INTO v_position_id, v_amount, v_transaction_id, v_transaction_at, v_action, v_description
    FROM wlt_position_update
    WHERE position_update_id = p_reservation_id;

    IF v_not_found OR v_action != 'RESERVE' THEN
        SELECT 'COMMIT_FAILED'  AS status,
               p_reservation_id AS position_update_id,
               v_position_id    AS position_id,
               'COMMIT'         AS action,
               v_transaction_id AS transaction_id,
               v_currency       AS currency,
               v_amount         AS amount,
               v_old_position   AS old_position,
               v_old_position   AS new_position,
               v_old_reserved   AS old_reserved,
               v_new_reserved   AS new_reserved,
               v_ndc            AS ndc,
               v_transaction_at AS transaction_at;

        LEAVE proc_commit;
    END IF;

    START TRANSACTION;

    SET v_not_found = FALSE;

    SELECT position,
           reserved,
           ndc,
           currency
    INTO v_old_position, v_old_reserved, v_ndc, v_currency
    FROM wlt_position
    WHERE position_id = v_position_id FOR
    UPDATE;

    IF v_not_found THEN
        ROLLBACK;

        SELECT 'COMMIT_FAILED'  AS status,
               p_reservation_id AS position_update_id,
               v_position_id    AS position_id,
               'COMMIT'         AS action,
               v_transaction_id AS transaction_id,
               v_currency       AS currency,
               v_amount         AS amount,
               v_old_position   AS old_position,
               v_old_position   AS new_position,
               v_old_reserved   AS old_reserved,
               v_new_reserved   AS new_reserved,
               v_ndc            AS ndc,
               v_transaction_at AS transaction_at;

        LEAVE proc_commit;
    END IF;

    SET v_new_reserved = v_old_reserved - v_amount;
    SET v_new_position = v_old_position + v_amount;

    UPDATE wlt_position
    SET position = v_new_position, reserved = v_new_reserved
    WHERE position_id = v_position_id;

    INSERT INTO wlt_position_update (position_update_id,
                                     position_id,
                                     action,
                                     transaction_id,
                                     currency,
                                     amount,
                                     old_position,
                                     new_position,
                                     old_reserved,
                                     new_reserved,
                                     ndc,
                                     description,
                                     transaction_at,
                                     created_at,
                                     reservation_id,
                                     rec_created_at,
                                     rec_updated_at,
                                     rec_version)
    VALUES (p_position_update_id,
            v_position_id,
            'COMMIT',
            v_transaction_id,
            v_currency,
            v_amount,
            v_old_position,
            v_new_position,
            v_old_reserved,
            v_new_reserved,
            v_ndc,
            v_description,
            v_transaction_at,
            v_now,
            p_reservation_id,
            v_now,
            v_now,
            0);

    COMMIT;

    SELECT 'SUCCESS' AS status,
           pu.position_update_id,
           pu.position_id,
           pu.action,
           pu.transaction_id,
           pu.currency,
           pu.amount,
           pu.old_position,
           pu.new_position,
           pu.old_reserved,
           pu.new_reserved,
           pu.ndc,
           pu.transaction_at
    FROM wlt_position_update pu
    WHERE pu.position_update_id = p_position_update_id;
END $$

DROP PROCEDURE IF EXISTS `sp_fulfil_positions` $$
CREATE PROCEDURE sp_fulfil_positions(
                                    IN p_reservation_id        BIGINT,
                                    IN p_reservation_commit_id BIGINT,
                                    IN p_position_decrement_id BIGINT,
                                    IN p_payee_position_id     BIGINT,
                                    IN p_description           VARCHAR(256))
proc_fulfil:
BEGIN
    DECLARE v_payer_position_id BIGINT;
    DECLARE v_action VARCHAR(32);
    DECLARE v_amount DECIMAL(34, 4);
    DECLARE v_transaction_id BIGINT;
    DECLARE v_transaction_at BIGINT;
    DECLARE v_description VARCHAR(255);
    DECLARE v_now BIGINT;
    DECLARE v_payer_old_position DECIMAL(34, 4);
    DECLARE v_payer_new_position DECIMAL(34, 4);
    DECLARE v_payer_old_reserved DECIMAL(34, 4);
    DECLARE v_payer_new_reserved DECIMAL(34, 4);
    DECLARE v_payer_ndc DECIMAL(34, 4);
    DECLARE v_payer_currency VARCHAR(3);
    DECLARE v_payee_old_position DECIMAL(34, 4);
    DECLARE v_payee_new_position DECIMAL(34, 4);
    DECLARE v_payee_old_reserved DECIMAL(34, 4);
    DECLARE v_payee_new_reserved DECIMAL(34, 4);
    DECLARE v_payee_ndc DECIMAL(34, 4);
    DECLARE v_payee_currency VARCHAR(3);
    DECLARE v_not_found BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;

        SELECT 'ERROR'         AS status,
               'FULFIL_FAILED' AS err_error_code,
               NULL            AS payer_commit_id,
               NULL            AS payee_commit_id;
    END;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_not_found = TRUE;

    SET v_now = UNIX_TIMESTAMP();

    SELECT position_id,
           amount,
           transaction_id,
           transaction_at,
           action,
           description
    INTO v_payer_position_id, v_amount, v_transaction_id, v_transaction_at, v_action, v_description
    FROM wlt_position_update
    WHERE position_update_id = p_reservation_id;

    IF v_not_found OR v_action != 'RESERVE' THEN
        SELECT 'ERROR'         AS status,
               'COMMIT_FAILED' AS err_error_code,
               NULL            AS payer_commit_id,
               NULL            AS payee_commit_id;

        LEAVE proc_fulfil;
    END IF;

    START TRANSACTION;

    SET v_not_found = FALSE;

    SELECT position,
           reserved,
           ndc,
           currency
    INTO v_payer_old_position, v_payer_old_reserved, v_payer_ndc, v_payer_currency
    FROM wlt_position
    WHERE position_id = v_payer_position_id FOR
    UPDATE;

    IF v_not_found THEN
        ROLLBACK;

        SELECT 'ERROR'                    AS status,
               'PAYER_POSITION_NOT_FOUND' AS err_error_code,
               NULL                       AS payer_commit_id,
               NULL                       AS payee_commit_id;

        LEAVE proc_fulfil;
    END IF;

    SET v_payer_new_reserved = v_payer_old_reserved - v_amount;
    SET v_payer_new_position = v_payer_old_position + v_amount;

    UPDATE wlt_position
    SET position = v_payer_new_position, reserved = v_payer_new_reserved
    WHERE position_id = v_payer_position_id;

    SET v_not_found = FALSE;

    SELECT position,
           reserved,
           ndc,
           currency
    INTO v_payee_old_position, v_payee_old_reserved, v_payee_ndc, v_payee_currency
    FROM wlt_position
    WHERE position_id = p_payee_position_id FOR
    UPDATE;

    IF v_not_found THEN
        ROLLBACK;

        SELECT 'ERROR'                    AS status,
               'PAYEE_POSITION_NOT_FOUND' AS err_error_code,
               NULL                       AS payer_commit_id,
               NULL                       AS payee_commit_id;

        LEAVE proc_fulfil;
    END IF;

    IF v_payee_currency != v_payer_currency THEN
        ROLLBACK;

        SELECT 'ERROR'                 AS status,
               'CURRENCIES_MISMATCHED' AS err_error_code,
               NULL                    AS payer_commit_id,
               NULL                    AS payee_commit_id;

        LEAVE proc_fulfil;
    END IF;

    SET v_payee_new_position = v_payee_old_position - v_amount;
    SET v_payee_new_reserved = v_payee_old_reserved;

    UPDATE wlt_position
    SET position = v_payee_new_position
    WHERE position_id = p_payee_position_id;

    INSERT INTO wlt_position_update (position_update_id,
                                     position_id,
                                     action,
                                     transaction_id,
                                     currency,
                                     amount,
                                     old_position,
                                     new_position,
                                     old_reserved,
                                     new_reserved,
                                     ndc,
                                     description,
                                     transaction_at,
                                     created_at,
                                     reservation_id,
                                     rec_created_at,
                                     rec_updated_at,
                                     rec_version)
    VALUES (p_reservation_commit_id,
            v_payer_position_id,
            'COMMIT',
            v_transaction_id,
            v_payer_currency,
            v_amount,
            v_payer_old_position,
            v_payer_new_position,
            v_payer_old_reserved,
            v_payer_new_reserved,
            v_payer_ndc,
            p_description,
            v_transaction_at,
            v_now,
            p_reservation_id,
            v_now,
            v_now,
            0);

    INSERT INTO wlt_position_update (position_update_id,
                                     position_id,
                                     action,
                                     transaction_id,
                                     currency,
                                     amount,
                                     old_position,
                                     new_position,
                                     old_reserved,
                                     new_reserved,
                                     ndc,
                                     description,
                                     transaction_at,
                                     created_at,
                                     reservation_id,
                                     rec_created_at,
                                     rec_updated_at,
                                     rec_version)
    VALUES (p_position_decrement_id,
            p_payee_position_id,
            'DECREASE',
            v_transaction_id,
            v_payee_currency,
            v_amount,
            v_payee_old_position,
            v_payee_new_position,
            v_payee_old_reserved,
            v_payee_new_reserved,
            v_payee_ndc,
            p_description,
            v_transaction_at,
            v_now,
            NULL,
            v_now,
            v_now,
            0);

    COMMIT;

    SELECT 'SUCCESS'               AS status,
           NULL                    AS err_error_code,
           p_reservation_commit_id AS payer_commit_id,
           p_position_decrement_id AS payee_commit_id;
END $$

DELIMITER ;
