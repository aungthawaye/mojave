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

package io.mojaloop.fspiop.common.error;

import io.mojaloop.fspiop.spec.core.ErrorInformation;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;

public record ErrorDefinition(FspiopErrorType errorType, String description) {

    public ErrorInformationObject toErrorObject() {

        return new ErrorInformationObject().errorInformation(
            new ErrorInformation(this.errorType.getCode(), this.description()));
    }

}
