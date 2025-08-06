package io.mojaloop.connector.adapter;

import io.mojaloop.connector.adapter.fsp.FspAdapter;

public class ConnectorAdapterConfiguration {

    public interface RequiredBeans {

        FspAdapter fspAdapter();

    }

    public interface RequiredSettings {

    }

}
