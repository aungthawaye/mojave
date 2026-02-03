/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.core.accounting.admin.controller.api.command.account;

import jakarta.validation.Valid;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.Objects;

@RestController
public class CreateAccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAccountController.class);

    private final CreateAccountCommand createAccountCommand;

    public CreateAccountController(CreateAccountCommand createAccountCommand) {

        Objects.requireNonNull(createAccountCommand);

        this.createAccountCommand = createAccountCommand;
    }

    @PostMapping("/accounts/create")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CreateAccountCommand.Output execute(
        @Valid @RequestBody CreateAccountCommand.Input input) {

        return this.createAccountCommand.execute(input);
    }

}
