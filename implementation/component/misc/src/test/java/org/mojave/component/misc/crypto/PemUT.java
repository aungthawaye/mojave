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
package org.mojave.component.misc.crypto;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PemUT {

    private static final Logger LOGGER = LoggerFactory.getLogger(PemUT.class);

    @Test
    public void test() {

        var pem = "-----BEGIN PUBLIC KEY-----\n" +
                      "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr+f0vHYEtU4z+VCFWCA5\n" +
                      "5Y19S6kEktXRXnUCyzLOxjuIV2alov2MwuAbOYe6LrvazkGZtzTzi1+S/noxBvJX\n" +
                      "tDAxM9qKXLMuq+pmku65dWWBUyQt4uRkpu6a+V8zK73aJM5LycgmXDwWN5fNzN5l\n" +
                      "rTRdiwMVB9SrekqhnJjIBWO8E4tZwY4j+Qr1vSRj4PxVLMxIktxQViUSrFn2WVfF\n" +
                      "M5wZset33rGQSgZv9TYFKAlQA3DZ9yGJWR7h/tz6deLu5tX0o/L20RsFJZ4RwBZL\n" +
                      "4qCwhM7+cTv94/UWYWUL0imnm55w6aFS+YbpAwlJPNi4p2mtTnvquWjEUfZ+r4Bh\n" +
                      "6wIDAQAB\n" + "-----END PUBLIC KEY-----";

        byte[] data = Pem.from(pem);
    }

}
