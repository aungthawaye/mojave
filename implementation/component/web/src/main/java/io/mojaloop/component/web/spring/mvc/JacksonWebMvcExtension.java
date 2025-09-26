package io.mojaloop.component.web.spring.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

public class JacksonWebMvcExtension implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;

    public JacksonWebMvcExtension(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        for (int i = 0; i < converters.size(); i++) {

            HttpMessageConverter<?> c = converters.get(i);

            if (c instanceof MappingJackson2HttpMessageConverter) {

                converters.set(i, new MappingJackson2HttpMessageConverter(this.objectMapper));

                return; // replaced the existing one
            }
        }

        // none present? add ours at the front
        converters.addFirst(new MappingJackson2HttpMessageConverter(this.objectMapper));
    }

}
