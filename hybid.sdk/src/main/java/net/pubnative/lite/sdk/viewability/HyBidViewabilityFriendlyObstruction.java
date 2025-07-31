// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.viewability;

import android.view.View;

import net.pubnative.lite.sdk.viewability.baseom.BaseFriendlyObstructionPurpose;

public class HyBidViewabilityFriendlyObstruction {
    private final View view;
    private final BaseFriendlyObstructionPurpose purpose;
    private final String reason;

    public HyBidViewabilityFriendlyObstruction(View view, BaseFriendlyObstructionPurpose purpose, String reason) {
        this.view = view;
        this.purpose = purpose;
        this.reason = reason;
    }

    public View getView() {
        return view;
    }

    public BaseFriendlyObstructionPurpose getPurpose() {
        return purpose;
    }

    public String getReason() {
        return reason;
    }
}
