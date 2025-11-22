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

package io.mojaloop.component.misc.jwt;

import io.mojaloop.component.misc.crypto.Rs256;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

public class Rs256JwtUT {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rs256JwtUT.class);

    @Test
    public void testSign() throws NoSuchAlgorithmException, InvalidKeySpecException {

        var privateKey = Rs256.privateKeyFromPem("-----BEGIN PRIVATE KEY-----\n" + "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCv5/S8dgS1TjP5\n" +
                                                     "UIVYIDnljX1LqQSS1dFedQLLMs7GO4hXZqWi/YzC4Bs5h7ouu9rOQZm3NPOLX5L+\n" +
                                                     "ejEG8le0MDEz2opcsy6r6maS7rl1ZYFTJC3i5GSm7pr5XzMrvdokzkvJyCZcPBY3\n" +
                                                     "l83M3mWtNF2LAxUH1Kt6SqGcmMgFY7wTi1nBjiP5CvW9JGPg/FUszEiS3FBWJRKs\n" +
                                                     "WfZZV8UznBmx63fesZBKBm/1NgUoCVADcNn3IYlZHuH+3Pp14u7m1fSj8vbRGwUl\n" +
                                                     "nhHAFkvioLCEzv5xO/3j9RZhZQvSKaebnnDpoVL5hukDCUk82Linaa1Oe+q5aMRR\n" +
                                                     "9n6vgGHrAgMBAAECggEAR8CBe0hWpk/2cllucuJ+S0z6EV0poO13wCRRleotHv4U\n" +
                                                     "oLYdlU0AWFNKt55OCxcUavKmTW1bdHvxPT1Bd1ht/vFzeHfJ3YM3Y+eynHelDaZw\n" +
                                                     "aKYG05ifF9qXO2YiPNLtwvqlPnMWdqQJ2fZhe5Ix/kMwbu48a1vL8I+1uWc2m7+D\n" +
                                                     "2BeR0C6CeoSI/PXDPuhihL5PHCmM4EUsWX8dfL8Nq+4ktJxw7fYOP4UaVCuFNx96\n" +
                                                     "zezrM1RSJG5iNKqNjAG92NOZPUiwTXsauQHbNdAP3qEAw8JmqJZ7/IWKfM0i9XGW\n" +
                                                     "Wh9tm0qlPYzDpWSZXfVO3qGR1E57cXI5d6dD5BDExQKBgQDkTlTDg3BTcTLPmoCE\n" +
                                                     "oLxukSyrrey4jAaWf58MGl+QZRhljmFj4zOwTPjh9OMsvv++tlUCh86ypGTydzk2\n" +
                                                     "HbBU9ZL6soHtn/SIwHM0SUqtHKcZqsrbjp58lush71e3E+xPXjQCMXCgDzbb5+vu\n" +
                                                     "y8QUcmQJjYC+mzlgApkhwI6+twKBgQDFPm59YoIhU3xHMp7j8RcWSsqFac+mjtiC\n" +
                                                     "a/LkztR7glTegwOmSAauSVW5PGSMYJ9nnvrNSUzqfjtW9XV7febdAoJY5g0YoKEu\n" +
                                                     "WgFkgOGokDbpmTJt3kLtJ5Soo/yfYWftjDUUla8nhlSn0e9ADbNGhHQmtjMxD1v8\n" +
                                                     "J2e1uOJCbQKBgCkDPUzmBr6mnicXLTEmItOF9s1ccCLy3Jgakvq8niA1WP2dZzdn\n" +
                                                     "cDiiiy8kK880/IJJBNpwhRwoVg4MhtWG3c9VUjMA8EA8tNQAJnHlHKoedJIy0UBB\n" +
                                                     "zYy6G8E72rkDtjbHyHYAZagLM6KrdgGuzymRJxFkPxV+kv8BWHrsuXffAoGATxjH\n" +
                                                     "JB7hwk2BU+fX0d63fGgHqNKSw98ascpqQBk1GkZclFKDnyXZEAE2kC7Iv4Zrxhj5\n" +
                                                     "yGLxpkNytl9+ekQ11UERVmcCr3KS0EylrFEGrRsP+kNF9ssfmYy6z5nbT2q3S4pj\n" +
                                                     "KtsPv1DeF+JXCB+2Wbv5/CAqjSz0nTyMGVKH4U0CgYAe6AQcrQfg8ok6lTmZB+3d\n" +
                                                     "qLyE64bSadRa7VsNJkY6/3LoqdFZKNKXgYfYH6wuTDE5upD3xXdBN3IUFqRgN9jY\n" +
                                                     "AuQBfdRswe5p6LfxASnjB8lQgn76CvIyeyM+1RF3C+i3sbF/Pr3uKAUPvsovfMwX\n" + "HyFsQTxMTbJaGmnoi6X+gQ==\n" +
                                                     "-----END PRIVATE KEY-----");

        var publicKey = Rs256.publicKeyFromPem("-----BEGIN PUBLIC KEY-----\n" + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr+f0vHYEtU4z+VCFWCA5\n" +
                                                   "5Y19S6kEktXRXnUCyzLOxjuIV2alov2MwuAbOYe6LrvazkGZtzTzi1+S/noxBvJX\n" +
                                                   "tDAxM9qKXLMuq+pmku65dWWBUyQt4uRkpu6a+V8zK73aJM5LycgmXDwWN5fNzN5l\n" +
                                                   "rTRdiwMVB9SrekqhnJjIBWO8E4tZwY4j+Qr1vSRj4PxVLMxIktxQViUSrFn2WVfF\n" +
                                                   "M5wZset33rGQSgZv9TYFKAlQA3DZ9yGJWR7h/tz6deLu5tX0o/L20RsFJZ4RwBZL\n" +
                                                   "4qCwhM7+cTv94/UWYWUL0imnm55w6aFS+YbpAwlJPNi4p2mtTnvquWjEUfZ+r4Bh\n" + "6wIDAQAB\n" + "-----END PUBLIC KEY-----");

        var headers = new HashMap<String, String>();
        headers.put("alg", "RS256");
        headers.put("typ", "JWT");

        var payload = "{\"date\":\"Fri, 23 May 2025 02:30:00 GMT\"}";

        var token = Rs256Jwt.sign(privateKey, headers, payload);
        LOGGER.debug("token : {}", token);

        var encodedPayload = JwtBase64Util.encode(payload);
        LOGGER.debug("encodedPayload : {}", encodedPayload);

        var ok = Rs256Jwt.verify(publicKey, new Rs256Jwt.Token(token.header(), encodedPayload, token.signature()));
        LOGGER.debug("ok : {}", ok);

    }

}
