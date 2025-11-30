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

package io.mojaloop.connector.adapter.fsp.client;

import io.mojaloop.connector.adapter.fsp.payload.Parties;
import io.mojaloop.connector.adapter.fsp.payload.Quotes;
import io.mojaloop.connector.adapter.fsp.payload.Transfers;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payer;

public interface FspClient {

    Parties.Get.Response getParties(Payer payer, Parties.Get.Request request)
        throws FspiopException;

    void patchTransfers(Payer payer, Transfers.Patch.Request request) throws FspiopException;

    Quotes.Post.Response postQuotes(Payer payer, Quotes.Post.Request request)
        throws FspiopException;

    Transfers.Post.Response postTransfers(Payer payer, Transfers.Post.Request request)
        throws FspiopException;

}
