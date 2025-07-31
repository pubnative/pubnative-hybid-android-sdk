// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CountryUtils {
    private static final Set<String> GDPR_COUNTRIES;

    static {
        GDPR_COUNTRIES = new HashSet<>();

        GDPR_COUNTRIES.addAll(Arrays.asList(
                "BE", "EL", "LT", "PT", "BG", "ES", "LU", "RO", "CZ", "FR", "HU", "SI", "DK", "HR",
                "MT", "SK", "DE", "IT", "NL", "FI", "EE", "CY", "AT", "SE", "IE", "LV", "PL", "UK",
                "GB", "CH", "NO", "IS", "LI"));
    }

    public static boolean isGDPRCountry(String countryCode) {
        String code = countryCode.toUpperCase(Locale.ENGLISH);
        return GDPR_COUNTRIES.contains(code);
    }
}
