package net.pubnative.lite.sdk.utils;

public class WrapperURLDigger {

    public WrapperURLDigger() {

    }

    public String getURL(String wrapperURL) {
        String url;
        try {
            url = wrapperURL;
            if (url.contains("\n")) {
                if (url.split("\n").length > 0) {
                    url = url.split("\n")[0];
                } else {
                    url = wrapperURL;
                }
            }
        } catch (RuntimeException ex) {
            url = wrapperURL;
        }

        return url;
    }
}
