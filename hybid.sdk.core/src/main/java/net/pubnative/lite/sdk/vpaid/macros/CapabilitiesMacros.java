package net.pubnative.lite.sdk.vpaid.macros;

import com.iab.omid.library.pubnativenet.adsession.Partner;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Api;
import net.pubnative.lite.sdk.models.Protocol;
import net.pubnative.lite.sdk.utils.EncodingUtils;
import net.pubnative.lite.sdk.viewability.ViewabilityManager;

import java.util.Locale;

public class CapabilitiesMacros {
    private static final String MACRO_VAST_VERSIONS = "[VASTVERSIONS]";
    private static final String MACRO_API_FRAMEWORKS = "[APIFRAMEWORKS]";
    private static final String MACRO_EXTENSIONS = "[EXTENSIONS]";
    private static final String MACRO_VERIFICATION_VENDORS = "[VERIFICATIONVENDORS]";
    private static final String MACRO_OMID_PARTNER = "[OMIDPARTNER]";
    private static final String MACRO_MEDIA_MIME = "[MEDIAMIME]";
    private static final String MACRO_PLAYER_CAPABILITIES = "[PLAYERCAPABILITIES]";
    private static final String MACRO_CLICK_TYPE = "[CLICKTYPE]";

    private final String mSupportedVastVersions;
    private final String mSupportedApiFrameworks;
    private final String mPlayerCapabilities;
    private final String mOmidPartner;

    public CapabilitiesMacros() {
        this(HyBid.getViewabilityManager());
    }

    CapabilitiesMacros(ViewabilityManager viewabilityManager) {
        // All VAST versions
        mSupportedVastVersions = Protocol.VAST_1_0 + ',' +
                Protocol.VAST_2_0 + ',' +
                Protocol.VAST_3_0 + ',' +
                Protocol.VAST_1_0_WRAPPER + ',' +
                Protocol.VAST_2_0_WRAPPER + ',' +
                Protocol.VAST_3_0_WRAPPER + ',' +
                Protocol.VAST_4_0 + ',' +
                Protocol.VAST_4_0_WRAPPER + ',' +
                Protocol.VAST_4_1 + ',' +
                Protocol.VAST_4_1_WRAPPER + ',' +
                Protocol.VAST_4_2 + ',' +
                Protocol.VAST_4_2_WRAPPER;

        //Supported API frameworks
        mSupportedApiFrameworks = Api.MRAID_1 + ',' +
                Api.MRAID_2 + ',' +
                Api.MRAID_3 + ',' +  //MRAID 3.0
                Api.OMID_1 + ',';

        mPlayerCapabilities = "skip" + ',' +
                "mute" + ',' +
                "autoplay" + ',' +
                "mautoplay";

        if (viewabilityManager != null && viewabilityManager.getPartner() != null) {
            Partner partner = viewabilityManager.getPartner();
            mOmidPartner = EncodingUtils.urlEncode(String.format(Locale.ENGLISH, "%s/%s", partner.getName(), partner.getVersion()));
        } else {
            mOmidPartner = String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
        }
    }

    public String processUrl(String url) {
        return url
                .replace(MACRO_VAST_VERSIONS, getVastVersions())
                .replace(MACRO_API_FRAMEWORKS, getApiFrameworks())
                .replace(MACRO_EXTENSIONS, getExtensions())
                .replace(MACRO_VERIFICATION_VENDORS, getVerificationVendors())
                .replace(MACRO_OMID_PARTNER, getOmidPartner())
                .replace(MACRO_PLAYER_CAPABILITIES, getPlayerCapabilities())
                .replace(MACRO_CLICK_TYPE, getClickType());
    }

    private String getVastVersions() {
        return mSupportedVastVersions;
    }

    private String getApiFrameworks() {
        return mSupportedApiFrameworks;
    }

    private String getExtensions() {
        return "AdVerifications";
    }

    private String getVerificationVendors() {
        return "iabtechlab.com-omid";
    }

    private String getOmidPartner() {
        return mOmidPartner;
    }

    private String getMediaMime() {
        return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
    }

    private String getPlayerCapabilities() {
        return mPlayerCapabilities;
    }

    private String getClickType() {
        // Clickable on full video area
        return "1";
    }
}
