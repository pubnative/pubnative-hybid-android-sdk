// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.utils;

import android.os.Bundle;

import net.pubnative.lite.sdk.models.Ad;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by erosgarciaponte on 11.01.18.
 */

public final class PrebidUtils {
    private static final double ECPM_POINTS_DIVIDER = 1000.0;

    public interface KEYS {
        String PN_BID = "pn_bid";
    }

    public static String getPrebidKeywords(Ad ad) {
        return getPrebidKeywords(ad, "");
    }

    public static String getPrebidKeywords(Ad ad, String zoneId) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(KEYS.PN_BID).append(':').append(getBidECPM(ad));

        return stringBuilder.toString();
    }

    public static Bundle getPrebidKeywordsBundle(Ad ad) {
        return getPrebidKeywordsBundle(ad, "");
    }

    public static Bundle getPrebidKeywordsBundle(Ad ad, String zoneid) {
        Bundle bundle = new Bundle();

        bundle.putString(KEYS.PN_BID, getBidECPM(ad));

        return bundle;
    }

    public static Set<String> getPrebidKeywordsSet(Ad ad) {
        return getPrebidKeywordsSet(ad, "");
    }

    public static Set<String> getPrebidKeywordsSet(Ad ad, String zoneid) {
        Set<String> set = new LinkedHashSet<>(3);

        set.add(KEYS.PN_BID.concat(":").concat(getBidECPM(ad)));

        return set;
    }

    private static String getBidECPM(Ad ad) {
        Double eCPM = ad.getECPM().doubleValue() / ECPM_POINTS_DIVIDER;
        return String.format(Locale.ENGLISH, "%.3f", eCPM);
    }
}
