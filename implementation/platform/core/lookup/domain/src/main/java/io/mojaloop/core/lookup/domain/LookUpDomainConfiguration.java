package io.mojaloop.core.lookup.domain;

import io.mojaloop.common.component.ComponentConfiguration;
import io.mojaloop.common.fspiop.FspiopConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.lookup.domain"})
@Import(value = {ComponentConfiguration.class, FspiopConfiguration.class, LookUpDomainSettings.class})
public class LookUpDomainConfiguration {

    public interface RequiredSettings extends ComponentConfiguration.RequiredSettings, FspiopConfiguration.RequiredSettings { }

}
