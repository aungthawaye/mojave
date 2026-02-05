SELECT
	aa.code ,
	alb.currency ,
	alb.`scale` ,
	alb.nature ,
	alb.posted_debits ,
	alb.posted_credits
FROM
	acc_account aa
inner join acc_ledger_balance alb on
	aa.account_id = alb.ledger_balance_id ;

SELECT * from wlt_position;
