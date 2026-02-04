package org.mojave.common.datatype.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum Currency {

    AED("AED", 2, "UAE dirham"),

    AFN("AFN", 2, "Afghanistan afghani"),

    ALL("ALL", 2, "Albanian lek"),

    AMD("AMD", 2, "Armenian dram"),

    ANG("ANG", 2, "Netherlands Antillian guilder"),

    AOA("AOA", 2, "Angolan kwanza"),

    ARS("ARS", 2, "Argentine peso"),

    AUD("AUD", 2, "Australian dollar"),

    AWG("AWG", 2, "Aruban guilder"),

    AZN("AZN", 2, "Azerbaijanian new manat"),

    BAM("BAM", 2, "Bosnia-Herzegovina convertible mark"),

    BBD("BBD", 2, "Barbados dollar"),

    BDT("BDT", 2, "Bangladeshi taka"),

    BGN("BGN", 2, "Bulgarian lev"),

    BHD("BHD", 3, "Bahraini dinar"),

    BIF("BIF", 0, "Burundi franc"),

    BMD("BMD", 2, "Bermudian dollar"),

    BND("BND", 2, "Brunei dollar"),

    BOB("BOB", 2, "Bolivian boliviano"),

    BRL("BRL", 2, "Brazilian real"),

    BSD("BSD", 2, "Bahamian dollar"),

    BTN("BTN", 2, "Bhutan ngultrum"),

    BWP("BWP", 2, "Botswana pula"),

    BYN("BYN", 4, "Belarusian ruble"),

    BZD("BZD", 2, "Belize dollar"),

    CAD("CAD", 2, "Canadian dollar"),

    CDF("CDF", 2, "Congolese franc"),

    CHF("CHF", 2, "Swiss franc"),

    CLP("CLP", 0, "Chilean peso"),

    CNY("CNY", 2, "Chinese yuan renminbi"),

    COP("COP", 2, "Colombian peso"),

    CRC("CRC", 2, "Costa Rican colon"),

    CUC("CUC", 2, "Cuban convertible peso"),

    CUP("CUP", 2, "Cuban peso"),

    CVE("CVE", 2, "Cape Verde escudo"),

    CZK("CZK", 2, "Czech koruna"),

    DJF("DJF", 0, "Djibouti franc"),

    DKK("DKK", 2, "Danish krone"),

    DOP("DOP", 2, "Dominican peso"),

    DZD("DZD", 2, "Algerian dinar"),

    EGP("EGP", 2, "Egyptian pound"),

    ERN("ERN", 2, "Eritrean nakfa"),

    ETB("ETB", 2, "Ethiopian birr"),

    EUR("EUR", 2, "EU euro"),

    FJD("FJD", 2, "Fiji dollar"),

    FKP("FKP", 2, "Falkland Islands pound"),

    GBP("GBP", 2, "British pound"),

    GEL("GEL", 2, "Georgian lari"),

    GGP("GGP", 4, "Guernsey pound"),

    GHS("GHS", 2, "Ghanaian new cedi"),

    GIP("GIP", 2, "Gibraltar pound"),

    GMD("GMD", 2, "Gambian dalasi"),

    GNF("GNF", 0, "Guinean franc"),

    GTQ("GTQ", 2, "Guatemalan quetzal"),

    GYD("GYD", 2, "Guyana dollar"),

    HKD("HKD", 2, "Hong Kong SAR dollar"),

    HNL("HNL", 2, "Honduran lempira"),

    HRK("HRK", 2, "Croatian kuna"),

    HTG("HTG", 2, "Haitian gourde"),

    HUF("HUF", 2, "Hungarian forint"),

    IDR("IDR", 2, "Indonesian rupiah"),

    ILS("ILS", 2, "Israeli new shekel"),

    IMP("IMP", 4, "Isle of Man pound"),

    INR("INR", 2, "Indian rupee"),

    IQD("IQD", 3, "Iraqi dinar"),

    IRR("IRR", 2, "Iranian rial"),

    ISK("ISK", 0, "Icelandic krona"),

    JEP("JEP", 4, "Jersey pound"),

    JMD("JMD", 2, "Jamaican dollar"),

    JOD("JOD", 3, "Jordanian dinar"),

    JPY("JPY", 0, "Japanese yen"),

    KES("KES", 2, "Kenyan shilling"),

    KGS("KGS", 2, "Kyrgyz som"),

    KHR("KHR", 2, "Cambodian riel"),

    KMF("KMF", 0, "Comoros franc"),

    KPW("KPW", 2, "North Korean won"),

    KRW("KRW", 0, "South Korean won"),

    KWD("KWD", 3, "Kuwaiti dinar"),

    KYD("KYD", 2, "Cayman Islands dollar"),

    KZT("KZT", 2, "Kazakh tenge"),

    LAK("LAK", 2, "Lao kip"),

    LBP("LBP", 2, "Lebanese pound"),

    LKR("LKR", 2, "Sri Lanka rupee"),

    LRD("LRD", 2, "Liberian dollar"),

    LSL("LSL", 2, "Lesotho loti"),

    LYD("LYD", 3, "Libyan dinar"),

    MAD("MAD", 2, "Moroccan dirham"),

    MDL("MDL", 2, "Moldovan leu"),

    MGA("MGA", 2, "Malagasy ariary"),

    MKD("MKD", 2, "Macedonian denar"),

    MMK("MMK", 2, "Myanmar kyat"),

    MNT("MNT", 2, "Mongolian tugrik"),

    MOP("MOP", 2, "Macao SAR pataca"),

    MRO("MRO", 2, "Mauritanian ouguiya"),

    MUR("MUR", 2, "Mauritius rupee"),

    MVR("MVR", 2, "Maldivian rufiyaa"),

    MWK("MWK", 2, "Malawi kwacha"),

    MXN("MXN", 2, "Mexican peso"),

    MYR("MYR", 2, "Malaysian ringgit"),

    MZN("MZN", 2, "Mozambique new metical"),

    NAD("NAD", 2, "Namibian dollar"),

    NGN("NGN", 2, "Nigerian naira"),

    NIO("NIO", 2, "Nicaraguan cordoba oro"),

    NOK("NOK", 2, "Norwegian krone"),

    NPR("NPR", 2, "Nepalese rupee"),

    NZD("NZD", 2, "New Zealand dollar"),

    OMR("OMR", 3, "Omani rial"),

    PAB("PAB", 2, "Panamanian balboa"),

    PEN("PEN", 2, "Peruvian nuevo sol"),

    PGK("PGK", 2, "Papua New Guinea kina"),

    PHP("PHP", 2, "Philippine peso"),

    PKR("PKR", 2, "Pakistani rupee"),

    PLN("PLN", 2, "Polish zloty"),

    PYG("PYG", 0, "Paraguayan guarani"),

    QAR("QAR", 2, "Qatari rial"),

    RON("RON", 2, "Romanian new leu"),

    RSD("RSD", 2, "Serbian dinar"),

    RUB("RUB", 2, "Russian ruble"),

    RWF("RWF", 0, "Rwandan franc"),

    SAR("SAR", 2, "Saudi riyal"),

    SBD("SBD", 2, "Solomon Islands dollar"),

    SCR("SCR", 2, "Seychelles rupee"),

    SDG("SDG", 2, "Sudanese pound"),

    SEK("SEK", 2, "Swedish krona"),

    SGD("SGD", 2, "Singapore dollar"),

    SHP("SHP", 2, "Saint Helena pound"),

    SLL("SLL", 2, "Sierra Leone leone"),

    SOS("SOS", 2, "Somali shilling"),

    SPL("SPL", 4, "Seborgan luigino"),

    SRD("SRD", 2, "Suriname dollar"),

    STD("STD", 2, "Sao Tome and Principe dobra"),

    SVC("SVC", 2, "El Salvador colon"),

    SYP("SYP", 2, "Syrian pound"),

    SZL("SZL", 2, "Swaziland lilangeni"),

    THB("THB", 2, "Thai baht"),

    TJS("TJS", 2, "Tajik somoni"),

    TMT("TMT", 2, "Turkmen new manat"),

    TND("TND", 3, "Tunisian dinar"),

    TOP("TOP", 2, "Tongan pa'anga"),

    TRY("TRY", 2, "Turkish lira"),

    TTD("TTD", 2, "Trinidad and Tobago dollar"),

    TVD("TVD", 4, "Tuvaluan dollar"),

    TWD("TWD", 2, "Taiwan New dollar"),

    TZS("TZS", 2, "Tanzanian shilling"),

    UAH("UAH", 2, "Ukrainian hryvnia"),

    UGX("UGX", 0, "Uganda new shilling"),

    USD("USD", 2, "US dollar"),

    UYU("UYU", 2, "Uruguayan peso uruguayo"),

    UZS("UZS", 2, "Uzbekistani sum"),

    VEF("VEF", 2, "Venezuelan bolivar fuerte"),

    VND("VND", 0, "Vietnamese dong"),

    VUV("VUV", 0, "Vanuatu vatu"),

    WST("WST", 2, "Samoan tala"),

    XAF("XAF", 0, "CFA franc BEAC"),

    XCD("XCD", 2, "East Caribbean dollar"),

    XDR("XDR", 4, "IMF special drawing right"),

    XOF("XOF", 0, "CFA franc BCEAO"),

    XPF("XPF", 0, "CFP franc"),

    XTS("XTS", 4, "Reserved for testing purposes"),

    XXX("XXX", 4, "Assigned for transactions where no currency is involved"),

    YER("YER", 2, "Yemeni rial"),

    ZAR("ZAR", 2, "South African rand"),

    ZMW("ZMW", 2, "Zambian kwacha"),

    ZWD("ZWD", 4, "Zimbabwe dollar (initial)");

    private static final Map<String, Currency> CURRENCIES = new HashMap<>();

    static {

        for (final var b : Currency.values()) {
            CURRENCIES.put(b.value, b);
        }
    }

    private final String value;

    private final int scale;

    private final String name;

    Currency(final String value, final int scale, final String name) {

        this.value = value;
        this.scale = scale;
        this.name = name;
    }

    @JsonCreator
    public static Currency from(final String value) {

        final var b = CURRENCIES.get(value);

        if (b == null) {
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

        return b;
    }

    @Override
    @JsonValue
    public String toString() {

        return this.value;
    }
}
