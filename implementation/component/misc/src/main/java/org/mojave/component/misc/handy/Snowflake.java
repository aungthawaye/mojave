/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package org.mojave.component.misc.handy;

import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.CRC32;

public final class Snowflake {

    // Layout: [timestamp | nodeId | sequence]
    private static final int TIMESTAMP_BITS = 42;

    private static final int NODE_ID_BITS = 10;

    private static final int SEQUENCE_BITS = 12;

    private static final int MAX_NODE_ID = (1 << NODE_ID_BITS) - 1;      // 1023

    private static final Snowflake INSTANCE = new Snowflake(resolveNodeId());

    private static final int MAX_SEQUENCE = (1 << SEQUENCE_BITS) - 1;    // 4095

    private static final int NODE_ID_SHIFT = SEQUENCE_BITS;

    private static final int TIMESTAMP_SHIFT = NODE_ID_BITS + SEQUENCE_BITS;

    private static final long CUSTOM_EPOCH_MILLIS = 1577813400000L;

    private static final long MAX_TIMESTAMP = (1L << TIMESTAMP_BITS) - 1;

    // state encodes: (timestamp << SEQUENCE_BITS) | sequence
    // initial -1 means "uninitialized"
    private final AtomicLong state = new AtomicLong(-1L);

    private final int nodeId;

    public Snowflake() {

        this(resolveNodeId());
    }

    public Snowflake(int nodeId) {

        if (nodeId < 0 || nodeId > MAX_NODE_ID) {
            throw new IllegalArgumentException(
                "nodeId must be between 0 and " + MAX_NODE_ID + " (inclusive)");
        }
        this.nodeId = nodeId;
    }

    private static int createNodeIdFromMac() {

        try {

            CRC32 crc = new CRC32();
            boolean found = false;

            var networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {

                NetworkInterface ni = networkInterfaces.nextElement();

                if (ni == null || !ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }

                byte[] mac = ni.getHardwareAddress();

                if (mac != null && mac.length > 0) {
                    crc.update(mac);
                    found = true;
                }
            }

            int base = found ? (int) crc.getValue() : ThreadLocalRandom.current().nextInt();

            return base & MAX_NODE_ID;

        } catch (Exception e) {

            return ThreadLocalRandom.current().nextInt() & MAX_NODE_ID;
        }
    }

    public static Snowflake get() {

        return INSTANCE;
    }

    /**
     * Prefer explicit node id in distributed systems.
     * -Dsnowflake.nodeId=123 OR SNOWFLAKE_NODE_ID=123
     */
    private static int resolveNodeId() {

        String s = System.getProperty("snowflake.nodeId");

        if (s == null || s.isBlank()) {
            s = System.getenv("SNOWFLAKE_NODE_ID");
        }

        if (s == null || s.isBlank()) {
            return createNodeIdFromMac(); // last resort
        }

        // If it's a plain int, use it
        try {
            return Integer.parseInt(s.trim()) & MAX_NODE_ID;
        } catch (NumberFormatException ignored) { }

        // Otherwise extract trailing "-<number>" (StatefulSet pod name)
        int dash = s.lastIndexOf('-');

        if (dash >= 0 && dash + 1 < s.length()) {

            try {

                int ordinal = Integer.parseInt(s.substring(dash + 1));
                return ordinal & MAX_NODE_ID;

            } catch (NumberFormatException ignored) { }
        }

        // fallback
        return createNodeIdFromMac();
    }

    private static long timestamp() {

        return System.currentTimeMillis() - CUSTOM_EPOCH_MILLIS;
    }

    private static long waitNextMillis(long lastTimestamp) {

        long ts;

        do {

            ts = timestamp();
            // polite spinning when throughput is extremely high
            Thread.onSpinWait();

        } while (ts <= lastTimestamp);

        return ts;
    }

    public long nextId() {

        for (; ; ) {

            final long now = timestamp();

            if (now < 0) {
                throw new IllegalStateException("Epoch is in the future (system clock incorrect).");
            }

            if (now > MAX_TIMESTAMP) {
                throw new IllegalStateException(
                    "Timestamp overflow: exceeded " + TIMESTAMP_BITS + " bits.");
            }

            final long prev = state.get();

            final long lastTs;
            final int lastSeq;

            if (prev < 0) {

                lastTs = -1L;
                lastSeq = 0;

            } else {

                lastTs = prev >>> SEQUENCE_BITS;
                lastSeq = (int) (prev & MAX_SEQUENCE);
            }

            // If your infra can move time backwards (NTP / VM), waiting is usually better than crashing.
            // If you prefer the old behavior, replace this block with: if (now < lastTs) throw ...
            if (now < lastTs) {
                final long waited = waitNextMillis(lastTs);
                return composeAndSet(prev, waited, 0);
            }

            final long ts;
            final int seq;

            if (now == lastTs) {

                int nextSeq = (lastSeq + 1) & MAX_SEQUENCE;

                if (nextSeq == 0) {

                    ts = waitNextMillis(lastTs);
                    seq = 0;

                } else {

                    ts = now;
                    seq = nextSeq;
                }
            } else {

                ts = now;
                seq = 0;
            }

            final long nextState = (ts << SEQUENCE_BITS) | (seq & MAX_SEQUENCE);

            if (state.compareAndSet(prev, nextState)) {

                return (ts << TIMESTAMP_SHIFT) | ((long) nodeId << NODE_ID_SHIFT) |
                           (seq & MAX_SEQUENCE);
            }
        }
    }

    private long composeAndSet(long prev, long ts, int seq) {

        final long nextState = (ts << SEQUENCE_BITS) | (seq & MAX_SEQUENCE);
        // best effort: if CAS fails, just retry via public loop

        if (state.compareAndSet(prev, nextState)) {

            return (ts << TIMESTAMP_SHIFT) | ((long) nodeId << NODE_ID_SHIFT) |
                       (seq & MAX_SEQUENCE);
        }

        return nextId();
    }

}

