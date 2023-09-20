package net.pubnative.lite.sdk.models.bidstream;

import java.util.ArrayList;
import java.util.List;

public class ImpressionVideo extends Signal {
    @BidParam(name = "videomimes")
    public final List<String> mimes;
    @BidParam(name = "placement")
    public final Integer placement;
    @BidParam(name = "plcmt")
    public final Integer plcmt;
    @BidParam(name = "linearity")
    public final int linearity;
    @BidParam(name = "boxingallowed")
    public final int boxingallowed;
    @BidParam(name = "playbackmethod")
    public final List<Integer> playbackmethod;
    @BidParam(name = "playbackend")
    public final int playbackend;
    @BidParam(name = "clktype")
    public final int clktype;
    @BidParam(name = "delivery")
    public final List<Integer> delivery;
    @BidParam(name = "videopos")
    public final int pos;
    @BidParam(name = "mraidendcard")
    public final boolean mraidendcard;

    public ImpressionVideo(Integer placement, Integer placementSubtype, int pos, List<Integer> playbackMethods) {
        this.pos = pos;
        this.placement = placement;
        this.plcmt = placementSubtype;
        this.playbackmethod = playbackMethods;
        mimes = new ArrayList<>();
        mimes.add("video/mp4");
        mimes.add("video/webm");

        this.boxingallowed = 0; // No boxing
        this.linearity = 1; // Linear
        this.playbackend = 1; // Video finish or user action
        this.mraidendcard = true;
        this.clktype = 3; // Native browser
        this.delivery = new ArrayList<>();
        this.delivery.add(3); // Download
    }
}
