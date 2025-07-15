/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.common.component.constraint;

import io.mojaloop.common.component.exception.ErrorMessage;

public class StringSizeConstraints {

    public static final int IDENTIFIER = 48;

    public static final int SHORT_CODE = 24;

    public static final int LONG_CODE = 48;

    public static final int SHORT_STRING = 64;

    public static final int STRING = 128;

    public static final int TEXT = 256;

    public static final int PARAGRAPH = 4096;

    public static class Errors {

        //@@formatter:off
        public static final ErrorMessage IDENTIFIER_EXCEEDS_MAX_LENGTH = new ErrorMessage("IDENTIFIER_EXCEEDS_MAX_LENGTH","The length of Identifier must NOT exceed " + IDENTIFIER + ".");
        public static final ErrorMessage CODE_EXCEEDS_MAX_LENGTH = new ErrorMessage("CODE_EXCEEDS_MAX_LENGTH","The length of Code must NOT exceed " + SHORT_CODE + ".");
        public static final ErrorMessage TEXT_EXCEEDS_MAX_LENGTH = new ErrorMessage("TEXT_EXCEEDS_MAX_LENGTH","The length of Text must NOT exceed " + TEXT + ".");
        public static final ErrorMessage PARAGRAPH_EXCEEDS_MAX_LENGTH = new ErrorMessage("PARAGRAPH_EXCEEDS_MAX_LENGTH","The length of Paragraph must NOT exceed " + PARAGRAPH + ".");
        //@@formatter:on
    }

}
