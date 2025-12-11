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
package org.mojave.core.wallet.intercom.controller.component;

import org.mojave.component.web.request.CachedServletRequest;
import org.mojave.component.web.spring.security.AuthenticationFailureException;
import org.mojave.component.web.spring.security.Authenticator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;

public class EmptyGatekeeper implements Authenticator {

    @Override
    public UsernamePasswordAuthenticationToken authenticate(CachedServletRequest cachedServletRequest)
        throws AuthenticationFailureException {

        return new UsernamePasswordAuthenticationToken("user", "password", new ArrayList<>());
    }

}
