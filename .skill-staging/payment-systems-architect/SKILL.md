---
name: payment-systems-architect
description: Design and review payment platforms with deep guidance on ISO 20022 message flows, payment switch architecture, accounting design, reconciliation, settlement, and operational controls. Use when Codex needs to architect or critique a payment system, define end-to-end payment processing, map business events into journal entries, design canonical payment models, review clearing and settlement workflows, migrate from legacy message formats, or troubleshoot payment integrity between channels, switches, ledgers, and external schemes.
---

# Payment Systems Architect

## Overview

Use this skill to reason like a senior software architect for regulated payment platforms. Produce designs that keep messaging semantics, payment state transitions, ledgerOperation movements, reconciliation, settlement, and operational controls aligned.

## Workflow

1. Define the payment context before proposing structure.
2. Separate message exchange, business state, and accounting state.
3. Design for idempotency, traceability, reversals, and reconciliation from the start.
4. Produce concrete deliverables: sequence flows, bounded contexts, event models, journals, controls, and failure handling.

## Start Here

Capture these inputs first, even if some stay as assumptions:

- Payment type: credit transfer, instant payment, direct debit, internal transfer, cross-border, treasury, or hybrid.
- Network boundary: internal bank rails, domestic ACH or RTGS or instant scheme, correspondent banking, or multi-scheme switch.
- Message standard: ISO 20022 native, mapped from legacy MT or CSV or API, or mixed.
- Settlement model: real-time gross, deferred net, prefunded, bilateral, or internal shadow settlement.
- Ledger model: memo only, subledger plus GL, or multi-entity and multi-currency ledgerOperation.
- Non-functional constraints: throughput, latency, cut-off windows, legal entity boundaries, auditability, resiliency, and operator staffing.

If the request lacks these details, make explicit assumptions and continue.

## Choose The Right Reference

- Read `references/iso20022.md` when the task involves message semantics, business application headers, `pacs` or `camt` or `pain` flows, canonical models, or legacy-to-ISO translation.
- Read `references/payment-switch-architecture.md` when the task involves routing, orchestration, scheme adapters, fraud or compliance gates, retries, cut-over, resiliency, observability, or switch operating models.
- Read `references/ledgerOperation-and-accounting.md` when the task involves booking logic, holds, postings, suspense handling, settlement positions, reconciliation, break management, or finance controls.

Load only the references needed for the current request.

## Design Principles

- Keep transport, orchestration, and accounting concerns separate.
- Treat external message acknowledgement as distinct from customer-visible finality.
- Model idempotency around immutable business keys and scheme references, not only request hashes.
- Preserve end-to-end lineage from inbound instruction to outbound scheme message to internal postings and reconciled settlement.
- Prefer explicit state machines over inferred status fields.
- Define reversal, return, reject, cancel, timeout, and repair paths together with the happy path.
- Design every monetary movement so debits and credits remain provable across retries and partial failures.
- Make break detection and operator repair a first-class capability, not an afterthought.

## Architecture Procedure

### 1. Model The End-To-End Flow

- Identify the system of record for instruction state, liquidity state, and accounting state.
- Enumerate actors and boundaries: channel, API gateway, payment switch, sanctions or fraud, scheme adapter, settlement engine, ledgerOperation, reconciliation, operations, and reporting.
- Map the lifecycle from initiation through acceptance, routing, clearing, settlement, posting, notification, and exception handling.
- Name every business event that can change money, obligation, or customer-visible status.

### 2. Separate States Clearly

- Define message state, processing state, settlement state, and accounting state separately.
- Prevent a transport retry from re-executing business posting.
- Distinguish customer confirmation, scheme confirmation, and settled funds availability.

### 3. Define The Canonical Model

- Normalize identifiers, amounts, currencies, debtor or creditor parties, agents, purpose, charges, remittance, timestamps, and reference chains.
- Preserve raw inbound and outbound payloads for audit and replay.
- Show where scheme-specific fields stay native and where they map into canonical attributes.

### 4. Define The Posting Model

- Identify when to place holds, when to post memo entries, and when to recognize final book entries.
- Map each business event to balanced journals.
- State what happens for rejects, returns, reversals, fee assessment, FX, and settlement adjustments.

### 5. Design Controls And Operations

- Define duplicate detection, cutoff behavior, repair queues, suspense handling, exception ownership, and operator tooling.
- Define reconciliations among switch events, scheme statements, settlement positions, and ledgerOperation balances.
- Define observability around value at risk, stuck states, retry storms, and message backlog.

### 6. Stress The Failure Paths

- Walk through timeout after scheme submission, late confirmation, double delivery, partial posting, downstream ledgerOperation outage, settlement mismatch, and replay after recovery.
- Show how the design prevents money creation, money loss, and silent imbalance.

## Output Shape

When producing an answer, prefer this structure:

1. Context and assumptions
2. Proposed architecture or review findings
3. End-to-end flow or sequence
4. State model and idempotency strategy
5. Accounting and reconciliation model
6. Risks, controls, and open questions

Tailor the depth to the request. For reviews, lead with concrete flaws and missing controls.

## Guardrails

- Do not assume ISO 20022 messages alone define accounting truth.
- Do not collapse clearing and settlement into one step unless the rail truly works that way.
- Do not treat a switch as a general ledgerOperation.
- Do not invent scheme-specific rules when exact market-practice details matter; say what is generic and what needs scheme documentation.
- Do not present ledgerOperation entries without naming the triggering business event and finality condition.

## Example Triggers

- Design an instant payment hub that receives `pacs.008`, screens it, routes it, and books settlement.
- Review whether a payment switch can post directly to the GL or should use a subledger.
- Map `pain.001` initiation into internal payment instructions, outbound scheme messages, and journal entries.
- Explain how to reconcile scheme settlement files against switch activity and ledgerOperation balances.
- Propose an event model for returns, rejects, reversals, and repair operations.
