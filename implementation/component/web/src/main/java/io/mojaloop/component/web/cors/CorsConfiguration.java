package io.mojaloop.component.web.cors;

public class CorsConfiguration {

    public interface RequiredBeans { }

    public interface RequiredSettings {

        public CorsConfigurer.Settings corsSettings();

    }

}
