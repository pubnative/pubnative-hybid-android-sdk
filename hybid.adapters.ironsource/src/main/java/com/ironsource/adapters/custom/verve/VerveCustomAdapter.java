package com.ironsource.adapters.custom.verve;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors;

import net.pubnative.lite.sdk.HyBid;

public class VerveCustomAdapter extends BaseAdapter {
    public static final String IRONSOURCE_MEDIATION_VENDOR = "is";
    public static final String KEY_APP_TOKEN = "appToken";
    public static final String KEY_ZONE_ID = "zoneId";

    private static final String NETWORK_SDK_VERSION = "2.14.0";
    private static final String ADAPTER_VERSION = NETWORK_SDK_VERSION + ".0";

    @Override
    public void init(AdData adData, Context context, final NetworkInitializationListener networkInitializationListener) {
        String appToken = adData.getString(KEY_APP_TOKEN);
        if (TextUtils.isEmpty(appToken)) {
            if (networkInitializationListener != null) {
                networkInitializationListener.onInitFailed(AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS,
                        "HyBid initialisation failed: Missing app token");
            }
        } else {
            if (!HyBid.isInitialized()) {
                HyBid.initialize(appToken, (Application) context.getApplicationContext(), new HyBid.InitialisationListener() {
                    @Override
                    public void onInitialisationFinished(boolean success) {
                        if (networkInitializationListener != null) {
                            networkInitializationListener.onInitSuccess();
                        }
                    }
                });
            } else {
                if (networkInitializationListener != null) {
                    networkInitializationListener.onInitSuccess();
                }
            }
        }
    }

    @Override
    public String getNetworkSDKVersion() {
        return NETWORK_SDK_VERSION;
    }

    @Override
    public String getAdapterVersion() {
        return ADAPTER_VERSION;
    }
}
