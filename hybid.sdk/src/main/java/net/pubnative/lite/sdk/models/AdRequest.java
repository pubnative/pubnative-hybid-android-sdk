// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.models.bidstream.Signal;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by erosgarciaponte on 10.01.18.
 */

public class AdRequest extends JsonModel {
    public String appToken;
    public String zoneId;
    public Boolean isInterstitial = false;
    /**
     * @deprecated The AdServices Topics API integration has been removed.
     * This field is no longer populated by the SDK nor sent in the ad request;
     * it is retained only for source compatibility and will be removed in a future major release.
     */
    @Deprecated
    public List<Topic> topics;
    private final List<Signal> signals = new CopyOnWriteArrayList<>();
    public void addSignal(Signal signal){
        signals.add(signal);
    }
    public List<Signal> getSignals(){
        return signals;
    }
}
