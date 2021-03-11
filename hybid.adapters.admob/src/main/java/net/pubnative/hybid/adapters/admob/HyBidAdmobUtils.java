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
