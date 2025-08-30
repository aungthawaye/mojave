package io.mojaloop.connector.adapter;

import io.mojaloop.connector.adapter.fsp.FspAdapter;
import org.springframework.context.annotation.ComponentScan;

public class ConnectorAdapterConfiguration {

    public interface RequiredBeans {

        FspAdapter fspAdapter();

    }

    public interface RequiredSettings {

    }

}
