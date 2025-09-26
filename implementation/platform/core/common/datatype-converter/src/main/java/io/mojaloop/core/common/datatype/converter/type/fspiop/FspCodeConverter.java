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

package io.mojaloop.core.common.datatype.converter.type.fspiop;

import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FspCodeConverter implements AttributeConverter<FspCode, String> {

    @Override
    public String convertToDatabaseColumn(FspCode attribute) {

        return attribute == null ? null : attribute.value();
    }

    @Override
    public FspCode convertToEntityAttribute(String dbData) {

        return dbData == null ? null : new FspCode(dbData);
    }

}
