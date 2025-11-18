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
import org.interledger.core.InterledgerFulfillment;
import org.interledger.core.InterledgerPreparePacket;
import org.interledger.encoding.asn.framework.CodecContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

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

    public static Optional<String> fulfil(String ilpSecret, String peer, UnsignedLong amount, String data, String condition, int lifetimeSeconds) {

        assert ilpSecret != null;
        assert peer != null;
        assert amount != null;
        assert condition != null;

        var fulfilmentPacket = Interledger.prepare(ilpSecret, peer, amount, data, lifetimeSeconds);
        var fulfilmentCondition = fulfilmentPacket.base64Condition;
        var fulfilment = fulfilmentPacket.base64Fulfillment;

        LOGGER.debug("fulfilmentCondition: {}", fulfilmentCondition);
        LOGGER.debug("condition: {}", condition);

        var valid = fulfilmentCondition.equals(condition);

        return Optional.ofNullable(valid ? fulfilment : null);

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

        InterledgerPreparePacket preparePacket = InterledgerPreparePacket.builder()
                                                                         .amount(amount)
                                                                         .destination(InterledgerAddress.of(peer))
                                                                         .executionCondition(condition)
                                                                         .expiresAt(Instant.now().plusSeconds(lifetimeSeconds))
                                                                         .data(data.getBytes(StandardCharsets.UTF_8))
                                                                         .build();

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

    public static class Amount {

        private static final BigInteger UINT64_MAX = new BigInteger(UnsignedLong.MAX_VALUE.toString());

        public static BigDecimal deserialize(UnsignedLong amount, int scale) {

            assert amount != null;
            assert scale >= 0;

            BigInteger minor = new BigInteger(amount.toString());

            return new BigDecimal(minor).movePointLeft(scale);
        }

        public static UnsignedLong serialize(BigDecimal amount, int scale, RoundingMode roundingMode) {

            assert amount != null;
            assert scale >= 0;
            assert roundingMode != null;

            BigDecimal minor = amount.movePointRight(scale);
            BigInteger asInt = minor.setScale(0, roundingMode).toBigIntegerExact();

            if (asInt.signum() < 0) {
                throw new IllegalArgumentException("Negative ILP amount not allowed");
            }

            if (asInt.compareTo(UINT64_MAX) > 0) {
                throw new IllegalArgumentException("Exceeds UInt64");
            }

            return UnsignedLong.valueOf(asInt.toString());
        }

    }

}
