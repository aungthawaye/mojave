SELECT
    COUNT(DISTINCT tt.transaction_id) AS total_tt_closed,
    COUNT(DISTINCT wpu.transaction_id) AS total_wpu,
    COUNT(DISTINCT alm.transaction_id) AS total_alm,
    COUNT(DISTINCT wpu.transaction_id) - COUNT(DISTINCT alm.transaction_id) AS diff_wpu_minus_alm
FROM txn_transaction tt
LEFT JOIN wlt_position_update wpu
    ON tt.transaction_id = wpu.transaction_id
LEFT JOIN acc_ledger_movement alm
    ON tt.transaction_id = alm.transaction_id
WHERE tt.phase = 'CLOSE';

