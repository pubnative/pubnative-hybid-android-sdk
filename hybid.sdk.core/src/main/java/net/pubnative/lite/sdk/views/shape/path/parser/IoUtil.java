package net.pubnative.lite.sdk.views.shape.path.parser;

import net.pubnative.lite.sdk.HyBid;

import java.io.InputStream;

@SuppressWarnings("FinalStaticMethod")
public class IoUtil {
    public static final void closeQuitely(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (Throwable ignored) {
                HyBid.reportException(ignored);
                //ignored
            }
        }
    }
}
