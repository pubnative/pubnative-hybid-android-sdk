package net.pubnative.lite.sdk.vpaid.response;

import net.pubnative.lite.sdk.vpaid.models.vast.Tracking;

import java.util.ArrayList;
import java.util.List;

public class AdParams {

    private String id;
    private int duration;
    private String endCardRedirectUrl;
    private String videoRedirectUrl;
    private boolean vpaid;
    private String adParams;
    private String vpaidJsUrl;
    private String skipTime;

    private List<String> videoFileUrlsList = new ArrayList<>();
    private List<String> endCardUrlList = new ArrayList<>();

    private List<String> impressions = new ArrayList<>();
    private List<String> companionCreativeViewEvents = new ArrayList<>();
    private List<String> videoClicks = new ArrayList<>();
    private List<String> endCardClicks = new ArrayList<>();
    private List<Tracking> events = new ArrayList<>();

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setEndCardRedirectUrl(String endCardRedirectUrl) {
        this.endCardRedirectUrl = endCardRedirectUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEndCardRedirectUrl() {
        return endCardRedirectUrl;
    }

    public boolean isVpaid() {
        return vpaid;
    }

    public String getAdParams() {
        return adParams;
    }

    public String getVpaidJsUrl() {
        return vpaidJsUrl;
    }

    public void setVpaid() {
        this.vpaid = true;
    }

    public void setAdParams(String adParams) {
        this.adParams = adParams;
    }

    public void setVpaidJsUrl(String vpaidJsUrl) {
        this.vpaidJsUrl = vpaidJsUrl;
    }

    public List<String> getImpressions() {
        return impressions;
    }

    public void setImpressions(List<String> impressions) {
        this.impressions = impressions;
    }

    public List<String> getCompanionCreativeViewEvents() {
        return companionCreativeViewEvents;
    }

    public void setCompanionCreativeViewEvents(List<String> companionCreativeViewEvents) {
        this.companionCreativeViewEvents = companionCreativeViewEvents;
    }

    public List<Tracking> getEvents() {
        return events;
    }

    public void setEvents(List<Tracking> events) {
        this.events = events;
    }

    public void addEvents(List<Tracking> events) {
        if (this.events != null && events != null)
        {
            this.events.addAll(events);
        }
    }

    public List<String> getVideoClicks() {
        return videoClicks;
    }

    public void setVideoClicks(List<String> videoClicks) {
        this.videoClicks = videoClicks;
    }

    public List<String> getEndCardClicks() {
        return endCardClicks;
    }

    public void setEndCardClicks(List<String> endCardClicks) {
        this.endCardClicks = endCardClicks;
    }

    public String getVideoRedirectUrl() {
        return videoRedirectUrl;
    }

    public void setVideoRedirectUrl(String videoRedirectUrl) {
        this.videoRedirectUrl = videoRedirectUrl;
    }

    public List<String> getVideoFileUrlsList() {
        return videoFileUrlsList;
    }

    public void setVideoFileUrlsList(List<String> videoFileUrlsList) {
        this.videoFileUrlsList = videoFileUrlsList;
    }

    public List<String> getEndCardUrlList() {
        return endCardUrlList;
    }

    public void setEndCardUrlList(List<String> endCardUrlList) {
        this.endCardUrlList = endCardUrlList;
    }

    public String getSkipTime() {
        return skipTime;
    }

    public void setSkipTime(String skipTime) {
        this.skipTime = skipTime;
    }
}
