package io.mojaloop.component.redis;

public class RedisDefaults {

    public static final String CODEC = "kryo";

    public static final int EXECUTOR_COUNT = Runtime.getRuntime().availableProcessors() * 2;

    public static final int CONNECTION_POOL_SIZE = Math.max(8, Math.min(RedisDefaults.EXECUTOR_COUNT, 32));

    public static final int CONNECTION_POOL_MIN_IDLE = CONNECTION_POOL_SIZE / 2;

}
