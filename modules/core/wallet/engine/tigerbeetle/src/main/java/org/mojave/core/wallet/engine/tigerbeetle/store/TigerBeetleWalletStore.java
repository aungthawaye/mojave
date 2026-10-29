package org.mojave.core.wallet.engine.tigerbeetle.store;

import com.tigerbeetle.AccountBatch;
import com.tigerbeetle.AccountFlags;
import com.tigerbeetle.Client;
import com.tigerbeetle.CreateAccountStatus;
import com.tigerbeetle.CreateTransferStatus;
import com.tigerbeetle.IdBatch;
import com.tigerbeetle.QueryFilter;
import com.tigerbeetle.TransferBatch;
import com.tigerbeetle.TransferFlags;
import org.mojave.scheme.rule.enums.Currency;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Stores money and its immutable journal in one TigerBeetle linked transfer chain.
 * Wallet version markers serialize all writers, including writers in other processes.
 */
public final class TigerBeetleWalletStore implements AutoCloseable {

    public static final int BALANCE = 1;

    public static final int POSITION = 2;

    public static final int NDC = 3;

    private static final int METADATA_DEBIT = 7;

    private static final int METADATA_CREDIT = 8;

    private static final int METADATA_LEDGER = 0x4D574500;

    private static final int GUARD = 60001;

    private static final int HEADER = 60002;

    private static final int CHUNK = 60003;

    private static final int UNIQUE = 60004;

    private static final int CHUNK_BYTES = 16;

    private static final int MAX_EVENT_BYTES = 4096;

    private final Client client;

    public TigerBeetleWalletStore(final Client client) {

        this.client = Objects.requireNonNull(client);
    }

    public boolean createWallet(final long wallet, final Currency currency, final int scale,
                                final String tag) {

        Objects.requireNonNull(currency, "currency");
        if (wallet <= 0 || scale < 0 || scale > WalletRecord.SCALE) {
            throw new IllegalArgumentException("Positive wallet ID and scale from 0 to 4 required");
        }
        if (tag != null && tag.codePointCount(0, tag.length()) > 64) {
            throw new IllegalArgumentException("Tag exceeds 64 characters");
        }
        final var tagBytes = tag == null ? new byte[0] : tag.getBytes(StandardCharsets.UTF_8);
        final var tagChunks = (tagBytes.length + CHUNK_BYTES - 1) / CHUNK_BYTES;
        final var accounts = new AccountBatch(8 + tagChunks);
        for (var role = 1; role <= 8; role++) {
            accounts.add();
            accounts.setId(WalletCodec.accountId(wallet, role));
            accounts.setLedger(role < METADATA_DEBIT ? ledger(currency) : METADATA_LEDGER);
            accounts.setCode(role);
            accounts.setUserData64(wallet);
            accounts.setUserData32(role == METADATA_CREDIT ? tagBytes.length : scale);
            accounts.setUserData128(Arrays.copyOf(currency.name().getBytes(StandardCharsets.US_ASCII), 16));
            accounts.setFlags(AccountFlags.LINKED |
                (role == BALANCE ? AccountFlags.DEBITS_MUST_NOT_EXCEED_CREDITS : 0));
        }
        for (var index = 0; index < tagChunks; index++) {
            accounts.add();
            accounts.setId(WalletCodec.accountId(wallet, 100 + index));
            accounts.setLedger(METADATA_LEDGER);
            accounts.setCode(100);
            accounts.setUserData64(wallet);
            accounts.setUserData32(index);
            accounts.setUserData128(Arrays.copyOfRange(tagBytes, index * 16, index * 16 + 16));
            accounts.setFlags(AccountFlags.LINKED);
        }
        accounts.setFlags(accounts.getFlags() & ~AccountFlags.LINKED);

        final var results = call(() -> client.createAccounts(accounts));
        var duplicate = false;
        while (results.next()) {
            final var status = results.getStatus();
            if (status.name().startsWith("Exists")) {
                duplicate = true;
            } else if (status != CreateAccountStatus.Created &&
                       status != CreateAccountStatus.LinkedEventFailed) {
                throw new IllegalStateException("TigerBeetle account creation failed: " + status);
            }
        }
        if (!duplicate && results.getLength() != accounts.getLength()) {
            throw new IllegalStateException("Incomplete TigerBeetle account creation response");
        }
        return !duplicate;
    }

    public WalletRecord.State state(final long wallet) {

        final var accounts = call(() -> client.lookupAccounts(new IdBatch(WalletCodec.accountId(wallet, BALANCE))));
        if (!accounts.next()) {
            return null;
        }
        final var currency = Currency.valueOf(new String(accounts.getUserData128(), 0, 3, StandardCharsets.US_ASCII));
        final var scale = accounts.getUserData32();
        final var query = new QueryFilter();
        query.setLedger(METADATA_LEDGER);
        query.setCode(GUARD);
        query.setUserData64(wallet);
        query.setReversed(true);
        query.setLimit(1);

        final var latest = call(() -> client.queryTransfers(query));
        if (!latest.next()) {
            return new WalletRecord.State(wallet, currency, scale, 0, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        final var event = event(latest.getUserData128());
        if (event == null || event.after().wallet() != wallet) {
            throw new IllegalStateException("Wallet version references a missing or unrelated event");
        }
        return event.after();
    }

    public WalletRecord.Event event(final int group, final long id) {

        return event(WalletCodec.eventId(group, id));
    }

    private WalletRecord.Event event(final byte[] id) {

        final var header = call(() -> client.lookupTransfers(new IdBatch(id)));
        if (!header.next()) {
            return null;
        }
        final var length = header.getUserData32();
        if (header.getCode() != HEADER || length <= 0 || length > MAX_EVENT_BYTES) {
            throw new IllegalStateException("Invalid wallet event header");
        }
        final var ids = new IdBatch((length + CHUNK_BYTES - 1) / CHUNK_BYTES);
        for (var index = 0; index < ids.getCapacity(); index++) {
            ids.add(chunkId(id, index));
        }
        final var chunks = call(() -> client.lookupTransfers(ids));
        if (chunks.getLength() != ids.getLength()) {
            throw new IllegalStateException("Incomplete wallet journal");
        }
        final var bytes = new byte[length];
        final var seen = new boolean[ids.getLength()];
        while (chunks.next()) {
            final var index = chunks.getUserData32();
            if (index < 0 || index >= seen.length || seen[index] || chunks.getCode() != CHUNK) {
                throw new IllegalStateException("Invalid wallet journal chunk");
            }
            seen[index] = true;
            System.arraycopy(chunks.getUserData128(), 0, bytes, index * CHUNK_BYTES,
                Math.min(CHUNK_BYTES, length - index * CHUNK_BYTES));
        }
        final var event = WalletCodec.decode(bytes);
        if (!Arrays.equals(id, WalletCodec.eventId(event.command().action().group(), event.command().id()))) {
            throw new IllegalStateException("Wallet journal identity mismatch");
        }
        return event;
    }

    public boolean exists(final byte[] id) {

        return call(() -> client.lookupTransfers(new IdBatch(id))).getLength() != 0;
    }

    public static byte[] resolutionId(final int group, final long reference) {

        return WalletCodec.derivedId("resolution", group, reference);
    }

    /** Returns false only when a competing transaction consumed a version or unique key. */
    public boolean append(final List<WalletRecord.Event> events) {

        final var batch = new TransferBatch(512);
        for (final var event : events) {
            final var command = event.command();
            final var id = WalletCodec.eventId(command.action().group(), command.id());
            metadata(batch, WalletCodec.derivedId("version", command.wallet(), event.after().version()),
                command.wallet(), GUARD, id, 0);
        }
        for (final var event : events) {
            appendEvent(batch, event);
            appendMovement(batch, event);
        }
        batch.setFlags(batch.getFlags() & ~TransferFlags.LINKED);

        final var results = call(() -> client.createTransfers(batch));
        var conflict = false;
        var failed = false;
        while (results.next()) {
            final var status = results.getStatus();
            if (status.name().startsWith("Exists")) {
                conflict = true;
            } else if (status == CreateTransferStatus.LinkedEventFailed) {
                failed = true;
            } else if (status != CreateTransferStatus.Created) {
                throw new IllegalStateException("TigerBeetle transfer rejected: " + status);
            }
        }
        if (conflict) {
            return false;
        }
        if (failed || results.getLength() != batch.getLength()) {
            throw new IllegalStateException("Incomplete TigerBeetle linked transfer result");
        }
        return true;
    }

    public static byte[] businessId(final WalletRecord.Command command) {

        return WalletCodec.derivedId("business", command.wallet(), command.action().code(),
            command.transaction());
    }

    private static void appendEvent(final TransferBatch batch, final WalletRecord.Event event) {

        final var command = event.command();
        final var id = WalletCodec.eventId(command.action().group(), command.id());
        final var bytes = WalletCodec.encode(event);
        if (bytes.length > MAX_EVENT_BYTES) {
            throw new IllegalArgumentException("Wallet event exceeds storage limit");
        }
        metadata(batch, id, command.wallet(), HEADER, new byte[16], bytes.length);
        for (var index = 0; index * CHUNK_BYTES < bytes.length; index++) {
            metadata(batch, chunkId(id, index), command.wallet(), CHUNK,
                Arrays.copyOfRange(bytes, index * CHUNK_BYTES, index * CHUNK_BYTES + CHUNK_BYTES), index);
        }
        metadata(batch, businessId(command), command.wallet(), UNIQUE, id, 0);
        if (command.reference() != 0) {
            metadata(batch, resolutionId(command.action().group(), command.reference()),
                command.wallet(), UNIQUE, id, 0);
        }
    }

    private static byte[] chunkId(final byte[] eventId, final int index) {

        final var buffer = ByteBuffer.wrap(eventId);
        return WalletCodec.derivedId("chunk", buffer.getLong(), buffer.getLong(), index);
    }

    private static void metadata(final TransferBatch batch, final byte[] id, final long wallet,
                                 final int code, final byte[] data, final int number) {

        batch.add();
        batch.setId(id);
        batch.setDebitAccountId(WalletCodec.accountId(wallet, METADATA_DEBIT));
        batch.setCreditAccountId(WalletCodec.accountId(wallet, METADATA_CREDIT));
        batch.setLedger(METADATA_LEDGER);
        batch.setCode(code);
        batch.setAmount(0);
        batch.setUserData128(data);
        batch.setUserData64(wallet);
        batch.setUserData32(number);
        batch.setFlags(TransferFlags.LINKED);
    }

    private static void appendMovement(final TransferBatch batch, final WalletRecord.Event event) {

        final var command = event.command();
        final var role = command.action().group();
        final var units = command.action() == WalletRecord.Action.DECREASE_NDC ?
            event.before().ndc().subtract(event.after().ndc()).unscaledValue() :
            command.amount().unscaledValue();
        final var outgoing = switch (command.action()) {
            case WITHDRAW, INCREASE_POSITION, RESERVE, DECREASE_NDC -> true;
            default -> false;
        };
        final var resolving = command.action() == WalletRecord.Action.COMMIT ||
            command.action() == WalletRecord.Action.ROLLBACK;

        batch.add();
        batch.setId(movementId(role, command.id()));
        batch.setAmount(units);
        batch.setFlags(TransferFlags.LINKED);
        batch.setUserData64(command.transaction());
        if (resolving) {
            batch.setPendingId(movementId(POSITION, command.reference()));
            batch.setFlags(batch.getFlags() | (command.action() == WalletRecord.Action.COMMIT ?
                TransferFlags.POST_PENDING_TRANSFER : TransferFlags.VOID_PENDING_TRANSFER));
        } else {
            batch.setDebitAccountId(WalletCodec.accountId(command.wallet(), outgoing ? role : role + 3));
            batch.setCreditAccountId(WalletCodec.accountId(command.wallet(), outgoing ? role + 3 : role));
            batch.setLedger(ledger(event.before().currency()));
            batch.setCode(role);
            if (command.action() == WalletRecord.Action.RESERVE) {
                batch.setFlags(batch.getFlags() | TransferFlags.PENDING);
            }
        }
    }

    public static byte[] movementId(final int group, final long id) {

        return WalletCodec.derivedId("movement", group, id);
    }

    private static int ledger(final Currency currency) {

        final var code = currency.name();
        return (code.charAt(0) << 16) | (code.charAt(1) << 8) | code.charAt(2);
    }

    private static <T> T call(final Request<T> request) {

        try {
            return request.execute();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("TigerBeetle request interrupted; retry with the same update ID", e);
        }
    }

    @Override
    public void close() {

        client.close();
    }

    @FunctionalInterface
    private interface Request<T> {

        T execute() throws InterruptedException;
    }
}
