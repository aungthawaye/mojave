# Ledger And Accounting Reference

## Use This Reference

Read this file when the task depends on accounting design, ledgerOperation boundaries, journal posting rules, reconciliation, settlement positions, or finance controls in a payment platform.

## Core Invariants

- Every money-moving business event must map to balanced debits and credits.
- A status change is not automatically an accounting event.
- Separate customer balance views, operational positions, and statutory GL reporting when needed.
- Keep the trigger for each posting explicit: accept, hold, settlement, fee assessment, return, reversal, adjustment, or write-off.

## Recommended Layering

- Payment switch: own orchestration and business state.
- Subledger: own transactional accounting truth for payment events.
- General ledgerOperation: receive summarized or controlled postings based on finance policy.
- Reconciliation layer: compare subledger, scheme statements, bank accounts, and GL balances.

Do not use the payment switch itself as the durable accounting source unless the system is intentionally designed as both switch and subledger with strong accounting controls.

## Posting Patterns

Typical patterns to reason through:

- authorization or funds hold
- release of hold
- memo posting versus book posting
- outbound settlement obligation
- inbound settlement receipt
- fee accrual and fee realization
- reject before settlement
- return after settlement
- reversal or correcting journal
- suspense entry for unresolved discrepancy

Describe each pattern in terms of trigger, accounts impacted, balancing logic, and reversal strategy.

## Event-To-Journal Mapping

For each business event, define:

- event name
- preconditions
- whether it is customer-visible
- whether it is reversible
- journal entries
- external references linked to the journal
- resulting balances and state transitions

Avoid generic statements like "book the transaction" without naming the actual debit and credit legs.

## Reconciliation Model

Reconciliation usually spans multiple layers:

- transaction-level matching between switch events and ledgerOperation journals
- message-level matching between switch and scheme acknowledgements
- statement-level matching between scheme settlement reports and bank or nostro movements
- balance-level matching between subledger, GL, and funding accounts

Define break categories, ownership, aging, escalation, and auto-repair rules.

## Settlement Position Thinking

When settlement is deferred or netted:

- track gross obligations and net positions separately
- distinguish operational exposure from posted ledgerOperation balances
- define cut-off handling and carry-forward behavior
- model prefunding, collateral, or liquidity usage explicitly

When settlement is real-time gross:

- still separate instruction acceptance from settled cash confirmation unless the rail guarantees they are the same event

## Controls Checklist

- Can duplicate upstream or downstream events create duplicate postings?
- Can late rejects reverse a previously final posting?
- Are suspense and repair flows explicit?
- Can finance trace a GL movement back to payment events?
- Can operations explain a balance break with evidence?
- Are journals immutable with correcting entries instead of silent mutation?
