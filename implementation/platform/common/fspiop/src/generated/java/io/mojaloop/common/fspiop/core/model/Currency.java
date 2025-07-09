package io.mojaloop.common.fspiop.core.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The currency codes defined in [ISO 4217](https://www.iso.org/iso-4217-currency-codes.html) as three-letter alphabetic codes are used as the standard naming representation for currencies.
 */
public enum Currency {
  
  AED("AED"),
  
  AFN("AFN"),
  
  ALL("ALL"),
  
  AMD("AMD"),
  
  ANG("ANG"),
  
  AOA("AOA"),
  
  ARS("ARS"),
  
  AUD("AUD"),
  
  AWG("AWG"),
  
  AZN("AZN"),
  
  BAM("BAM"),
  
  BBD("BBD"),
  
  BDT("BDT"),
  
  BGN("BGN"),
  
  BHD("BHD"),
  
  BIF("BIF"),
  
  BMD("BMD"),
  
  BND("BND"),
  
  BOB("BOB"),
  
  BRL("BRL"),
  
  BSD("BSD"),
  
  BTN("BTN"),
  
  BWP("BWP"),
  
  BYN("BYN"),
  
  BZD("BZD"),
  
  CAD("CAD"),
  
  CDF("CDF"),
  
  CHF("CHF"),
  
  CLP("CLP"),
  
  CNY("CNY"),
  
  COP("COP"),
  
  CRC("CRC"),
  
  CUC("CUC"),
  
  CUP("CUP"),
  
  CVE("CVE"),
  
  CZK("CZK"),
  
  DJF("DJF"),
  
  DKK("DKK"),
  
  DOP("DOP"),
  
  DZD("DZD"),
  
  EGP("EGP"),
  
  ERN("ERN"),
  
  ETB("ETB"),
  
  EUR("EUR"),
  
  FJD("FJD"),
  
  FKP("FKP"),
  
  GBP("GBP"),
  
  GEL("GEL"),
  
  GGP("GGP"),
  
  GHS("GHS"),
  
  GIP("GIP"),
  
  GMD("GMD"),
  
  GNF("GNF"),
  
  GTQ("GTQ"),
  
  GYD("GYD"),
  
  HKD("HKD"),
  
  HNL("HNL"),
  
  HRK("HRK"),
  
  HTG("HTG"),
  
  HUF("HUF"),
  
  IDR("IDR"),
  
  ILS("ILS"),
  
  IMP("IMP"),
  
  INR("INR"),
  
  IQD("IQD"),
  
  IRR("IRR"),
  
  ISK("ISK"),
  
  JEP("JEP"),
  
  JMD("JMD"),
  
  JOD("JOD"),
  
  JPY("JPY"),
  
  KES("KES"),
  
  KGS("KGS"),
  
  KHR("KHR"),
  
  KMF("KMF"),
  
  KPW("KPW"),
  
  KRW("KRW"),
  
  KWD("KWD"),
  
  KYD("KYD"),
  
  KZT("KZT"),
  
  LAK("LAK"),
  
  LBP("LBP"),
  
  LKR("LKR"),
  
  LRD("LRD"),
  
  LSL("LSL"),
  
  LYD("LYD"),
  
  MAD("MAD"),
  
  MDL("MDL"),
  
  MGA("MGA"),
  
  MKD("MKD"),
  
  MMK("MMK"),
  
  MNT("MNT"),
  
  MOP("MOP"),
  
  MRO("MRO"),
  
  MUR("MUR"),
  
  MVR("MVR"),
  
  MWK("MWK"),
  
  MXN("MXN"),
  
  MYR("MYR"),
  
  MZN("MZN"),
  
  NAD("NAD"),
  
  NGN("NGN"),
  
  NIO("NIO"),
  
  NOK("NOK"),
  
  NPR("NPR"),
  
  NZD("NZD"),
  
  OMR("OMR"),
  
  PAB("PAB"),
  
  PEN("PEN"),
  
  PGK("PGK"),
  
  PHP("PHP"),
  
  PKR("PKR"),
  
  PLN("PLN"),
  
  PYG("PYG"),
  
  QAR("QAR"),
  
  RON("RON"),
  
  RSD("RSD"),
  
  RUB("RUB"),
  
  RWF("RWF"),
  
  SAR("SAR"),
  
  SBD("SBD"),
  
  SCR("SCR"),
  
  SDG("SDG"),
  
  SEK("SEK"),
  
  SGD("SGD"),
  
  SHP("SHP"),
  
  SLL("SLL"),
  
  SOS("SOS"),
  
  SPL("SPL"),
  
  SRD("SRD"),
  
  STD("STD"),
  
  SVC("SVC"),
  
  SYP("SYP"),
  
  SZL("SZL"),
  
  THB("THB"),
  
  TJS("TJS"),
  
  TMT("TMT"),
  
  TND("TND"),
  
  TOP("TOP"),
  
  TRY("TRY"),
  
  TTD("TTD"),
  
  TVD("TVD"),
  
  TWD("TWD"),
  
  TZS("TZS"),
  
  UAH("UAH"),
  
  UGX("UGX"),
  
  USD("USD"),
  
  UYU("UYU"),
  
  UZS("UZS"),
  
  VEF("VEF"),
  
  VND("VND"),
  
  VUV("VUV"),
  
  WST("WST"),
  
  XAF("XAF"),
  
  XCD("XCD"),
  
  XDR("XDR"),
  
  XOF("XOF"),
  
  XPF("XPF"),
  
  XTS("XTS"),
  
  XXX("XXX"),
  
  YER("YER"),
  
  ZAR("ZAR"),
  
  ZMW("ZMW"),
  
  ZWD("ZWD");

  private String value;

  Currency(String value) {
    this.value = value;
  }

    /**
     * Convert a String into String, as specified in the
     * <a href="https://download.oracle.com/otndocs/jcp/jaxrs-2_0-fr-eval-spec/index.html">See JAX RS 2.0 Specification, section 3.2, p. 12</a>
     */
    public static Currency fromString(String s) {
      for (Currency b : Currency.values()) {
        // using Objects.toString() to be safe if value type non-object type
        // because types like 'int' etc. will be auto-boxed
        if (java.util.Objects.toString(b.value).equals(s)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected string value '" + s + "'");
    }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static Currency fromValue(String value) {
    for (Currency b : Currency.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}


