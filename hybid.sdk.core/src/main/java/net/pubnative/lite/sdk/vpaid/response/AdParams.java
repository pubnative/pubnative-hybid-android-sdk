package net.pubnative.lite.sdk.vpaid.response;

import com.iab.omid.library.pubnativenet.adsession.VerificationScriptResource;

import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.models.vast.AdServingId;
import net.pubnative.lite.sdk.vpaid.models.vast.Category;
import net.pubnative.lite.sdk.vpaid.models.vast.Icon;
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
    private String ctaExtensionHtml;
    private String skipTime;
    private Icon adIcon;
    private int publisherSkipSeconds = 0;

    private final List<String> videoFileUrlsList = new ArrayList<>();
    private final List<EndCardData> endCardList = new ArrayList<>();
    private final List<String> impressions = new ArrayList<>();
    private final List<String> companionCreativeViewEvents = new ArrayList<>();
    private final List<String> videoClicks = new ArrayList<>();
    private final List<String> endCardClicks = new ArrayList<>();
    private final List<String> ctaExtensionClicks = new ArrayList<>();
    private final List<Tracking> events = new ArrayList<>();
    private final List<AdServingId> adServingIds = new ArrayList<>();
    private final List<Category> adCategories = new ArrayList<>();
    private final List<VerificationScriptResource> verificationScriptResources = new ArrayList<>();


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
        if (impressions != null) {
            this.impressions.addAll(impressions);
        }
    }

    public List<String> getCompanionCreativeViewEvents() {
        return companionCreativeViewEvents;
    }

    public void setCompanionCreativeViewEvents(List<String> companionCreativeViewEvents) {
        if (companionCreativeViewEvents != null) {
            this.companionCreativeViewEvents.addAll(companionCreativeViewEvents);
        }
    }

    public List<Tracking> getEvents() {
        return events;
    }

    public void addEvents(List<Tracking> events) {
        if (events != null) {
            this.events.addAll(events);
        }
    }

    public List<String> getVideoClicks() {
        return videoClicks;
    }

    public void setVideoClicks(List<String> videoClicks) {
        if (videoClicks != null) {
            this.videoClicks.addAll(videoClicks);
        }
    }

    public List<String> getCtaExtensionClicks() {
        return ctaExtensionClicks;
    }

    public void setCtaExtensionClicks(List<String> ctaExtensionClicks) {
        if (ctaExtensionClicks != null) {
            this.ctaExtensionClicks.addAll(ctaExtensionClicks);
        }
    }

    public List<String> getEndCardClicks() {
        return endCardClicks;
    }

    public void setEndCardClicks(List<String> endCardClicks) {
        if (endCardClicks != null) {
            this.endCardClicks.addAll(endCardClicks);
        }
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
        if (videoFileUrlsList != null) {
            this.videoFileUrlsList.addAll(videoFileUrlsList);
        }
    }

    public List<EndCardData> getEndCardList() {
        return endCardList;
    }

    public void setEndCardList(List<EndCardData> endCardList) {
        if (endCardList != null) {
            this.endCardList.addAll(endCardList);
        }
    }

    public String getSkipTime() {
        return skipTime;
    }

    public void setSkipTime(String skipTime) {
        this.skipTime = skipTime;
    }

    public int getPublisherSkipSeconds() {
        return publisherSkipSeconds;
    }

    public void setPublisherSkipSeconds(int seconds) {
        this.publisherSkipSeconds = seconds;
    }

    public List<VerificationScriptResource> getVerificationScriptResources() {
        return verificationScriptResources;
    }

    public void addVerificationScriptResources(List<VerificationScriptResource> verificationScriptResources) {
        if (verificationScriptResources != null) {
            this.verificationScriptResources.addAll(verificationScriptResources);
        }
    }

    public void addAdServingId(AdServingId adServingId) {
        if (adServingId != null) {
            this.adServingIds.add(adServingId);
        }
    }

    public List<AdServingId> getAdServingIds() {
        return adServingIds;
    }

    public void addAdCategories(List<Category> adCategories) {
        if (adCategories != null) {
            this.adCategories.addAll(adCategories);
        }
    }

    public Icon getAdIcon() {
        return adIcon;
    }

    public void setAdIcon(Icon adIcon) {
        this.adIcon = adIcon;
    }

    public List<Category> getAdCategories() {
        return adCategories;
    }

    public String getCtaExtensionHtml() {
        return ctaExtensionHtml;
    }

    public void setCtaExtensionHtml(String ctaExtensionHtml) {
        this.ctaExtensionHtml = ctaExtensionHtml;
    }
}
