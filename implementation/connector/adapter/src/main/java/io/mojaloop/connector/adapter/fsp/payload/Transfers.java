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

package io.mojaloop.connector.adapter.fsp.payload;

import io.mojaloop.fspiop.common.data.Agreement;
import io.mojaloop.fspiop.spec.core.ExtensionList;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.TransferState;

public class Transfers {

    public static class Post {

        public record Request(String transferId, Agreement agreement, ExtensionList extensionList) { }

        public record Response(String homeTransactionId) { }

    }

    public static class Patch {

        public record Request(String transferId, TransferState transferState, String completedTimestamp, ExtensionList extensionList) { }

    }

}
