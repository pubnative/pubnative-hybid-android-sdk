// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;

import net.pubnative.lite.sdk.api.BannerRequestManager;
import net.pubnative.lite.sdk.api.OpenRTBApiClient;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.OpenRTBAdRequestFactory;

public class HyBidBannerAdView extends HyBidAdView {

    public HyBidBannerAdView(Context context) {
        super(context);
    }

    public HyBidBannerAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HyBidBannerAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public HyBidBannerAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected String getLogTag() {
        return HyBidBannerAdView.class.getSimpleName();
    }

    @Override
    RequestManager getRequestManager() {
        return new BannerRequestManager();
    }

    @Override
    RequestManager getORTBRequestManager(){
        return new BannerRequestManager(new OpenRTBApiClient(getContext()), new OpenRTBAdRequestFactory());
    }
}
