/*-
 * ================================================================================
 * Mojave
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

package org.mojave.component.misc.logger;

import tools.jackson.databind.ObjectMapper;

public class ObjectLogger {

    private static ObjectMapper OBJECT_MAPPER = null;

    public static void init(ObjectMapper objectMapper) {

        OBJECT_MAPPER = objectMapper;
    }

    public static Object log(Object object) {

        return new LazyObject(object);
    }

    private record LazyObject(Object value) {

        @Override
        public String toString() {

            switch (value) {
                case null -> {
                    return "null";
                }
                case String s -> {
                    return s;
                }
                case Number number -> {
                    return String.valueOf(value);
                }
                case Boolean b -> {
                    return String.valueOf(value);
                }
                case Enum<?> anEnum -> {
                    return String.valueOf(value);
                }
                case Exception e -> {
                    return e.getMessage();
                }
                default -> {
                    return OBJECT_MAPPER.writeValueAsString(value);
                }
            }

        }

    }

}
