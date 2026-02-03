package org.mojave.component.misc.handy;

import com.google.common.primitives.UnsignedLong;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

public class MoneyUtil {

    private static final BigInteger UINT64_MAX = new BigInteger(UnsignedLong.MAX_VALUE.toString());

    public static BigDecimal deserialize(UnsignedLong amount, int scale) {

        Objects.requireNonNull(amount);
        if (scale < 0) {
            throw new IllegalArgumentException("scale must be greater than or equal to 0");
        }

        BigInteger minor = new BigInteger(amount.toString());

        return new BigDecimal(minor).movePointLeft(scale);
    }

    public static UnsignedLong serialize(BigDecimal amount, int scale, RoundingMode roundingMode) {

        Objects.requireNonNull(amount);
        if (scale < 0) {
            throw new IllegalArgumentException("scale must be greater than or equal to 0");
        }
        Objects.requireNonNull(roundingMode);

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
