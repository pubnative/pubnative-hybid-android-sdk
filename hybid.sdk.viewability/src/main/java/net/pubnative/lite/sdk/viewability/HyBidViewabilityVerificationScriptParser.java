package net.pubnative.lite.sdk.viewability;

import android.text.TextUtils;

import com.iab.omid.library.pubnativenet.adsession.VerificationScriptResource;

import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONObject;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HyBidViewabilityVerificationScriptParser {
    private static final String TAG = HyBidViewabilityVerificationScriptParser.class.getSimpleName();

    private static final String KEY_VIEWABILITY = "viewability";
    private static final String RESPONSE_KEY_CONFIG = "config";
    private static final Pattern PATTERN_SRC_VALUE = Pattern.compile("src=\"(.*?)\"");
    private static final Pattern PATTERN_VENDORKEY_VALUE = Pattern.compile("vk=(.*?);");
    private static final String KEY_HASH = "#";

    public static VerificationScriptResource parseViewabilityObjectfromAdObject(JSONObject viewabilityObject) {

        if (viewabilityObject == null) {
            return null;
        }

        String configString;
        try {
            configString = viewabilityObject.getString(RESPONSE_KEY_CONFIG);
        } catch (Exception e) {
            configString = "";
        }

        if (TextUtils.isEmpty(configString)) {
            return null;
        }

        VerificationScriptResource omidverificationScriptResource;
        try {
            //Extracts everything in between src=""
            Matcher srcStringMatcher = PATTERN_SRC_VALUE.matcher(configString);
            srcStringMatcher.find(0);
            String src = srcStringMatcher.group(1);

            if (!TextUtils.isEmpty(src)) {
                String[] verificationScriptResource = src.split(KEY_HASH, 2);
                URL url = new URL(verificationScriptResource[0]);
                String params = verificationScriptResource[1];

                Matcher vkStringMatcher = PATTERN_VENDORKEY_VALUE.matcher(params);
                vkStringMatcher.find(0);
                String vendorKey = vkStringMatcher.group(1);
                omidverificationScriptResource =
                        VerificationScriptResource.createVerificationScriptResourceWithParameters(vendorKey,
                                url, params);
            } else {
                omidverificationScriptResource = null;
            }

        } catch (Exception e) {
            Logger.d(TAG, " Exception: " + e.getMessage());
            return null;
        }
        return omidverificationScriptResource;
    }
}
