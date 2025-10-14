/*-
 * ==============================================================================
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
 * ==============================================================================
 */

package io.mojaloop.core.transaction.contract.exception.fundtransfer;

import io.mojaloop.component.misc.exception.DomainException;
import io.mojaloop.component.misc.exception.ErrorTemplate;

public class FundTransferDefinitionNameTakenException extends DomainException {

    private static final String TEMPLATE = "Fund Transfer Definition name ({0}) is already taken.";

    public FundTransferDefinitionNameTakenException(String name) {

        super(new ErrorTemplate("FUND_TRANSFER_DEFINITION_NAME_TAKEN", TEMPLATE), name);
    }

}
