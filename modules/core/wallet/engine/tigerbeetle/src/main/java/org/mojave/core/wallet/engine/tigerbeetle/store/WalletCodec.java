package org.mojave.core.wallet.engine.tigerbeetle.store;

import com.tigerbeetle.UInt128;
import org.mojave.scheme.rule.enums.Currency;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;

public final class WalletCodec {

    private static final int SCHEMA = 1;

    private WalletCodec() { }

    public static byte[] accountId(final long wallet, final int role) {

        return UInt128.asBytes(wallet, 0x4D57410000000000L | role);
    }

    public static byte[] eventId(final int group, final long id) {

        return UInt128.asBytes(id, 0x4D57450000000000L | group);
    }

    public static byte[] derivedId(final String purpose, final long... values) {

        try {
            final var digest = MessageDigest.getInstance("SHA-256");
            digest.update(("mojave-wallet-v1/" + purpose).getBytes(StandardCharsets.UTF_8));
            for (final var value : values) {
                digest.update(UInt128.asBytes(value));
            }
            final var id = Arrays.copyOf(digest.digest(), 16);
            // Separate hashed control IDs from the reversible event ID namespace.
            id[15] = (byte) 0xA7;
            return id;
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    public static byte[] encode(final WalletRecord.Event event) {

        try {
            final var bytes = new ByteArrayOutputStream();
            final var out = new DataOutputStream(bytes);
            final var command = event.command();
            out.writeInt(SCHEMA);
            out.writeUTF(command.action().name());
            out.writeLong(command.id());
            out.writeLong(command.wallet());
            out.writeLong(command.transaction());
            out.writeLong(command.at().getEpochSecond());
            out.writeInt(command.at().getNano());
            out.writeUTF(command.amount().toPlainString());
            out.writeLong(command.reference());
            out.writeBoolean(command.description() != null);
            if (command.description() != null) {
                out.writeUTF(command.description());
            }
            out.writeLong(command.peer());
            out.writeLong(command.peerUpdate());
            writeState(out, event.before());
            writeState(out, event.after());
            out.flush();
            return bytes.toByteArray();
        } catch (final IOException e) {
            throw new IllegalStateException("Cannot encode wallet event", e);
        }
    }

    public static WalletRecord.Event decode(final byte[] bytes) {

        try {
            final var in = new DataInputStream(new ByteArrayInputStream(bytes));
            if (in.readInt() != SCHEMA) {
                throw new IllegalStateException("Unsupported wallet event schema");
            }
            final var command = new WalletRecord.Command(
                WalletRecord.Action.valueOf(in.readUTF()), in.readLong(), in.readLong(),
                in.readLong(), Instant.ofEpochSecond(in.readLong(), in.readInt()),
                new BigDecimal(in.readUTF()), in.readLong(),
                in.readBoolean() ? in.readUTF() : null, in.readLong(), in.readLong());
            final var event = new WalletRecord.Event(command, readState(in), readState(in));
            if (in.available() != 0) {
                throw new IllegalStateException("Unexpected wallet event trailing bytes");
            }
            return event;
        } catch (final IOException e) {
            throw new IllegalStateException("Corrupt wallet event", e);
        }
    }

    private static void writeState(final DataOutputStream out, final WalletRecord.State state)
        throws IOException {

        out.writeLong(state.wallet());
        out.writeUTF(state.currency().name());
        out.writeInt(state.scale());
        out.writeLong(state.version());
        out.writeUTF(state.balance().toPlainString());
        out.writeUTF(state.position().toPlainString());
        out.writeUTF(state.reserved().toPlainString());
        out.writeUTF(state.ndc().toPlainString());
    }

    private static WalletRecord.State readState(final DataInputStream in) throws IOException {

        return new WalletRecord.State(in.readLong(), Currency.valueOf(in.readUTF()), in.readInt(),
            in.readLong(), new BigDecimal(in.readUTF()), new BigDecimal(in.readUTF()),
            new BigDecimal(in.readUTF()), new BigDecimal(in.readUTF()));
    }
}
