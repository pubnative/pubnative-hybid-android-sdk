// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.mraid.model;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdExperience;
import net.pubnative.lite.sdk.models.CustomCTAData;
import net.pubnative.lite.sdk.models.EndCardData;
import net.pubnative.lite.sdk.utils.AdCustomCTAManager;
import net.pubnative.lite.sdk.utils.AdEndCardManager;
import net.pubnative.lite.sdk.utils.ClickThroughTimerManager;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;

public class HTMLAd {

    private String link;
    private Integer skipDelay;
    private Integer nativeButtonCloseDelay;
    private Integer endCardCloseDelay;
    private boolean shouldShowCustomEndCard;
    private EndCardData endCardData;
    private CustomCTAData customCTAData;
    private Integer customCTADelay;
    private final Ad ad;
    private LandingPageHandler landingPageHandler;
    private int clickThroughTimer;
    ClickThroughTimerManager.ClickThroughTimerListener clickThroughTimerListener;

    public HTMLAd(Context context, Ad ad, AdType adType) {
        this.ad = ad;
        if (ad != null) {
            link = ad.getLink();
            customCTAData = ad.getCustomCta(context, false);
            customCTADelay = AdCustomCTAManager.getCustomCtaDelay(ad);
            clickThroughTimer = ad.getClickThroughTimer();
            landingPageHandler = new LandingPageHandler(ad);
            if (ad.getCustomEndCard() != null && AdEndCardManager.shouldShowCustomEndcard(ad)) {
                shouldShowCustomEndCard = AdEndCardManager.shouldShowCustomEndcard(ad);
                endCardData = ad.getCustomEndCard();
                endCardCloseDelay = ad.getEndCardCloseDelay();
            }
            if (adType == AdType.INTERSTITIAL) {
                skipDelay = SkipOffsetManager.getHTMLSkipOffset(ad.getHtmlSkipOffset(), true) * 1000;
            } else {
                skipDelay = SkipOffsetManager.getHTMLSkipOffset(ad.getMraidRewardedSkipOffset(), false) * 1000;
            }
            nativeButtonCloseDelay = SkipOffsetManager.getNativeCloseButtonDelay(ad.getNativeCloseButtonDelay()) * 1000;
        }
    }

    public Integer getSkipDelay() {
        return skipDelay;
    }

    public Integer getNativeButtonCloseDelay() {
        return nativeButtonCloseDelay;
    }

    public Integer getCloseDelay() {
        return skipDelay;
    }

    public String getLink() {
        return link;
    }

    public LandingPageHandler getLandingPage() {
        return landingPageHandler;
    }

    public EndCardData getEndCardData() {
        return endCardData;
    }

    public Integer getEndCardCloseDelay() {
        return endCardCloseDelay;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ClickThroughTimerManager.ClickThroughTimerListener getClickThroughTimerListener() {
        return clickThroughTimerListener;
    }

    public void setClickThroughTimerListener(ClickThroughTimerManager.ClickThroughTimerListener listener) {
        this.clickThroughTimerListener = listener;
    }

    public int getClickThroughTimer() {
        return clickThroughTimer;
    }

    //-----Helpers----
    public boolean hasReducedCloseSize() {
        if (ad != null) {
            Boolean hasReducedIconSize = ad.isIconSizeReduced();
            String adExperience = ad.getAdExperience();
            return adExperience.equalsIgnoreCase(AdExperience.PERFORMANCE) && hasReducedIconSize != null && hasReducedIconSize;
        }
        return false;
    }

    public boolean shouldInitEndCardView() {
        return endCardData != null && !TextUtils.isEmpty(endCardData.getContent())
                && shouldShowCustomEndCard && ad != null && ad.isPerformanceAd();
    }

    public boolean hasLandingPage() {
        return landingPageHandler != null && landingPageHandler.isLandingPageEnabled() && !TextUtils.isEmpty(landingPageHandler.getCustomisationString());
    }

    public Integer getCustomCTADelay() {
        return customCTADelay;
    }

    public CustomCTAData getCustomCTAData() {
        return customCTAData;
    }

    public Boolean isCustomCTAEnabled() {
        return ad != null && customCTAData != null && AdCustomCTAManager.isEnabled(this.ad);
    }

    public enum AdType {
        INTERSTITIAL, REWARDED

    }
}