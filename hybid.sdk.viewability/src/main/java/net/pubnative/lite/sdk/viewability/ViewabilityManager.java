package net.pubnative.lite.sdk.viewability;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;

import com.iab.omid.library.pubnativenet.Omid;
import com.iab.omid.library.pubnativenet.adsession.Partner;

import net.pubnative.lite.sdk.analytics.ReportingController;

public class ViewabilityManager {

    private static final String TAG = ViewabilityManager.class.getSimpleName();
    private static final String VIEWABILITY_PARTNER_NAME = "Pubnativenet";
    private static String VIEWABILITY_JS_SERVICE_CONTENT;

    private static Partner mPubNativePartner = null;
    private final ReportingController reportingController;

    private boolean mShouldMeasureViewability = true;

    public ViewabilityManager(final Application application, ReportingController reportingController) {
        this.reportingController = reportingController;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!Omid.isActive()) {
                        Omid.activate(application);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

                if (Omid.isActive() && mPubNativePartner == null) {
                    try {
                        mPubNativePartner = Partner.createPartner(VIEWABILITY_PARTNER_NAME, BuildConfig.SDK_VERSION);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }

                if (TextUtils.isEmpty(VIEWABILITY_JS_SERVICE_CONTENT)) {
                    String omsdkStr = Assets.omsdkjs;
                    byte[] omsdkBytes = Base64.decode(omsdkStr, Base64.DEFAULT);
                    VIEWABILITY_JS_SERVICE_CONTENT = new String(omsdkBytes);
                }
            }
        });
    }

    public boolean isViewabilityMeasurementActivated() {
        return Omid.isActive() && mShouldMeasureViewability;
    }

    public Partner getPartner() {
        return mPubNativePartner;
    }

    public void setViewabilityMeasurementEnabled(boolean shouldMeasureVisibility) {
        this.mShouldMeasureViewability = shouldMeasureVisibility;
    }

    public boolean isViewabilityMeasurementEnabled() {
        return mShouldMeasureViewability;
    }

    public String getServiceJs() {
        return VIEWABILITY_JS_SERVICE_CONTENT;
    }

    public ReportingController getReportingController() {
        return reportingController;
    }
}
