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
package org.mojave.core.common.datatype.converter.identifier.wallet;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.mojave.core.common.datatype.identifier.wallet.BalanceUpdateId;

@Converter(autoApply = true)
public class BalanceUpdateIdConverter implements AttributeConverter<BalanceUpdateId, Long> {

    @Override
    public Long convertToDatabaseColumn(BalanceUpdateId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public BalanceUpdateId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new BalanceUpdateId(dbData);
    }

}
