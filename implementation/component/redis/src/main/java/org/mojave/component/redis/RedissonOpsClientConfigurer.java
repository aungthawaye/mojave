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
package org.mojave.component.redis;

import org.redisson.Redisson;
import org.redisson.codec.Kryo5Codec;
import org.redisson.codec.LZ4Codec;
import org.redisson.codec.LZ4CodecV2;
import org.redisson.config.Config;

import java.util.concurrent.Executors;

public class RedissonOpsClientConfigurer {

    public static RedissonOpsClient configure(Settings settings) {

        var config = new Config();

        if (settings.codec() == null) {

            config.setCodec(new Kryo5Codec());

        } else {

            switch (settings.codec()) {
                case Codec.KRYO:
                    config.setCodec(new Kryo5Codec());
                    break;
                case Codec.LZ4:
                    config.setCodec(new LZ4Codec(new Kryo5Codec()));
                    break;
                case Codec.LZ4V2:
                    config.setCodec(new LZ4CodecV2(new Kryo5Codec()));
                default:
                    config.setCodec(new Kryo5Codec());
            }
        }

        config.setNettyThreads(Runtime.getRuntime().availableProcessors() * 2);
        config.setExecutor(Executors.newFixedThreadPool(
            settings.executorCount() != 0 ? settings.executorCount() :
                Runtime.getRuntime().availableProcessors() * 2));

        int masterConnectionPoolSize =
            settings.connectionPoolSize() != 0 ? settings.connectionPoolSize() : 64;
        int masterConnectionMinimumIdleSize =
            settings.connectionMinimumIdleSize() != 0 ? settings.connectionMinimumIdleSize() : 64;

        if (!settings.cluster()) {

            config
                .useSingleServer()
                .setAddress(settings.hosts()[0])
                .setConnectTimeout(5000)
                .setTimeout(3000)
                .setIdleConnectionTimeout(10000)
                .setRetryAttempts(3)
                .setConnectionPoolSize(masterConnectionPoolSize)
                .setConnectionMinimumIdleSize(masterConnectionMinimumIdleSize)
                .setPingConnectionInterval(10000);

        } else {

            config
                .useClusterServers()
                .addNodeAddress(settings.hosts())
                .setScanInterval(2000)
                .setConnectTimeout(5000)
                .setTimeout(3000)
                .setIdleConnectionTimeout(10000)
                .setRetryAttempts(3)
                .setMasterConnectionPoolSize(masterConnectionPoolSize)
                .setMasterConnectionMinimumIdleSize(masterConnectionMinimumIdleSize)
                .setSlaveConnectionPoolSize(masterConnectionPoolSize)
                .setSlaveConnectionMinimumIdleSize(masterConnectionMinimumIdleSize)
                .setPingConnectionInterval(10000);
        }

        return new RedissonOpsClient(Redisson.create(config));
    }

    public static class Codec {

        public static final String KRYO = "kryo";

        public static final String LZ4 = "lz4";

        public static final String LZ4V2 = "lz4v2";

    }

    public record Settings(String[] hosts,
                           boolean cluster,
                           String codec,
                           int executorCount,
                           int connectionPoolSize,
                           int connectionMinimumIdleSize) { }

}
