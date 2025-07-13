package io.mojaloop.common.component.exception;

public interface LocaleTranslator {

    String translate(String code, String... values);

}
