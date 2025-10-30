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

package io.mojaloop.fspiop.component.interledger;

import com.google.common.primitives.UnsignedLong;
import org.apache.commons.codec.digest.DigestUtils;
import org.interledger.codecs.ilp.InterledgerCodecContextFactory;
import org.interledger.core.InterledgerAddress;
import org.interledger.core.InterledgerCondition;
import org.interledger.core.InterledgerFulfillment;
import org.interledger.core.InterledgerPreparePacket;
import org.interledger.encoding.asn.framework.CodecContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class Interledger {

    private static final Logger LOGGER = LoggerFactory.getLogger(Interledger.class);

    private static final CodecContext CODEC_CONTEXT = InterledgerCodecContextFactory.oer();

    private Interledger() {

    }

    public static String address(String peer) {

        return "g." + peer;
    }

    public static byte[] base64Decode(String data) {

        return Base64.getUrlDecoder().decode(data);
    }

    public static String base64Encode(byte[] data, boolean padding) {

        return !padding ? Base64.getUrlEncoder().withoutPadding().encodeToString(data) : Base64.getUrlEncoder().encodeToString(data);
    }

    private static <T> T deserialize(String base64Packet, Class<T> clazz) {

        try (var bin = new ByteArrayInputStream(Base64.getUrlDecoder().decode(base64Packet))) {

            return CODEC_CONTEXT.read(clazz, bin);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Fulfill fulfill(String ilpSecret,
                                  String peer,
                                  UnsignedLong amount,
                                  String data,
                                  String comparingBase64Condition,
                                  String comparingBase64Packet,
                                  int lifetimeSeconds) {

        assert ilpSecret != null;
        assert peer != null;
        assert amount != null;
        assert comparingBase64Packet != null;

        var createdPacket = Interledger.prepare(ilpSecret, peer, amount, data, lifetimeSeconds);
        var createdCondition = createdPacket.base64Condition;
        var createdFulfillment = createdPacket.base64Fulfillment;

        LOGGER.debug("createdFulfillment.preimage: {}", base64Decode(createdFulfillment));

        var unwrappedPacket = Interledger.unwrap(comparingBase64Packet);
        var unwrappedCondition = base64Encode(unwrappedPacket.getExecutionCondition().getHash(), false);
        var comparingCondition = base64Encode(InterledgerCondition.of(base64Decode(comparingBase64Condition)).getHash(), false);

        LOGGER.debug("createdCondition: {}", createdCondition);
        LOGGER.debug("unwrappedCondition: {}", unwrappedCondition);
        LOGGER.debug("comparingCondition: {}", comparingCondition);

        var valid = createdCondition.equals(unwrappedCondition) && comparingCondition.equals(unwrappedCondition);

        return new Fulfill(valid, createdFulfillment);

    }

    private static byte[] preimage(String ilpSecret, UnsignedLong amount, String destination, String data) {

        String joined = ilpSecret + ":" + amount.longValue() + ":" + destination + ":" + data;

        return DigestUtils.sha256(joined.getBytes(StandardCharsets.UTF_8));
    }

    public static Prepare prepare(String ilpSecret, String peer, UnsignedLong amount, String data, int lifetimeSeconds) {

        assert ilpSecret != null;
        assert peer != null;
        assert amount != null;

        var preimage = preimage(ilpSecret, amount, peer, data);
        var fulfillment = InterledgerFulfillment.of(preimage);
        var condition = fulfillment.getCondition();

        LOGGER.debug("preimage: {}", preimage);
        LOGGER.debug("fulfillment.preimage: {}", fulfillment.getPreimage());

        InterledgerPreparePacket preparePacket = InterledgerPreparePacket.builder().amount(amount).destination(InterledgerAddress.of(peer)).executionCondition(condition)
                                                                         .expiresAt(Instant.now().plusSeconds(lifetimeSeconds)).data(data.getBytes(StandardCharsets.UTF_8)).build();

        return new Prepare(base64Encode(serialize(preparePacket), true), base64Encode(fulfillment.getPreimage(), false), base64Encode(condition.getHash(), false));

    }

    private static <T> byte[] serialize(T any) {

        try (var bout = new ByteArrayOutputStream()) {

            CODEC_CONTEXT.write(any, bout);

            bout.flush();

            return bout.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static InterledgerPreparePacket unwrap(String base64Packet) {

        assert base64Packet != null;

        return deserialize(base64Packet, InterledgerPreparePacket.class);
    }

    public record Prepare(String base64PreparePacket, String base64Fulfillment, String base64Condition) { }

    public record Fulfill(boolean valid, String base64Fulfillment) { }

}
