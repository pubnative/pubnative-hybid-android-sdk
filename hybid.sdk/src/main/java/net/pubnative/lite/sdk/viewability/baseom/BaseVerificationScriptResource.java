package net.pubnative.lite.sdk.viewability.baseom;

import java.net.URL;

public class BaseVerificationScriptResource {
    private final String vendorKey;
    private final URL resourceUrl;
    private final String verificationParameters;

    private BaseVerificationScriptResource(String vendorKey, URL resourceUrl, String verificationParameters) {
        this.vendorKey = vendorKey;
        this.resourceUrl = resourceUrl;
        this.verificationParameters = verificationParameters;
    }

    public static BaseVerificationScriptResource createVerificationScriptResourceWithParameters(String vendorKey, URL resourceUrl, String verificationParameters) {
        return new BaseVerificationScriptResource(vendorKey, resourceUrl, verificationParameters);
    }

    public String getVendorKey() {
        return this.vendorKey;
    }

    public URL getResourceUrl() {
        return this.resourceUrl;
    }

    public String getVerificationParameters() {
        return this.verificationParameters;
    }
}
