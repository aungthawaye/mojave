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

    public static final int LEN_1 = 1;

    public static final int LEN_2 = 2;

    public static final int LEN_3 = 3;

    public static final int LEN_4 = 4;

    public static final int LEN_16 = 16;

    public static final int LEN_24 = 24;

    public static final int LEN_48 = 48;

    public static final int LEN_64 = 64;

    public static final int LEN_128 = 128;

    public static final int LEN_256 = 256;

    public static final int LEN_512 = 512;

    public static final int LEN_1024 = 1024;

    public static final int LEN_4096 = 4096;

    public static class Errors {

        //@@formatter:off
        public static final ErrorMessage IDENTIFIER_EXCEEDS_MAX_LENGTH = new ErrorMessage("IDENTIFIER_EXCEEDS_MAX_LENGTH","The length of Identifier must NOT exceed " + LEN_48 + ".");
        public static final ErrorMessage CODE_EXCEEDS_MAX_LENGTH = new ErrorMessage("CODE_EXCEEDS_MAX_LENGTH","The length of Code must NOT exceed " + LEN_24 + ".");
        public static final ErrorMessage TEXT_EXCEEDS_MAX_LENGTH = new ErrorMessage("TEXT_EXCEEDS_MAX_LENGTH","The length of Text must NOT exceed " + LEN_256 + ".");
        public static final ErrorMessage PARAGRAPH_EXCEEDS_MAX_LENGTH = new ErrorMessage("PARAGRAPH_EXCEEDS_MAX_LENGTH","The length of Paragraph must NOT exceed " + LEN_4096 + ".");
        //@@formatter:on
    }

}
