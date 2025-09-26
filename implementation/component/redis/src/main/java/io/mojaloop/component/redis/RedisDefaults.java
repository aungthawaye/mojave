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

package io.mojaloop.component.redis;

public class RedisDefaults {

    public static final String CODEC = "kryo";

    public static final int EXECUTOR_COUNT = Runtime.getRuntime().availableProcessors() * 2;

    public static final int CONNECTION_POOL_SIZE = Math.max(8, Math.min(RedisDefaults.EXECUTOR_COUNT, 32));

    public static final int CONNECTION_POOL_MIN_IDLE = CONNECTION_POOL_SIZE / 2;

}
