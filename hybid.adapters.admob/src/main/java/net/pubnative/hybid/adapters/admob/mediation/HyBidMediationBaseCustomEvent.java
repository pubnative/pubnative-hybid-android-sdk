// The MIT License (MIT)
//
// Copyright (c) 2023 PubNative GmbH
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
package net.pubnative.hybid.adapters.admob.mediation;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.VersionInfo;
import com.google.android.gms.ads.mediation.Adapter;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;

import java.util.List;

public class HyBidMediationBaseCustomEvent extends Adapter {
    private static final String TAG = HyBidMediationBaseCustomEvent.class.getSimpleName();
    private static final int SDK_VERSION_MAJOR = 2;
    private static final int SDK_VERSION_MINOR = 21;
    private static final int SDK_VERSION_MICRO = 0;

    @Override
    public void initialize(@NonNull Context context, @NonNull InitializationCompleteCallback initializationCompleteCallback, @NonNull List<MediationConfiguration> list) {
        initializationCompleteCallback.onInitializationSucceeded();
    }

    @NonNull
    @Override
    public VersionInfo getSDKVersionInfo() {
        return new VersionInfo(SDK_VERSION_MAJOR, SDK_VERSION_MINOR, SDK_VERSION_MICRO);
    }

    @NonNull
    @Override
    public VersionInfo getVersionInfo() {
        return new VersionInfo(SDK_VERSION_MAJOR, SDK_VERSION_MINOR, SDK_VERSION_MICRO);
    }
}
