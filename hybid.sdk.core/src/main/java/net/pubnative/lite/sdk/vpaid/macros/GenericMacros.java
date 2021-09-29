package net.pubnative.lite.sdk.vpaid.macros;

import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.EncodingUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class GenericMacros {
    protected static final String MACRO_TIMESTAMP = "[TIMESTAMP]";
    protected static final String MACRO_CACHE_BUSTING = "[CACHEBUSTING]";

    public String processUrl(String url) {
        return url
                .replace(MACRO_TIMESTAMP, getTimestamp())
                .replace(MACRO_CACHE_BUSTING, getCacheBusting());
    }

    private String getTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        String formattedDate = dateFormat.format(new Date());
        String encoded = EncodingUtils.urlEncode(formattedDate);
        return TextUtils.isEmpty(encoded) ? String.valueOf(MacroDefaultValues.VALUE_UNKNOWN) : encoded;
    }

    private String getCacheBusting() {
        Random random = new Random();
        long cacheNumber = 10000000 + random.nextInt(90000000);
        return String.valueOf(cacheNumber);
    }
}
