package io.mojaloop.common.component.exception;

import lombok.Getter;

@Getter
public abstract class InputException extends RuntimeException {

    public InputException(String message) {

        super(message);
    }

    public String convert(CodeConverter converter) {

        return converter.convert(this.getTemplate().code());
    }

    public abstract String[] getFillers();

    public abstract ErrorTemplate getTemplate();

    public String translate(LocaleTranslator translator) {

        return translator.translate(this.getTemplate().code(), this.getFillers());
    }


}
