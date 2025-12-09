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

package io.mojaloop.core.wallet.contract.exception.balance;

import io.mojaloop.component.misc.exception.CheckedDomainException;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ReversalFailedInWalletException extends CheckedDomainException {

    public static final String CODE = "REVERSAL_FAILED_IN_WALLET";

    private static final String TEMPLATE = "Reversal failed : withdrawId ({0}).";

    private final BalanceUpdateId reversalId;

    public ReversalFailedInWalletException(final BalanceUpdateId reversalId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{reversalId.getId().toString()}));

        this.reversalId = reversalId;
    }

    public static ReversalFailedInWalletException from(final Map<String, String> extras) {

        final var reversalId = new BalanceUpdateId(Long.valueOf(extras.get(Keys.WITHDRAW_ID)));

        return new ReversalFailedInWalletException(reversalId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.WITHDRAW_ID, this.reversalId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String WITHDRAW_ID = "withdrawId";

    }

}
