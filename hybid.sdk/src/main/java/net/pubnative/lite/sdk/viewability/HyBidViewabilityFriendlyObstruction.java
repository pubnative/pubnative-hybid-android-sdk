package net.pubnative.lite.sdk.viewability;

import android.view.View;

import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;

public class HyBidViewabilityFriendlyObstruction {
    private final View view;
    private final FriendlyObstructionPurpose purpose;
    private final String reason;

    public HyBidViewabilityFriendlyObstruction(View view, FriendlyObstructionPurpose purpose, String reason) {
        this.view = view;
        this.purpose = purpose;
        this.reason = reason;
    }

    public View getView() {
        return view;
    }

    public FriendlyObstructionPurpose getPurpose() {
        return purpose;
    }

    public String getReason() {
        return reason;
    }
}
