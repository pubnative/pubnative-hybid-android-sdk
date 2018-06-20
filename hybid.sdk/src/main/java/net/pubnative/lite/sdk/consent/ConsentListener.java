package net.pubnative.lite.sdk.consent;

public interface ConsentListener {
    void onConsentAccepted();
    void onConsentRejected();
    void onConsentClosed();
}
