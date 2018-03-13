package net.pubnative.lite.adapters.dfp;

import android.os.Bundle;
import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 13.03.18.
 */

public class PNLiteDFPUtils {
    private static final String TAG = PNLiteDFPUtils.class.getSimpleName();

    private static final String ZONE_ID = "pn_zone_id";

    public static String getZoneId(String extras) {

        return getParameter(extras, ZONE_ID);
    }

    public static String getZoneId(Bundle extras) {

        return getParameter(extras, ZONE_ID);
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
