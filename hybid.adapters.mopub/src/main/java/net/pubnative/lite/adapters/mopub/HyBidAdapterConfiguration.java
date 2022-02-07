// The MIT License (MIT)
//
// Copyright (c) 2019 PubNative GmbH
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
package net.pubnative.lite.adapters.mopub;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.HyBid;

import java.util.Map;

public class HyBidAdapterConfiguration extends BaseAdapterConfiguration {

    private static final String NETWORK_NAME = "pubnative";
    private static final String NETWORK_SDK_VERSION = "2.11.0";
    private static final String ADAPTER_VERSION = NETWORK_SDK_VERSION + ".0";

    public static final String CONFIG_KEY_APP_TOKEN = "pubnative_app_token";


    @NonNull
    @Override
    public String getAdapterVersion() {
        return ADAPTER_VERSION;
    }

    @Nullable
    @Override
    public String getBiddingToken(@NonNull Context context) {
        return null;
    }

    @NonNull
    @Override
    public String getMoPubNetworkName() {
        return NETWORK_NAME;
    }

    @NonNull
    @Override
    public String getNetworkSdkVersion() {
        return NETWORK_SDK_VERSION;
    }

    @Override
    public void initializeNetwork(@NonNull Context context, @Nullable Map<String, String> configuration, @NonNull final OnNetworkInitializationFinishedListener listener) {
        if (configuration != null && configuration.containsKey(CONFIG_KEY_APP_TOKEN)) {
            String appToken = configuration.get(CONFIG_KEY_APP_TOKEN);
            HyBid.initialize(appToken, (Application) context.getApplicationContext(), new HyBid.InitialisationListener() {
                @Override
                public void onInitialisationFinished(boolean success) {
                    listener.onNetworkInitializationFinished(HyBidAdapterConfiguration.class, MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
                }
            });
        } else {
            listener.onNetworkInitializationFinished(HyBidAdapterConfiguration.class, MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }
    }
}
