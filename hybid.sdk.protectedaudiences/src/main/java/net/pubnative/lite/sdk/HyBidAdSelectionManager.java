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
package net.pubnative.lite.sdk;

import android.adservices.adselection.AdSelectionManager;
import android.adservices.common.AdTechIdentifier;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.ext.SdkExtensions;

import net.pubnative.lite.sdk.models.BuyerSignals;
import net.pubnative.lite.sdk.wrappers.AdSelectionWrapper;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HyBidAdSelectionManager {
    private final String TAG = HyBidAdSelectionManager.class.getSimpleName();

    private AdSelectionWrapper mAdSelectionWrapper;
    private final boolean mIsApiAvailable;
    private static final Executor EXECUTOR = Executors.newCachedThreadPool();

    public HyBidAdSelectionManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && SdkExtensions.getExtensionVersion(SdkExtensions.AD_SERVICES) >= 4) {
            mIsApiAvailable = context.getSystemService(AdSelectionManager.class) != null;
        } else {
            mAdSelectionWrapper = null;
            mIsApiAvailable = false;
        }
    }

    public void initialise(Context context, List<AdTechIdentifier> buyers, AdTechIdentifier seller, Uri decisionUri, Uri trustedDataUri, BuyerSignals buyerSignals) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && SdkExtensions.getExtensionVersion(SdkExtensions.AD_SERVICES) >= 4) {
            mAdSelectionWrapper = new AdSelectionWrapper(buyers, seller, decisionUri, trustedDataUri, buyerSignals, context, EXECUTOR);
        }
    }

    public boolean isApiAvailable() {
        return mIsApiAvailable;
    }
}
