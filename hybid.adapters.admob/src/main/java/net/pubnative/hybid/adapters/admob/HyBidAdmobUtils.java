// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.hybid.adapters.admob;

import android.os.Bundle;
import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class HyBidAdmobUtils {
    private static final String TAG = HyBidAdmobUtils.class.getSimpleName();

    private static final String APP_TOKEN_KEY = "pn_app_token";
    private static final String ZONE_ID_KEY = "pn_zone_id";
    private static final String ECPM_KEY = "cpm";


    public static String getAppToken(String extras) {

        return getParameter(extras, APP_TOKEN_KEY);
    }

    public static String getAppToken(Bundle extras) {

        return getParameter(extras, APP_TOKEN_KEY);
    }

    public static String getZoneId(String extras) {

        return getParameter(extras, ZONE_ID_KEY);
    }

    public static String getZoneId(Bundle extras) {

        return getParameter(extras, ZONE_ID_KEY);
    }

    public static String getEcpm(String extras) {

        return getParameter(extras, ECPM_KEY);
    }

    public static String getEcpm(Bundle extras) {

        return getParameter(extras, ECPM_KEY);
    }

    private static String getParameter(String extras, String parameter) {

        String result = null;
        if (!TextUtils.isEmpty(extras) && !TextUtils.isEmpty(parameter)) {
            try {
                JSONObject object = new JSONObject(extras);

                String value = object.getString(parameter);

                if (!TextUtils.isEmpty(value)) {
                    result = value;
                }

            } catch (JSONException exception) {
                Logger.e(TAG, exception.getMessage());
            }
        }
        return result;
    }

    private static String getParameter(Bundle extras, String parameter) {

        String result = null;
        if (extras != null && extras.containsKey(parameter)) {
            String parameterObject = extras.getString(parameter);
            if (!TextUtils.isEmpty(parameterObject)) {
                result = parameterObject;
            }
        }
        return result;
    }
}
