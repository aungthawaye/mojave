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
/*-
 * ==============================================================================
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
 * ==============================================================================
 */

package io.mojaloop.core.transaction.contract.exception;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionPhase;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class InvalidTransactionStageException extends UncheckedDomainException {

    public static final String CODE = "INVALID_TRANSACTION_STAGE";

    private static final String TEMPLATE = "Transaction Stage ({0}) is not valid. Current stage is ({1}) stage.";

    private final TransactionPhase newStage;

    private final TransactionPhase expectedStage;

    public InvalidTransactionStageException(final TransactionPhase newStage, final TransactionPhase expectedStage) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{newStage.toString(), expectedStage.toString()}));

        this.newStage = newStage;
        this.expectedStage = expectedStage;
    }

    public static InvalidTransactionStageException from(final Map<String, String> extras) {

        final var newStage = TransactionPhase.valueOf(extras.get(Keys.NEW_STAGE));
        final var expected = TransactionPhase.valueOf(extras.get(Keys.EXPECTED_STAGE));

        return new InvalidTransactionStageException(newStage, expected);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.NEW_STAGE, this.newStage.name());
        extras.put(Keys.EXPECTED_STAGE, this.expectedStage.name());

        return extras;
    }

    public static class Keys {

        public static final String NEW_STAGE = "newStage";

        public static final String EXPECTED_STAGE = "expectedStage";

    }

}
