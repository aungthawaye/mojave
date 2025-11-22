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

package io.mojaloop.core.transfer.contract.command;

import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;

public interface GetTransfersCommand {

    Output execute(Input input);

    record Input(FspiopHttpRequest request, UdfTransferId udfTransferId) { }

    record Output() { }

}
