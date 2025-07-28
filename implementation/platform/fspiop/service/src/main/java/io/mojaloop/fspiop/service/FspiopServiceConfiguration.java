package io.mojaloop.fspiop.service;

import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import io.mojaloop.fspiop.service.component.security.FspiopSpringSecurityConfiguration;
import org.springframework.context.annotation.Import;

@Import(value = {FspiopSpringSecurityConfiguration.class, FspiopCommonConfiguration.class})
public class FspiopServiceConfiguration {

    public interface RequiredSettings
        extends FspiopSpringSecurityConfiguration.RequiredSettings, FspiopCommonConfiguration.RequiredSettings {

    }

}
