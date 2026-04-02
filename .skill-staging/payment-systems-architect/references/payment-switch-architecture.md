# Payment Switch Architecture Reference

## Use This Reference

Read this file when designing or reviewing routing, orchestration, scheme connectivity, reliability, controls, and operating models for a payment switch or hub.

## What A Payment Switch Should Do

A payment switch coordinates payment processing across channels, rails, and downstream services. It commonly:

- receive instructions from channels or upstream systems
- validate structure and eligibility
- perform screening, risk, limits, and routing
- orchestrate message exchange with external schemes or internal rails
- manage business state transitions
- hand off booking events to a ledger or accounting service
- surface operational visibility and repair controls

Avoid turning the switch into the owner of every concern. Keep customer profile, pricing, GL, sanctions list management, and settlement statement processing in bounded services unless there is a strong reason to centralize them.

## Logical Components

- Channel adapters or API ingress
- Canonical validation and enrichment
- Routing and orchestration engine
- Sanctions, fraud, and limits gateways
- Scheme adapters
- State store and idempotency service
- Event publication or workflow outbox
- Ledger or posting integration
- Reconciliation and settlement integrations
- Operator tooling and repair workflows

## State And Idempotency

- Use stable business keys for duplicate detection.
- Separate inbound request idempotency from downstream execution idempotency.
- Record whether an action is prepared, submitted, acknowledged, accepted, settled, rejected, or repaired.
- Make replay safe by ensuring downstream effects are either deduplicated or compensatable.

## Routing Guidance

- Route using explicit rules: payment type, currency, amount bands, customer segment, operating window, liquidity availability, and scheme reachability.
- Prefer deterministic routing decisions that are auditable.
- Record the routing reason and version of the rule set.
- Design controlled override paths for operations teams during incidents.

## Failure Handling

Design explicit responses for:

- duplicate inbound instruction
- timeout before downstream receipt
- timeout after downstream receipt but before response
- downstream accept followed by late reject
- ledger unavailable after external acceptance
- scheme unavailable before cutoff
- repeated retry causing message storms
- replay after crash recovery

For each scenario, specify customer-visible status, operator action, retry policy, and accounting effect.

## Resilience And Operations

- Define active-active versus active-passive behavior intentionally.
- Protect ordering where the rail or business process requires it.
- Expose queue depth, message age, repair backlog, and in-flight value metrics.
- Provide manual repair operations with tight guardrails and full audit trails.
- Keep immutable event history even when current state is mutable.

## Settlement And Reconciliation

Switch activity is not enough on its own. Confirm consistency across:

- switch business events
- outbound and inbound scheme messages
- settlement reports or statements
- ledger journals and account balances
- liquidity or prefunding positions

Design daily and intraday controls for breaks, missing acknowledgements, unbooked settlements, duplicate postings, and stale suspense balances.

## Review Checklist

- Does the switch own only the responsibilities it should own?
- Are business state and transport state distinct?
- Is idempotency end-to-end instead of only at the API edge?
- Can operators repair without bypassing controls?
- Is every external submission traceable to an internal event and booking decision?
- Are reconciliation and settlement flows designed as first-class processes?
