package net.pubnative.lite.sdk.interstitial.vast.model;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public enum VAST_DOC_ELEMENTS {
    vastVersion("2.0"),
    vasts("VASTS"),
    vastAdTagURI("VASTAdTagURI"),
    vastVersionAttribute("version");

    private String value;

    VAST_DOC_ELEMENTS(String value) {
        this.value = value;

    }

    public String getValue() {
        return value;
    }
}
