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

package org.mojave.core.wallet.intercom.controller.api.query;

import org.mojave.core.common.datatype.identifier.wallet.PositionId;
import org.mojave.core.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.data.PositionData;
import org.mojave.core.wallet.contract.query.PositionQuery;
import org.mojave.fspiop.spec.core.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PositionQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PositionQueryController.class.getName());

    private final PositionQuery positionQuery;

    public PositionQueryController(final PositionQuery positionQuery) {

        assert positionQuery != null;

        this.positionQuery = positionQuery;
    }

    @GetMapping("/positions/get-by-position-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PositionData get(@RequestParam final PositionId positionId) {

        return this.positionQuery.get(positionId);
    }

    @GetMapping("/positions/get-by-owner-id-currency")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PositionData> get(@RequestParam final WalletOwnerId ownerId,
                                  @RequestParam final Currency currency) {

        return this.positionQuery.get(ownerId, currency);
    }

    @GetMapping("/positions/get-all-positions")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PositionData> getAll() {

        return this.positionQuery.getAll();
    }

}
