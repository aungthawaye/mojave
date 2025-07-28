/*-
 * ================================================================================
 * Mojaloop OSS
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
package io.mojaloop.component.misc.handy;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;

/**
 * Distributed Sequence Generator. Inspired by Twitter snowflake:
 * <p>
 * <a href="https://github.com/twitter/snowflake/tree/snowflake-2010">...</a>
 */
public final class Snowflake {

    private static final int TOTAL_BITS = 64;

    private static final int EPOCH_BITS = 42;

    private static final int NODE_ID_BITS = 10;

    private static final int SEQUENCE_BITS = 12;

    private static final int MAX_NODE_ID = (int) (Math.pow(2, NODE_ID_BITS) - 1);

    private static final int MAX_SEQUENCE = (int) (Math.pow(2, SEQUENCE_BITS) - 1);

    private static final long BIG_BANG = 1577813400000L;

    private final static Snowflake INSTANCE = new Snowflake();

    private final int nodeId;

    private long lastTimestamp = -1L;

    private long sequence = 0L;

    public Snowflake() {

        this.nodeId = this.createNodeId();

    }

    public static Snowflake get() {

        return INSTANCE;

    }

    private static long timestamp() {

        return System.currentTimeMillis() - BIG_BANG;

    }

    public synchronized long nextId() {

        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp) {

            throw new IllegalStateException("Invalid System Clock!");

        }

        if (currentTimestamp == lastTimestamp) {

            sequence = (sequence + 1) & MAX_SEQUENCE;

            if (sequence == 0) {

                currentTimestamp = waitNextMillis(currentTimestamp);

            }

        } else {

            sequence = 0;

        }

        lastTimestamp = currentTimestamp;

        long id = currentTimestamp << (TOTAL_BITS - EPOCH_BITS);
        id |= ((long) nodeId << (TOTAL_BITS - EPOCH_BITS - NODE_ID_BITS));
        id |= sequence;

        return id;
    }

    private int createNodeId() {

        int nodeId;

        try {

            StringBuilder sb = new StringBuilder();

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = networkInterfaces.nextElement();

                byte[] mac = networkInterface.getHardwareAddress();

                if (mac != null) {

                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                }
            }

            nodeId = sb.toString().hashCode();

        } catch (Exception ex) {

            nodeId = (new SecureRandom().nextInt());
        }

        nodeId = nodeId & MAX_NODE_ID;

        return nodeId;
    }

    private long waitNextMillis(long currentTimestamp) {

        while (currentTimestamp == lastTimestamp) {

            currentTimestamp = timestamp();

        }

        return currentTimestamp;

    }

}
