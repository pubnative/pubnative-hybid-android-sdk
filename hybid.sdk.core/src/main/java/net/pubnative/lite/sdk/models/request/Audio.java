package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class Audio extends JsonModel {
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
    private Integer startdelay;
    @BindField
    private List<Integer> battr;
    @BindField
    private Integer maxextended;
    @BindField
    private Integer minbitrate;
    @BindField
    private Integer maxbitrate;
    @BindField
    private List<Integer> delivery;
    @BindField
    private List<Banner> companionad;
    @BindField
    private List<Integer> api;
    @BindField
    private List<Integer> companiontype;
    @BindField
    private Integer maxseq;
    @BindField
    private Integer feed;
    @BindField
    private Integer stitched;
    @BindField
    private Integer nvol;

    public Audio() {
    }

    public Audio(JSONObject jsonObject) throws Exception {
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

    public Integer getStartDelay() {
        return startdelay;
    }

    public void setStartDelay(Integer startDelay) {
        this.startdelay = startDelay;
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

    public List<Integer> getDelivery() {
        return delivery;
    }

    public void setDelivery(List<Integer> delivery) {
        this.delivery = delivery;
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

    public List<Integer> getCompanionTypes() {
        return companiontype;
    }

    public void setCompanionTypes(List<Integer> companionTypes) {
        this.companiontype = companionTypes;
    }

    public Integer getMaxSeq() {
        return maxseq;
    }

    public void setMaxSeq(Integer maxSeq) {
        this.maxseq = maxSeq;
    }

    public Integer getFeed() {
        return feed;
    }

    public void setFeed(Integer feed) {
        this.feed = feed;
    }

    public Integer getStitched() {
        return stitched;
    }

    public void setStitched(Integer stitched) {
        this.stitched = stitched;
    }

    public Integer getVolumeNormalizationMode() {
        return nvol;
    }

    public void setVolumeNormalizationMode(Integer mode) {
        this.nvol = mode;
    }
}
