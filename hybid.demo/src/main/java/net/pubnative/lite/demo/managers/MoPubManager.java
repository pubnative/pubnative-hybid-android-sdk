// The MIT License (MIT)
//
// Copyright (c) 2020 PubNative GmbH
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
package net.pubnative.lite.demo.managers;

import android.content.Context;

import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.logging.MoPubLog;

import net.pubnative.lite.adapters.mopub.HyBidAdapterConfiguration;

import java.util.HashMap;
import java.util.Map;

public class MoPubManager {
    public static void initMoPubSdk(Context context, String adUnitId, String hyBidAppToken) {
        initMoPubSdk(context, adUnitId, hyBidAppToken, null);
    }

    public static void initMoPubSdk(Context context, String adUnitId, String hyBidAppToken, final InitialisationListener listener) {
        Map<String, String> pubnativeInitConfig = new HashMap<>();
        pubnativeInitConfig.put(HyBidAdapterConfiguration.CONFIG_KEY_APP_TOKEN, hyBidAppToken);

        SdkConfiguration sdkConfiguration = new SdkConfiguration
                .Builder(adUnitId)
                .withLogLevel(MoPubLog.LogLevel.DEBUG)
                .withAdditionalNetwork(HyBidAdapterConfiguration.class.getName())
                .withMediatedNetworkConfiguration(HyBidAdapterConfiguration.class.getName(), pubnativeInitConfig)
                .build();
        MoPub.initializeSdk(context, sdkConfiguration, () -> {
            if (listener != null) {
                listener.onInitialisationFinished();
            }
        });
    }

    public interface InitialisationListener {
        void onInitialisationFinished();
    }
}
