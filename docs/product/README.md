# Mojave Product Documentation

Business-focused documentation explaining the concepts, features, and capabilities of the Mojave instant payment switch.

## Table of Contents

### 1. Overview

Introduction to instant payment systems and Mojave's implementation.

- [Instant Payment Concept](01-overview/instant-payment-concept.md) - Understanding interoperable instant payment systems (IIPS)
- [Mojave Introduction](01-overview/mojave-introduction.md) - Project overview, goals, and capabilities
- [Architecture Overview](01-overview/architecture-overview.md) - High-level system components and how they work together

### 2. Core Concepts

Fundamental concepts that underpin the Mojave payment system.

- [Participants and Roles](02-core-concepts/participants-and-roles.md) - FSPs, Hub, Oracles, and Settlement Service Providers
- [Accounting Model](02-core-concepts/accounting-model.md) - Double-entry bookkeeping and chart of accounts
- [Wallet and Positions](02-core-concepts/wallet-and-positions.md) - Position management and Net Debit Cap
- [Settlement Framework](02-core-concepts/settlement-framework.md) - Settlement definitions, filtering, and lifecycle
- [Transaction Lifecycle](02-core-concepts/transaction-lifecycle.md) - Transaction phases and state management

### 3. Features by Module

Detailed capabilities organized by functional module.

- [Participant Management](03-features/participant-management.md) - FSP onboarding, currency support, endpoint management
- [Accounting Capabilities](03-features/accounting-capabilities.md) - Ledger operations, flow definitions, balance management
- [Wallet Management](03-features/wallet-management.md) - Position tracking, NDC enforcement, reservations
- [Settlement Processing](03-features/settlement-processing.md) - Settlement rules, provider matching, batch processing
- [Transaction Tracking](03-features/transaction-tracking.md) - Audit trails, step recording, query capabilities

### 4. Payment Specifications

Protocol and message format specifications.

- [FSPIOP v2.0 Implementation](04-payment-specifications/fspiop-v2-implementation.md) - Complete FSPIOP protocol implementation
- [ISO 20022 Roadmap](04-payment-specifications/iso20022-roadmap.md) - Planned ISO 20022 support

## How Modules Work Together

Mojave's modules are designed to work in harmony to process instant payments:

```
Payment Request Flow:
  1. Participant Module identifies and routes to FSPs
  2. Transaction Module creates audit trail
  3. Wallet Module reserves positions (NDC check)
  4. Accounting Module posts double-entry ledger
  5. Settlement Module matches settlement provider
  6. Transaction Module closes with final state
```

## Key Capabilities

### Real-Time Payment Processing
- Instant transfer execution
- Synchronous position validation
- Immediate finality

### Multi-Currency Support
- Per-FSP currency configuration
- Currency-specific flow definitions
- Multi-currency wallets and positions

### Liquidity Management
- Net Debit Cap (NDC) enforcement
- Real-time position tracking
- Reservation and commitment lifecycle

### Settlement Flexibility
- Configurable settlement definitions
- Multi-tier FSP filtering
- Support for DFN, CGS, and RTGS settlement types

### Complete Audit Trail
- Transaction step recording
- Ledger movement history
- Position update tracking

## Getting Started

### For Business Analysts
1. Start with [Instant Payment Concept](01-overview/instant-payment-concept.md)
2. Review [Mojave Introduction](01-overview/mojave-introduction.md)
3. Understand [Participants and Roles](02-core-concepts/participants-and-roles.md)

### For Product Managers
1. Review [Architecture Overview](01-overview/architecture-overview.md)
2. Examine each feature module in Section 3
3. Understand [FSPIOP v2.0 Implementation](04-payment-specifications/fspiop-v2-implementation.md)

### For Compliance Officers
1. Study [Accounting Model](02-core-concepts/accounting-model.md)
2. Review [Transaction Lifecycle](02-core-concepts/transaction-lifecycle.md)
3. Examine [Transaction Tracking](03-features/transaction-tracking.md)

## Related Documentation

- [Technical Documentation](../technical/README.md) - Implementation details for developers
- [Main Documentation Index](../README.md) - Overall documentation navigation

## Document Conventions

### Terminology
- **FSP**: Financial Service Provider (banks, mobile money operators, etc.)
- **Hub**: Central payment switch connecting FSPs
- **Oracle**: Party lookup service for identifier resolution
- **SSP**: Settlement Service Provider handling settlement processing
- **NDC**: Net Debit Cap - maximum allowed negative position
- **FSPIOP**: Financial Services Provider Interoperability Protocol

### Diagrams
Diagrams use Mermaid syntax for clarity and version control.

### Examples
Examples use realistic scenarios to illustrate concepts.
