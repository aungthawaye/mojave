# TigerBeetle wallet engine

`mojave-core-wallet-engine-tigerbeetle` implements the existing `WalletEngine`
contract. It uses `com.tigerbeetle:tigerbeetle-java:0.17.9`, managed in
`modules/pom.xml`. The facade delegates to static task classes, matching the
MySQL module's organization.

## Usage

```java
import org.mojave.core.wallet.engine.tigerbeetle.TigerBeetleWalletEngine;

import java.math.BigInteger;
import java.util.List;

final var settings = new TigerBeetleWalletEngine.WalletClusterSettings(
    new BigInteger("123456"), List.of("127.0.0.1:3000"));

try (final var engine = new TigerBeetleWalletEngine(settings)) {
    // Use the existing WalletEngine operations.
}
```

Use one engine per cluster per application and close it during shutdown. For a
Spring bean, use `@Bean(destroyMethod = "close")`. The engine owns a thread-safe
native client. On Java 25, launch with `--enable-native-access=ALL-UNNAMED`.
Configure the actual cluster ID and all replica addresses; cluster ID zero is
reserved for tests. Application wiring still selects MySQL: adopting this module
requires changing the relevant application's dependency and bean construction.

## Monetary model

Each wallet has three monetary accounts and three corresponding contra accounts:

| Role | Meaning |
| --- | --- |
| 1 | Balance = posted credits minus posted debits; native overdraft protection |
| 2 | Position = posted debits minus posted credits; reserved = pending debits |
| 3 | NDC = posted credits minus posted debits |
| 4–6 | Contra accounts for roles 1–3 |
| 7–8 | Zero-amount journal accounts on a separate metadata ledger |

The monetary ledger is the three ASCII currency bytes interpreted as a positive
integer. All amounts use **four decimal places**, matching MySQL's
`decimal(34, 4)`, including wallets created with scale 0–4. The requested wallet
scale is retained as metadata. Conversion rejects excess fractional precision,
negative operation amounts, and values beyond the SQL decimal range. Positions
may be negative. IDs must be positive Java longs.

Deposits and reversals credit the balance account; withdrawals debit it.
Position increases debit the position account; decreases credit it. Reservations
are native pending debits with no expiry. Commitment posts the original pending
transfer; rollback voids it. NDC changes credit/debit the NDC account.

The tasks preserve the existing operation-specific rules:

- Withdrawals require a nonnegative resulting balance; they may reduce balance
  below the existing NDC, as in the MySQL procedure.
- Position increases and reservations require `position + reserved + amount <= ndc`.
- NDC increases require the new NDC to fit within the current balance.
- NDC decreases clamp at zero and require `position + reserved <= new ndc`.
- Fulfilment atomically commits the payer reservation and decreases the payee
  position, with matching currencies. Self-fulfilment is supported.

Zero amounts are supported. This implementation deliberately rejects negative
amounts and silent rounding even where the SQL procedures do not validate them.

## Atomicity, histories, and retries

Each operation is one **linked `createTransfers` chain** containing:

1. A unique wallet-version marker for each affected state transition.
2. An immutable event header and binary history chunks carried by zero-amount
   transfers on the metadata ledger.
3. Unique business keys for `(wallet, action, transaction)` and, for resolutions,
   the original withdrawal/reservation.
4. The native monetary transfer, or post/void of a pending transfer.

All records succeed or fail together. A writer reads the latest version, computes
and checks the next state, and submits a marker for that next version. If another
writer already consumed it, the whole linked chain fails and the writer retries
from current state. This protects cross-account NDC rules across independent
clients/processes without local locks. Fulfilment guards both wallets. Retries
are bounded at 128 attempts; exhaustion is an operational exception.

The journal captures before/after states, transaction IDs, exact caller timestamps,
descriptions, reference IDs, and fulfilment partners. Histories are reconstructed
from immutable events, never from a later balance read. Reversal timestamps are
generated on the first successful reversal. Tags are stored as immutable account
metadata chunks at wallet creation. No SQL database or in-memory recovery cache
is required.

Retry an uncertain operation using **the same update ID and payload**. An identical
retry returns its original history, including after reconnecting. Reusing an ID
with different inputs fails. A different ID for an already-used business key
fails, matching the SQL uniqueness constraints. Reservations and withdrawals can
be resolved only once. Duplicate wallet creation raises `WalletIdAlreadyTakenException`.
Operational/client failures propagate separately from domain rejections; an
interrupted caller retains its interrupt status and must reconcile/retry with the
same ID rather than assume failure.

Account and event IDs have deterministic domain-separated encodings. Control and
chunk IDs use SHA-256 truncated to 120 hash bits plus a namespace byte. A collision
fails closed through uniqueness/identity checks; it cannot authorize an extra
monetary movement. Action codes and binary schema version 1 are persistent format
contracts and must not be renumbered or repurposed.

## Operational boundaries

- This engine must exclusively own its account/transfer namespaces. Direct writes
  to its accounts would bypass wallet-version guards and invalidate the journal.
- Use a dedicated cluster or reserve these namespaces and the metadata ledger
  (`0x4D574500`) when sharing a cluster.
- The full contract requires durable strings and exact historical snapshots.
  Storing those in linked metadata transfers costs roughly tens of transfers per
  operation, more for long Unicode descriptions and fulfilments. Benchmark the
  actual workload before production rollout; this is not a minimal-transfer
  TigerBeetle design.
- Account cumulative debit/credit counters remain subject to TigerBeetle's uint128
  bounds, independently of each wallet's decimal balance bound.
- The synchronous native client can wait during loss of quorum. Application
  cancellation/availability policy should account for an uncertain commit.
- There is no migration of existing MySQL balances or application selection switch
  in this module. Accounts start at zero through `createWallet`.

## Tests

Unit tests run without a server:

```sh
mvn -f modules/pom.xml -pl :mojave-core-wallet-engine-tigerbeetle -am test
```

Integration tests use an explicitly supplied development cluster. For example,
using the TigerBeetle 0.17.9 binary in a disposable directory:

```sh
tigerbeetle format --cluster=0 --replica=0 --replica-count=1 --development 0_0.tigerbeetle
tigerbeetle start --addresses=127.0.0.1:33091 --development 0_0.tigerbeetle
```

In another terminal, from the repository root:

```sh
mvn -f modules/pom.xml -pl :mojave-core-wallet-engine-tigerbeetle -am verify \
  -Dtigerbeetle.addresses=127.0.0.1:33091 -Dtigerbeetle.cluster-id=0
```

Without `tigerbeetle.addresses`, integration tests are explicitly skipped. Tests
create uniquely named wallets and immutable transfers; use a disposable cluster,
not production. Coverage includes direct native-account balance checks, history
replay through a new client, concurrency between independent clients, financial
limits, fulfilment, resolution uniqueness, precision, Unicode, and zero amounts.
