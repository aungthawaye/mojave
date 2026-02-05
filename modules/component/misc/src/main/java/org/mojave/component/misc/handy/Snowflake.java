/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.component.misc.handy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.LockSupport;
import java.util.zip.CRC32;

public final class Snowflake {

    private static final Logger LOGGER = LoggerFactory.getLogger(Snowflake.class);

    private static final int EPOCH_BITS = 42;

    private static final int NODE_ID_BITS = 10;

    private static final int SEQUENCE_BITS = 12;

    private static final int MAX_NODE_ID = (1 << NODE_ID_BITS) - 1;      // 1023

    private static final Snowflake INSTANCE = new Snowflake(resolveNodeId());

    private static final int MAX_SEQUENCE = (1 << SEQUENCE_BITS) - 1;    // 4095

    private static final int NODE_ID_SHIFT = SEQUENCE_BITS;

    private static final int TIMESTAMP_SHIFT = NODE_ID_BITS + SEQUENCE_BITS;

    private static final long BIG_BANG = 1577813400000L;

    private final int nodeId;

    private long lastTimestamp = -1L;

    private int sequence = 0;

    private Snowflake(int nodeId) {

        this.nodeId = nodeId & MAX_NODE_ID;
    }

    private static int createNodeIdFromMac() {

        try {

            var crc = new CRC32();
            var networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {

                var ni = networkInterfaces.nextElement();
                byte[] mac = ni.getHardwareAddress();

                if (mac != null && mac.length > 0) {
                    crc.update(mac);
                }
            }

            return (int) crc.getValue() & MAX_NODE_ID;

        } catch (Exception e) {
            return ThreadLocalRandom.current().nextInt() & MAX_NODE_ID;
        }
    }

    public static Snowflake get() {

        return INSTANCE;
    }

    /**
     * In Kubernetes, MUST be explicit (StatefulSet ordinally recommended).
     * -Dsnowflake.nodeId=3 OR SNOWFLAKE_NODE_ID=myapp-3
     */
    private static int resolveNodeId() {

        String s = System.getProperty("snowflake.nodeId");

        if (s == null || s.isBlank()) {
            s = System.getenv("SNOWFLAKE_NODE_ID");
        }

        boolean inK8s = System.getenv("KUBERNETES_SERVICE_HOST") != null;

        if (s != null && !s.isBlank()) {
            s = s.trim();
            try {
                return Integer.parseInt(s) & MAX_NODE_ID;
            } catch (NumberFormatException ignored) { }

            // StatefulSet pod name: <name>-<ordinal>
            int dash = s.lastIndexOf('-');
            if (dash >= 0 && dash + 1 < s.length()) {
                try {
                    return Integer.parseInt(s.substring(dash + 1)) & MAX_NODE_ID;
                } catch (NumberFormatException ignored) { }
            }
        }

        // Fail-fast in K8s to prevent silent duplicates
        if (inK8s) {
            throw new IllegalStateException(
                "SNOWFLAKE_NODE_ID (or -Dsnowflake.nodeId) must be set in Kubernetes to avoid duplicate IDs.");
        }

        // non-K8s fallback only
        return createNodeIdFromMac();
    }

    private static long timestamp() {

        return System.currentTimeMillis() - BIG_BANG;
    }

    private static long waitNextMillis(long lastTimestamp) {

        long ts;

        do {

            ts = timestamp();
            Thread.onSpinWait();

            if (ts <= lastTimestamp) {
                LockSupport.parkNanos(100_000); // 0.1ms to avoid hot spin
            }

        } while (ts <= lastTimestamp);

        return ts;
    }

    public synchronized long nextId() {

        long currentTimestamp = timestamp();

        // safer than throwing in prod: wait until the clock catches up
        if (currentTimestamp < this.lastTimestamp) {
            currentTimestamp = waitNextMillis(this.lastTimestamp);
        }

        if (currentTimestamp == this.lastTimestamp) {

            this.sequence = (this.sequence + 1) & MAX_SEQUENCE;

            if (this.sequence == 0) {
                currentTimestamp = waitNextMillis(this.lastTimestamp);
            }

        } else {
            this.sequence = 0;
        }

        this.lastTimestamp = currentTimestamp;

        return (currentTimestamp << TIMESTAMP_SHIFT) | ((long) this.nodeId << NODE_ID_SHIFT) |
                      (this.sequence & MAX_SEQUENCE);
    }

}