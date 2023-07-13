package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class Video extends JsonModel {
    @BindField
    private List<String> mimes;
    @BindField
    private Integer minduration;
    @BindField
    private Integer maxduration;
    @BindField
    private List<Integer> protocols;
    @BindField
    private Integer protocol;
    @BindField
    private Integer w;
    @BindField
    private Integer h;
    @BindField
    private Integer startdelay;
    @BindField
    private Integer placement;
    @BindField
    private Integer linearity;
    @BindField
    private Integer skip;
    @BindField
    private Integer skipmin = 0;
    @BindField
    private Integer skipafter = 0;
    @BindField
    private Integer sequence;
    @BindField
    private List<Integer> battr;
    @BindField
    private Integer maxextended;
    @BindField
    private Integer minbitrate;
    @BindField
    private Integer maxbitrate;
    @BindField
    private Integer boxingallowed = 1;
    @BindField
    private List<Integer> playbackmethod;
    @BindField
    private Integer playbackend;
    @BindField
    private List<Integer> delivery;
    @BindField
    private Integer pos;
    @BindField
    private List<Banner> companionad;
    @BindField
    private List<Integer> api;
    @BindField
    private List<Integer> companiontype;

    public Video() {
    }

    public Video(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public List<String> getMimes() {
        return mimes;
    }

    public void setMimes(List<String> mimes) {
        this.mimes = mimes;
    }

    public Integer getMinDuration() {
        return minduration;
    }

    public void setMinDuration(Integer minDuration) {
        this.minduration = minDuration;
    }

    public Integer getMaxDuration() {
        return maxduration;
    }

    public void setMaxDuration(Integer maxDuration) {
        this.maxduration = maxDuration;
    }

    public List<Integer> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<Integer> protocols) {
        this.protocols = protocols;
    }

    public Integer getProtocol() {
        return protocol;
    }

    public void setProtocol(Integer protocol) {
        this.protocol = protocol;
    }

    public Integer getWidth() {
        return w;
    }

    public void setWidth(Integer w) {
        this.w = w;
    }

    public Integer getHeight() {
        return h;
    }

    public void setHeight(Integer h) {
        this.h = h;
    }

    public Integer getStartDelay() {
        return startdelay;
    }

    public void setStartDelay(Integer startDelay) {
        this.startdelay = startDelay;
    }

    public Integer getPlacement() {
        return placement;
    }

    public void setPlacement(Integer placement) {
        this.placement = placement;
    }

    public Integer getLinearity() {
        return linearity;
    }

    public void setLinearity(Integer linearity) {
        this.linearity = linearity;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public Integer getSkipMin() {
        return skipmin;
    }

    public void setSkipMin(Integer skipMin) {
        this.skipmin = skipMin;
    }

    public Integer getSkipAfter() {
        return skipafter;
    }

    public void setSkipAfter(Integer skipAfter) {
        this.skipafter = skipAfter;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public List<Integer> getBlockedAttr() {
        return battr;
    }

    public void setBlockedAttr(List<Integer> bAttr) {
        this.battr = bAttr;
    }

    public Integer getMaxExtended() {
        return maxextended;
    }

    public void setMaxExtended(Integer maxExtended) {
        this.maxextended = maxExtended;
    }

    public Integer getMinBitRate() {
        return minbitrate;
    }

    public void setMinBitRate(Integer minBitRate) {
        this.minbitrate = minBitRate;
    }

    public Integer getMaxBitRate() {
        return maxbitrate;
    }

    public void setMaxBitRate(Integer maxBitRate) {
        this.maxbitrate = maxBitRate;
    }

    public Integer getBoxingAllowed() {
        return boxingallowed;
    }

    public void setBoxingAllowed(Integer boxingAllowed) {
        this.boxingallowed = boxingAllowed;
    }

    public List<Integer> getPlaybackMethod() {
        return playbackmethod;
    }

    public void setPlaybackMethod(List<Integer> playbackMethod) {
        this.playbackmethod = playbackMethod;
    }

    public Integer getPlaybackEnd() {
        return playbackend;
    }

    public void setPlaybackEnd(Integer playbackEnd) {
        this.playbackend = playbackEnd;
    }

    public List<Integer> getDelivery() {
        return delivery;
    }

    public void setDelivery(List<Integer> delivery) {
        this.delivery = delivery;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public List<Banner> getCompanionAds() {
        return companionad;
    }

    public void setCompanionAds(List<Banner> companionAds) {
        this.companionad = companionAds;
    }

    public List<Integer> getApi() {
        return api;
    }

    public void setApi(List<Integer> api) {
        this.api = api;
    }

    public List<Integer> getCompanionType() {
        return companiontype;
    }

    public void setCompanionTypes(List<Integer> companionTypes) {
        this.companiontype = companionTypes;
    }
}
