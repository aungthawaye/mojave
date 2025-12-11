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
package org.mojave.core.accounting.contract.exception.definition;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.Map;

@Getter
public class RequireParticipantForReceiveInException extends UncheckedDomainException {

    public static final String CODE = "REQUIRE_PARTICIPANT_FOR_RECEIVE_IN";

    private static final String TEMPLATE = "Participant is required to receive through Chart Entry.";

    public RequireParticipantForReceiveInException() {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[0]));
    }

    public static RequireParticipantForReceiveInException from(final Map<String, String> extras) {

        return new RequireParticipantForReceiveInException();
    }

    @Override
    public Map<String, String> extras() {

        return Map.of();
    }

}
