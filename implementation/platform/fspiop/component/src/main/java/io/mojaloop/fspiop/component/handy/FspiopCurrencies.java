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

package io.mojaloop.fspiop.component.handy;

import io.mojaloop.fspiop.spec.core.Currency;

import java.util.HashMap;
import java.util.Map;

public class FspiopCurrencies {

    // Currency profiles based on ISO 4217 standards
    public static final Profile AED = new Profile(Currency.AED, "UAE dirham", 2);

    public static final Profile AFN = new Profile(Currency.AFN, "Afghanistan afghani", 2);

    public static final Profile ALL = new Profile(Currency.ALL, "Albanian lek", 2);

    public static final Profile AMD = new Profile(Currency.AMD, "Armenian dram", 2);

    public static final Profile ANG = new Profile(Currency.ANG, "Netherlands Antillian guilder", 2);

    public static final Profile AOA = new Profile(Currency.AOA, "Angolan kwanza", 2);

    public static final Profile ARS = new Profile(Currency.ARS, "Argentine peso", 2);

    public static final Profile AUD = new Profile(Currency.AUD, "Australian dollar", 2);

    public static final Profile AWG = new Profile(Currency.AWG, "Aruban guilder", 2);

    public static final Profile AZN = new Profile(Currency.AZN, "Azerbaijanian new manat", 2);

    public static final Profile BAM = new Profile(
        Currency.BAM, "Bosnia-Herzegovina convertible mark", 2);

    public static final Profile BBD = new Profile(Currency.BBD, "Barbados dollar", 2);

    public static final Profile BDT = new Profile(Currency.BDT, "Bangladeshi taka", 2);

    public static final Profile BGN = new Profile(Currency.BGN, "Bulgarian lev", 2);

    public static final Profile BHD = new Profile(Currency.BHD, "Bahraini dinar", 3);

    public static final Profile BIF = new Profile(Currency.BIF, "Burundi franc", 0);

    public static final Profile BMD = new Profile(Currency.BMD, "Bermudian dollar", 2);

    public static final Profile BND = new Profile(Currency.BND, "Brunei dollar", 2);

    public static final Profile BOB = new Profile(Currency.BOB, "Bolivian boliviano", 2);

    public static final Profile BRL = new Profile(Currency.BRL, "Brazilian real", 2);

    public static final Profile BSD = new Profile(Currency.BSD, "Bahamian dollar", 2);

    public static final Profile BTN = new Profile(Currency.BTN, "Bhutan ngultrum", 2);

    public static final Profile BWP = new Profile(Currency.BWP, "Botswana pula", 2);

    public static final Profile BYN = new Profile(Currency.BYN, "Belarusian ruble", 4);

    public static final Profile BZD = new Profile(Currency.BZD, "Belize dollar", 2);

    public static final Profile CAD = new Profile(Currency.CAD, "Canadian dollar", 2);

    public static final Profile CDF = new Profile(Currency.CDF, "Congolese franc", 2);

    public static final Profile CHF = new Profile(Currency.CHF, "Swiss franc", 2);

    public static final Profile CLP = new Profile(Currency.CLP, "Chilean peso", 0);

    public static final Profile CNY = new Profile(Currency.CNY, "Chinese yuan renminbi", 2);

    public static final Profile COP = new Profile(Currency.COP, "Colombian peso", 2);

    public static final Profile CRC = new Profile(Currency.CRC, "Costa Rican colon", 2);

    public static final Profile CUC = new Profile(Currency.CUC, "Cuban convertible peso", 2);

    public static final Profile CUP = new Profile(Currency.CUP, "Cuban peso", 2);

    public static final Profile CVE = new Profile(Currency.CVE, "Cape Verde escudo", 2);

    public static final Profile CZK = new Profile(Currency.CZK, "Czech koruna", 2);

    public static final Profile DJF = new Profile(Currency.DJF, "Djibouti franc", 0);

    public static final Profile DKK = new Profile(Currency.DKK, "Danish krone", 2);

    public static final Profile DOP = new Profile(Currency.DOP, "Dominican peso", 2);

    public static final Profile DZD = new Profile(Currency.DZD, "Algerian dinar", 2);

    public static final Profile EGP = new Profile(Currency.EGP, "Egyptian pound", 2);

    public static final Profile ERN = new Profile(Currency.ERN, "Eritrean nakfa", 2);

    public static final Profile ETB = new Profile(Currency.ETB, "Ethiopian birr", 2);

    public static final Profile EUR = new Profile(Currency.EUR, "EU euro", 2);

    public static final Profile FJD = new Profile(Currency.FJD, "Fiji dollar", 2);

    public static final Profile FKP = new Profile(Currency.FKP, "Falkland Islands pound", 2);

    public static final Profile GBP = new Profile(Currency.GBP, "British pound", 2);

    public static final Profile GEL = new Profile(Currency.GEL, "Georgian lari", 2);

    public static final Profile GGP = new Profile(Currency.GGP, "Guernsey pound", 4);

    public static final Profile GHS = new Profile(Currency.GHS, "Ghanaian new cedi", 2);

    public static final Profile GIP = new Profile(Currency.GIP, "Gibraltar pound", 2);

    public static final Profile GMD = new Profile(Currency.GMD, "Gambian dalasi", 2);

    public static final Profile GNF = new Profile(Currency.GNF, "Guinean franc", 0);

    public static final Profile GTQ = new Profile(Currency.GTQ, "Guatemalan quetzal", 2);

    public static final Profile GYD = new Profile(Currency.GYD, "Guyana dollar", 2);

    public static final Profile HKD = new Profile(Currency.HKD, "Hong Kong SAR dollar", 2);

    public static final Profile HNL = new Profile(Currency.HNL, "Honduran lempira", 2);

    public static final Profile HRK = new Profile(Currency.HRK, "Croatian kuna", 2);

    public static final Profile HTG = new Profile(Currency.HTG, "Haitian gourde", 2);

    public static final Profile HUF = new Profile(Currency.HUF, "Hungarian forint", 2);

    public static final Profile IDR = new Profile(Currency.IDR, "Indonesian rupiah", 2);

    public static final Profile ILS = new Profile(Currency.ILS, "Israeli new shekel", 2);

    public static final Profile IMP = new Profile(Currency.IMP, "Isle of Man pound", 4);

    public static final Profile INR = new Profile(Currency.INR, "Indian rupee", 2);

    public static final Profile IQD = new Profile(Currency.IQD, "Iraqi dinar", 3);

    public static final Profile IRR = new Profile(Currency.IRR, "Iranian rial", 2);

    public static final Profile ISK = new Profile(Currency.ISK, "Icelandic krona", 0);

    public static final Profile JEP = new Profile(Currency.JEP, "Jersey pound", 4);

    public static final Profile JMD = new Profile(Currency.JMD, "Jamaican dollar", 2);

    public static final Profile JOD = new Profile(Currency.JOD, "Jordanian dinar", 3);

    public static final Profile JPY = new Profile(Currency.JPY, "Japanese yen", 0);

    public static final Profile KES = new Profile(Currency.KES, "Kenyan shilling", 2);

    public static final Profile KGS = new Profile(Currency.KGS, "Kyrgyz som", 2);

    public static final Profile KHR = new Profile(Currency.KHR, "Cambodian riel", 2);

    public static final Profile KMF = new Profile(Currency.KMF, "Comoros franc", 0);

    public static final Profile KPW = new Profile(Currency.KPW, "North Korean won", 2);

    public static final Profile KRW = new Profile(Currency.KRW, "South Korean won", 0);

    public static final Profile KWD = new Profile(Currency.KWD, "Kuwaiti dinar", 3);

    public static final Profile KYD = new Profile(Currency.KYD, "Cayman Islands dollar", 2);

    public static final Profile KZT = new Profile(Currency.KZT, "Kazakh tenge", 2);

    public static final Profile LAK = new Profile(Currency.LAK, "Lao kip", 2);

    public static final Profile LBP = new Profile(Currency.LBP, "Lebanese pound", 2);

    public static final Profile LKR = new Profile(Currency.LKR, "Sri Lanka rupee", 2);

    public static final Profile LRD = new Profile(Currency.LRD, "Liberian dollar", 2);

    public static final Profile LSL = new Profile(Currency.LSL, "Lesotho loti", 2);

    public static final Profile LYD = new Profile(Currency.LYD, "Libyan dinar", 3);

    public static final Profile MAD = new Profile(Currency.MAD, "Moroccan dirham", 2);

    public static final Profile MDL = new Profile(Currency.MDL, "Moldovan leu", 2);

    public static final Profile MGA = new Profile(Currency.MGA, "Malagasy ariary", 2);

    public static final Profile MKD = new Profile(Currency.MKD, "Macedonian denar", 2);

    public static final Profile MMK = new Profile(Currency.MMK, "Myanmar kyat", 2);

    public static final Profile MNT = new Profile(Currency.MNT, "Mongolian tugrik", 2);

    public static final Profile MOP = new Profile(Currency.MOP, "Macao SAR pataca", 2);

    public static final Profile MRO = new Profile(Currency.MRO, "Mauritanian ouguiya", 2);

    public static final Profile MUR = new Profile(Currency.MUR, "Mauritius rupee", 2);

    public static final Profile MVR = new Profile(Currency.MVR, "Maldivian rufiyaa", 2);

    public static final Profile MWK = new Profile(Currency.MWK, "Malawi kwacha", 2);

    public static final Profile MXN = new Profile(Currency.MXN, "Mexican peso", 2);

    public static final Profile MYR = new Profile(Currency.MYR, "Malaysian ringgit", 2);

    public static final Profile MZN = new Profile(Currency.MZN, "Mozambique new metical", 2);

    public static final Profile NAD = new Profile(Currency.NAD, "Namibian dollar", 2);

    public static final Profile NGN = new Profile(Currency.NGN, "Nigerian naira", 2);

    public static final Profile NIO = new Profile(Currency.NIO, "Nicaraguan cordoba oro", 2);

    public static final Profile NOK = new Profile(Currency.NOK, "Norwegian krone", 2);

    public static final Profile NPR = new Profile(Currency.NPR, "Nepalese rupee", 2);

    public static final Profile NZD = new Profile(Currency.NZD, "New Zealand dollar", 2);

    public static final Profile OMR = new Profile(Currency.OMR, "Omani rial", 3);

    public static final Profile PAB = new Profile(Currency.PAB, "Panamanian balboa", 2);

    public static final Profile PEN = new Profile(Currency.PEN, "Peruvian nuevo sol", 2);

    public static final Profile PGK = new Profile(Currency.PGK, "Papua New Guinea kina", 2);

    public static final Profile PHP = new Profile(Currency.PHP, "Philippine peso", 2);

    public static final Profile PKR = new Profile(Currency.PKR, "Pakistani rupee", 2);

    public static final Profile PLN = new Profile(Currency.PLN, "Polish zloty", 2);

    public static final Profile PYG = new Profile(Currency.PYG, "Paraguayan guarani", 0);

    public static final Profile QAR = new Profile(Currency.QAR, "Qatari rial", 2);

    public static final Profile RON = new Profile(Currency.RON, "Romanian new leu", 2);

    public static final Profile RSD = new Profile(Currency.RSD, "Serbian dinar", 2);

    public static final Profile RUB = new Profile(Currency.RUB, "Russian ruble", 2);

    public static final Profile RWF = new Profile(Currency.RWF, "Rwandan franc", 0);

    public static final Profile SAR = new Profile(Currency.SAR, "Saudi riyal", 2);

    public static final Profile SBD = new Profile(Currency.SBD, "Solomon Islands dollar", 2);

    public static final Profile SCR = new Profile(Currency.SCR, "Seychelles rupee", 2);

    public static final Profile SDG = new Profile(Currency.SDG, "Sudanese pound", 2);

    public static final Profile SEK = new Profile(Currency.SEK, "Swedish krona", 2);

    public static final Profile SGD = new Profile(Currency.SGD, "Singapore dollar", 2);

    public static final Profile SHP = new Profile(Currency.SHP, "Saint Helena pound", 2);

    public static final Profile SLL = new Profile(Currency.SLL, "Sierra Leone leone", 2);

    public static final Profile SOS = new Profile(Currency.SOS, "Somali shilling", 2);

    public static final Profile SPL = new Profile(Currency.SPL, "Seborgan luigino", 4);

    public static final Profile SRD = new Profile(Currency.SRD, "Suriname dollar", 2);

    public static final Profile STD = new Profile(Currency.STD, "Sao Tome and Principe dobra", 2);

    public static final Profile SVC = new Profile(Currency.SVC, "El Salvador colon", 2);

    public static final Profile SYP = new Profile(Currency.SYP, "Syrian pound", 2);

    public static final Profile SZL = new Profile(Currency.SZL, "Swaziland lilangeni", 2);

    public static final Profile THB = new Profile(Currency.THB, "Thai baht", 2);

    public static final Profile TJS = new Profile(Currency.TJS, "Tajik somoni", 2);

    public static final Profile TMT = new Profile(Currency.TMT, "Turkmen new manat", 2);

    public static final Profile TND = new Profile(Currency.TND, "Tunisian dinar", 3);

    public static final Profile TOP = new Profile(Currency.TOP, "Tongan pa'anga", 2);

    public static final Profile TRY = new Profile(Currency.TRY, "Turkish lira", 2);

    public static final Profile TTD = new Profile(Currency.TTD, "Trinidad and Tobago dollar", 2);

    public static final Profile TVD = new Profile(Currency.TVD, "Tuvaluan dollar", 4);

    public static final Profile TWD = new Profile(Currency.TWD, "Taiwan New dollar", 2);

    public static final Profile TZS = new Profile(Currency.TZS, "Tanzanian shilling", 2);

    public static final Profile UAH = new Profile(Currency.UAH, "Ukrainian hryvnia", 2);

    public static final Profile UGX = new Profile(Currency.UGX, "Uganda new shilling", 0);

    public static final Profile USD = new Profile(Currency.USD, "US dollar", 2);

    public static final Profile UYU = new Profile(Currency.UYU, "Uruguayan peso uruguayo", 2);

    public static final Profile UZS = new Profile(Currency.UZS, "Uzbekistani sum", 2);

    public static final Profile VEF = new Profile(Currency.VEF, "Venezuelan bolivar fuerte", 2);

    public static final Profile VND = new Profile(Currency.VND, "Vietnamese dong", 0);

    public static final Profile VUV = new Profile(Currency.VUV, "Vanuatu vatu", 0);

    public static final Profile WST = new Profile(Currency.WST, "Samoan tala", 2);

    public static final Profile XAF = new Profile(Currency.XAF, "CFA franc BEAC", 0);

    public static final Profile XCD = new Profile(Currency.XCD, "East Caribbean dollar", 2);

    public static final Profile XDR = new Profile(Currency.XDR, "IMF special drawing right", 4);

    public static final Profile XOF = new Profile(Currency.XOF, "CFA franc BCEAO", 0);

    public static final Profile XPF = new Profile(Currency.XPF, "CFP franc", 0);

    public static final Profile XTS = new Profile(Currency.XTS, "Reserved for testing purposes", 4);

    public static final Profile XXX = new Profile(
        Currency.XXX, "Assigned for transactions where no currency is involved", 4);

    public static final Profile YER = new Profile(Currency.YER, "Yemeni rial", 2);

    public static final Profile ZAR = new Profile(Currency.ZAR, "South African rand", 2);

    public static final Profile ZMW = new Profile(Currency.ZMW, "Zambian kwacha", 2);

    public static final Profile ZWD = new Profile(Currency.ZWD, "Zimbabwe dollar (initial)", 4);

    private static final Map<Currency, Profile> CURRENCIES = new HashMap<>();

    static {
        CURRENCIES.put(Currency.AED, AED);
        CURRENCIES.put(Currency.AFN, AFN);
        CURRENCIES.put(Currency.ALL, ALL);
        CURRENCIES.put(Currency.AMD, AMD);
        CURRENCIES.put(Currency.ANG, ANG);
        CURRENCIES.put(Currency.AOA, AOA);
        CURRENCIES.put(Currency.ARS, ARS);
        CURRENCIES.put(Currency.AUD, AUD);
        CURRENCIES.put(Currency.AWG, AWG);
        CURRENCIES.put(Currency.AZN, AZN);
        CURRENCIES.put(Currency.BAM, BAM);
        CURRENCIES.put(Currency.BBD, BBD);
        CURRENCIES.put(Currency.BDT, BDT);
        CURRENCIES.put(Currency.BGN, BGN);
        CURRENCIES.put(Currency.BHD, BHD);
        CURRENCIES.put(Currency.BIF, BIF);
        CURRENCIES.put(Currency.BMD, BMD);
        CURRENCIES.put(Currency.BND, BND);
        CURRENCIES.put(Currency.BOB, BOB);
        CURRENCIES.put(Currency.BRL, BRL);
        CURRENCIES.put(Currency.BSD, BSD);
        CURRENCIES.put(Currency.BTN, BTN);
        CURRENCIES.put(Currency.BWP, BWP);
        CURRENCIES.put(Currency.BYN, BYN);
        CURRENCIES.put(Currency.BZD, BZD);
        CURRENCIES.put(Currency.CAD, CAD);
        CURRENCIES.put(Currency.CDF, CDF);
        CURRENCIES.put(Currency.CHF, CHF);
        CURRENCIES.put(Currency.CLP, CLP);
        CURRENCIES.put(Currency.CNY, CNY);
        CURRENCIES.put(Currency.COP, COP);
        CURRENCIES.put(Currency.CRC, CRC);
        CURRENCIES.put(Currency.CUC, CUC);
        CURRENCIES.put(Currency.CUP, CUP);
        CURRENCIES.put(Currency.CVE, CVE);
        CURRENCIES.put(Currency.CZK, CZK);
        CURRENCIES.put(Currency.DJF, DJF);
        CURRENCIES.put(Currency.DKK, DKK);
        CURRENCIES.put(Currency.DOP, DOP);
        CURRENCIES.put(Currency.DZD, DZD);
        CURRENCIES.put(Currency.EGP, EGP);
        CURRENCIES.put(Currency.ERN, ERN);
        CURRENCIES.put(Currency.ETB, ETB);
        CURRENCIES.put(Currency.EUR, EUR);
        CURRENCIES.put(Currency.FJD, FJD);
        CURRENCIES.put(Currency.FKP, FKP);
        CURRENCIES.put(Currency.GBP, GBP);
        CURRENCIES.put(Currency.GEL, GEL);
        CURRENCIES.put(Currency.GGP, GGP);
        CURRENCIES.put(Currency.GHS, GHS);
        CURRENCIES.put(Currency.GIP, GIP);
        CURRENCIES.put(Currency.GMD, GMD);
        CURRENCIES.put(Currency.GNF, GNF);
        CURRENCIES.put(Currency.GTQ, GTQ);
        CURRENCIES.put(Currency.GYD, GYD);
        CURRENCIES.put(Currency.HKD, HKD);
        CURRENCIES.put(Currency.HNL, HNL);
        CURRENCIES.put(Currency.HRK, HRK);
        CURRENCIES.put(Currency.HTG, HTG);
        CURRENCIES.put(Currency.HUF, HUF);
        CURRENCIES.put(Currency.IDR, IDR);
        CURRENCIES.put(Currency.ILS, ILS);
        CURRENCIES.put(Currency.IMP, IMP);
        CURRENCIES.put(Currency.INR, INR);
        CURRENCIES.put(Currency.IQD, IQD);
        CURRENCIES.put(Currency.IRR, IRR);
        CURRENCIES.put(Currency.ISK, ISK);
        CURRENCIES.put(Currency.JEP, JEP);
        CURRENCIES.put(Currency.JMD, JMD);
        CURRENCIES.put(Currency.JOD, JOD);
        CURRENCIES.put(Currency.JPY, JPY);
        CURRENCIES.put(Currency.KES, KES);
        CURRENCIES.put(Currency.KGS, KGS);
        CURRENCIES.put(Currency.KHR, KHR);
        CURRENCIES.put(Currency.KMF, KMF);
        CURRENCIES.put(Currency.KPW, KPW);
        CURRENCIES.put(Currency.KRW, KRW);
        CURRENCIES.put(Currency.KWD, KWD);
        CURRENCIES.put(Currency.KYD, KYD);
        CURRENCIES.put(Currency.KZT, KZT);
        CURRENCIES.put(Currency.LAK, LAK);
        CURRENCIES.put(Currency.LBP, LBP);
        CURRENCIES.put(Currency.LKR, LKR);
        CURRENCIES.put(Currency.LRD, LRD);
        CURRENCIES.put(Currency.LSL, LSL);
        CURRENCIES.put(Currency.LYD, LYD);
        CURRENCIES.put(Currency.MAD, MAD);
        CURRENCIES.put(Currency.MDL, MDL);
        CURRENCIES.put(Currency.MGA, MGA);
        CURRENCIES.put(Currency.MKD, MKD);
        CURRENCIES.put(Currency.MMK, MMK);
        CURRENCIES.put(Currency.MNT, MNT);
        CURRENCIES.put(Currency.MOP, MOP);
        CURRENCIES.put(Currency.MRO, MRO);
        CURRENCIES.put(Currency.MUR, MUR);
        CURRENCIES.put(Currency.MVR, MVR);
        CURRENCIES.put(Currency.MWK, MWK);
        CURRENCIES.put(Currency.MXN, MXN);
        CURRENCIES.put(Currency.MYR, MYR);
        CURRENCIES.put(Currency.MZN, MZN);
        CURRENCIES.put(Currency.NAD, NAD);
        CURRENCIES.put(Currency.NGN, NGN);
        CURRENCIES.put(Currency.NIO, NIO);
        CURRENCIES.put(Currency.NOK, NOK);
        CURRENCIES.put(Currency.NPR, NPR);
        CURRENCIES.put(Currency.NZD, NZD);
        CURRENCIES.put(Currency.OMR, OMR);
        CURRENCIES.put(Currency.PAB, PAB);
        CURRENCIES.put(Currency.PEN, PEN);
        CURRENCIES.put(Currency.PGK, PGK);
        CURRENCIES.put(Currency.PHP, PHP);
        CURRENCIES.put(Currency.PKR, PKR);
        CURRENCIES.put(Currency.PLN, PLN);
        CURRENCIES.put(Currency.PYG, PYG);
        CURRENCIES.put(Currency.QAR, QAR);
        CURRENCIES.put(Currency.RON, RON);
        CURRENCIES.put(Currency.RSD, RSD);
        CURRENCIES.put(Currency.RUB, RUB);
        CURRENCIES.put(Currency.RWF, RWF);
        CURRENCIES.put(Currency.SAR, SAR);
        CURRENCIES.put(Currency.SBD, SBD);
        CURRENCIES.put(Currency.SCR, SCR);
        CURRENCIES.put(Currency.SDG, SDG);
        CURRENCIES.put(Currency.SEK, SEK);
        CURRENCIES.put(Currency.SGD, SGD);
        CURRENCIES.put(Currency.SHP, SHP);
        CURRENCIES.put(Currency.SLL, SLL);
        CURRENCIES.put(Currency.SOS, SOS);
        CURRENCIES.put(Currency.SPL, SPL);
        CURRENCIES.put(Currency.SRD, SRD);
        CURRENCIES.put(Currency.STD, STD);
        CURRENCIES.put(Currency.SVC, SVC);
        CURRENCIES.put(Currency.SYP, SYP);
        CURRENCIES.put(Currency.SZL, SZL);
        CURRENCIES.put(Currency.THB, THB);
        CURRENCIES.put(Currency.TJS, TJS);
        CURRENCIES.put(Currency.TMT, TMT);
        CURRENCIES.put(Currency.TND, TND);
        CURRENCIES.put(Currency.TOP, TOP);
        CURRENCIES.put(Currency.TRY, TRY);
        CURRENCIES.put(Currency.TTD, TTD);
        CURRENCIES.put(Currency.TVD, TVD);
        CURRENCIES.put(Currency.TWD, TWD);
        CURRENCIES.put(Currency.TZS, TZS);
        CURRENCIES.put(Currency.UAH, UAH);
        CURRENCIES.put(Currency.UGX, UGX);
        CURRENCIES.put(Currency.USD, USD);
        CURRENCIES.put(Currency.UYU, UYU);
        CURRENCIES.put(Currency.UZS, UZS);
        CURRENCIES.put(Currency.VEF, VEF);
        CURRENCIES.put(Currency.VND, VND);
        CURRENCIES.put(Currency.VUV, VUV);
        CURRENCIES.put(Currency.WST, WST);
        CURRENCIES.put(Currency.XAF, XAF);
        CURRENCIES.put(Currency.XCD, XCD);
        CURRENCIES.put(Currency.XDR, XDR);
        CURRENCIES.put(Currency.XOF, XOF);
        CURRENCIES.put(Currency.XPF, XPF);
        CURRENCIES.put(Currency.XTS, XTS);
        CURRENCIES.put(Currency.XXX, XXX);
        CURRENCIES.put(Currency.YER, YER);
        CURRENCIES.put(Currency.ZAR, ZAR);
        CURRENCIES.put(Currency.ZMW, ZMW);
        CURRENCIES.put(Currency.ZWD, ZWD);
    }

    public static Profile get(Currency currency) {

        return CURRENCIES.get(currency);
    }

    public record Profile(Currency currency, String name, int scale) { }

}
