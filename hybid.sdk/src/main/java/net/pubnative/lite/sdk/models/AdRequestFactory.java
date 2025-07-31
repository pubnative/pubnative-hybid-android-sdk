// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

public interface AdRequestFactory {

    public interface Callback {
        void onRequestCreated(AdRequest adRequest);
    }

    public void createAdRequest(final String appToken, final String zoneid, final AdSize adSize, final boolean isRewarded, final boolean protectedAudiencesAvailable, final Callback callback);
    public AdRequest buildRequest(final String appToken, final String zoneid, AdSize adSize, final String advertisingId, final boolean limitTracking, final IntegrationType integrationType, final String mediationVendor, Integer impDepth, boolean paAvailable);
    public void setMediationVendor(String mediationVendor);
    public void setIntegrationType(IntegrationType integrationType);
    public void setAdFormat(String adFormat);
}
