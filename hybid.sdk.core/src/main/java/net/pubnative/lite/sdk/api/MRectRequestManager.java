// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.api;

import net.pubnative.lite.sdk.models.AdRequestFactory;
import net.pubnative.lite.sdk.models.AdSize;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class MRectRequestManager extends RequestManager {

    public MRectRequestManager(){
        super();
    }

    public MRectRequestManager(ApiClient apiClient, AdRequestFactory requestFactory) {
        super(apiClient, requestFactory);
    }

    @Override
    public AdSize getAdSize() {
        return AdSize.SIZE_300x250;
    }

}
