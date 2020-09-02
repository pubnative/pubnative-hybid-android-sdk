package net.pubnative.lite.sdk.api;


import java.util.ArrayList;
import java.util.List;

public class NativeRequestManager extends RequestManager {
    @Override
    protected String getAdSize() {
        return null;
    }

    @Override
    protected List<String> getSupportedFrameworks(){
        return new ArrayList();
    }
}
