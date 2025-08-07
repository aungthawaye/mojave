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

package io.mojaloop.fspiop.common.exception;

import io.mojaloop.fspiop.common.error.ErrorDefinition;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import lombok.Getter;

public class FspiopException extends Exception {

    @Getter
    private final ErrorDefinition errorDefinition;

    public FspiopException(ErrorDefinition errorDefinition) {

        super(errorDefinition.description());
        this.errorDefinition = errorDefinition;
    }

    public FspiopException(ErrorDefinition errorDefinition, String message) {

        super(message);
        this.errorDefinition = errorDefinition;
    }

    public FspiopException(ErrorDefinition errorDefinition, Throwable e) {

        super(e);
        this.errorDefinition = errorDefinition;
    }

    public ErrorInformationObject toErrorObject() {

        return this.errorDefinition.toErrorObject();
    }

}
