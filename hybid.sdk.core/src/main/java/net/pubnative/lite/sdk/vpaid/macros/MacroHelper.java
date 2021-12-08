package net.pubnative.lite.sdk.vpaid.macros;

import android.text.TextUtils;

public class MacroHelper {
    private final GenericMacros genericMacros;
    private final AdBreakMacros adBreakMacros;
    private final ClientMacros clientMacros;
    private final PublisherMacros publisherMacros;
    private final CapabilitiesMacros capabilitiesMacros;
    private final PlayerStateMacros playerStateMacros;
    private final ClickMacros clickMacros;
    private final ErrorMacros errorMacros;
    private final VerificationMacros verificationMacros;
    private final RegulationMacros regulationMacros;

    public MacroHelper() {
        this.genericMacros = new GenericMacros();
        this.adBreakMacros = new AdBreakMacros();
        this.clientMacros = new ClientMacros();
        this.publisherMacros = new PublisherMacros();
        this.capabilitiesMacros = new CapabilitiesMacros();
        this.playerStateMacros = new PlayerStateMacros();
        this.clickMacros = new ClickMacros();
        this.errorMacros = new ErrorMacros();
        this.verificationMacros = new VerificationMacros();
        this.regulationMacros = new RegulationMacros();
    }

    public String processUrl(String url) {
        return processUrl(url, "");
    }

    public String processUrl(String url, String errorCode) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }

        String finalUrl = url;
        finalUrl = genericMacros.processUrl(finalUrl);
        finalUrl = adBreakMacros.processUrl(finalUrl);
        finalUrl = clientMacros.processUrl(finalUrl);
        finalUrl = publisherMacros.processUrl(finalUrl);
        finalUrl = capabilitiesMacros.processUrl(finalUrl);
        finalUrl = playerStateMacros.processUrl(finalUrl);
        finalUrl = clickMacros.processUrl(finalUrl);
        finalUrl = errorMacros.processUrl(finalUrl, errorCode);
        finalUrl = verificationMacros.processUrl(finalUrl);
        finalUrl = regulationMacros.processUrl(finalUrl);
        return finalUrl;
    }
}
