package io.mojaloop.common.component.constraint;

import io.mojaloop.common.component.exception.ErrorMessage;

public class StringSizeConstraints {

    public static final int IDENTIFIER = 48;

    public static final int CODE = 64;

    public static final int TEXT = 256;

    public static final int PARAGRAPH = 4096;

    public static class Errors {

        //@@formatter:off
        public static final ErrorMessage IDENTIFIER_EXCEEDS_MAX_LENGTH = new ErrorMessage("IDENTIFIER_EXCEEDS_MAX_LENGTH","The length of Identifier must NOT exceed " + IDENTIFIER + ".");
        public static final ErrorMessage CODE_EXCEEDS_MAX_LENGTH = new ErrorMessage("CODE_EXCEEDS_MAX_LENGTH","The length of Code must NOT exceed " + CODE + ".");
        public static final ErrorMessage TEXT_EXCEEDS_MAX_LENGTH = new ErrorMessage("TEXT_EXCEEDS_MAX_LENGTH","The length of Text must NOT exceed " + TEXT + ".");
        public static final ErrorMessage PARAGRAPH_EXCEEDS_MAX_LENGTH = new ErrorMessage("PARAGRAPH_EXCEEDS_MAX_LENGTH","The length of Paragraph must NOT exceed " + PARAGRAPH + ".");
        //@@formatter:on
    }

}
