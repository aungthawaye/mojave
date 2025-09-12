package io.mojaloop.connector.adapter;

import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.connector.adapter.fsp.client.FspClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(value = {MiscConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.connector.adapter"})
public class ConnectorAdapterConfiguration implements MiscConfiguration.RequiredBeans {

    public interface RequiredBeans {

        FspClient fspClient();

    }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings {

    }

}
