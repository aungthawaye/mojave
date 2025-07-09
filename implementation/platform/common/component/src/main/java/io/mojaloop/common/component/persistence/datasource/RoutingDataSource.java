package io.mojaloop.common.component.persistence.datasource;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.InfrastructureProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource implements InfrastructureProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingDataSource.class);

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void clearDataSourceKey() {

        CONTEXT_HOLDER.remove();
    }

    public static String getDataSourceKey() {

        return CONTEXT_HOLDER.get();
    }

    public static void setDataSourceKey(String key) {

        CONTEXT_HOLDER.set(key);
    }

    @NotNull
    @Override
    public Object getWrappedObject() {

        return this.determineTargetDataSource();
    }

    @Override
    protected Object determineCurrentLookupKey() {

        Object key = CONTEXT_HOLDER.get();

        LOGGER.debug("determineCurrentLookupKey - key : [{}]", key);

        return CONTEXT_HOLDER.get();
    }

    public static class Qualifiers {

        public static final String READ = "read";

        public static final String WRITE = "write";

    }

}