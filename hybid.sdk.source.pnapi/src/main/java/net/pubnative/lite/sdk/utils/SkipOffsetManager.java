// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import java.util.ArrayList;

public class SkipOffsetManager {

    //All the below skip offsets are represented in seconds
    //TODO : Check the default values for interstitial and rewarded ads

    private static final int PLAYABLE_SKIP_OFFSET = 5;
    private static final int NATIVE_CLOSE_BUTTON_DELAY = 15;

    private static final int REWARDED_HTML_SKIP_OFFSET = 30;

    private static final int REWARDED_VIDEO_DEFAULT = 30;

    private static final int INTERSTITIAL_VIDEO_WITHOUT_END_CARD = 15;
    private static final int INTERSTITIAL_VIDEO_WITH_END_CARD = 10;

    public static final int INTERSTITIAL_MRAID = 3;

    private static Boolean isCustomInterstitialVideoSkipOffset = false;
    private static Boolean isCustomInterstitialHTMLSkipOffset = false;

    private static final int VIDEO_WITH_ENDCARD_SKIP_OFFSET = 10;

    private static final int VIDEO_WITHOUT_ENDCARD_SKIP_OFFSET = 15;

    private static final int ENDCARD_SKIP_OFFSET = 4;
    private static final int ENDCARD_CLOSE_DELAY_MAXIMUM = 30;

//    private static final int globalMaximumSkipOffsetWithoutEndCard = 30;
//
//    private static final int globalMaximumSkipOffsetWithEndCard = 30;

    private static final int globalMaximumSkipOffset = 30;

    public static Integer getPlayableSkipOffset(Integer remoteConfigSkipOffset) {
        Integer skipOffset = remoteConfigSkipOffset;
        if (remoteConfigSkipOffset == null || remoteConfigSkipOffset < 0 || remoteConfigSkipOffset > SkipOffsetManager.NATIVE_CLOSE_BUTTON_DELAY) {
            skipOffset = SkipOffsetManager.PLAYABLE_SKIP_OFFSET;
        }
        return skipOffset;
    }

    public static Integer getNativeCloseButtonDelay(Integer remoteConfigDelay) {
        Integer nativeCloseButtonDelay = remoteConfigDelay;
        if (nativeCloseButtonDelay == null || nativeCloseButtonDelay < 0 || nativeCloseButtonDelay > SkipOffsetManager.NATIVE_CLOSE_BUTTON_DELAY) {
            nativeCloseButtonDelay = SkipOffsetManager.NATIVE_CLOSE_BUTTON_DELAY;
        }
        return nativeCloseButtonDelay;
    }

    public static Integer getHTMLSkipOffset(Integer remoteConfigSkipOffset, Boolean isInterstitial) {
        ArrayList<Integer> values = new ArrayList<>();
        int defaultSkipOffset;

        if (isInterstitial) {
            defaultSkipOffset = INTERSTITIAL_MRAID;
        } else {
            defaultSkipOffset = REWARDED_HTML_SKIP_OFFSET;
        }

        values.add(remoteConfigSkipOffset);

        Integer skipOffset = findSkipOffset(values, defaultSkipOffset);
        if (isInterstitial)
            isCustomInterstitialHTMLSkipOffset = skipOffset != INTERSTITIAL_MRAID;

        return skipOffset;
    }

    public static Integer getVideoSkipOffset(Integer remoteConfigSkipOffset, Integer publisherSkipSeconds, Integer adParamsSkipSeconds, Boolean hasEndCard, Boolean isInterstitial) {
        int defaultSkipOffset;
        if (isInterstitial) {
            if (hasEndCard) defaultSkipOffset = INTERSTITIAL_VIDEO_WITH_END_CARD;
            else defaultSkipOffset = INTERSTITIAL_VIDEO_WITHOUT_END_CARD;
        } else {
            defaultSkipOffset = REWARDED_VIDEO_DEFAULT;
        }

        ArrayList<Integer> values = new ArrayList<>();

        values.add(adParamsSkipSeconds);
        values.add(remoteConfigSkipOffset);
        values.add(publisherSkipSeconds);

        Integer skipOffset = findSkipOffset(values, defaultSkipOffset);
        if (isInterstitial) isCustomInterstitialVideoSkipOffset = skipOffset != defaultSkipOffset;

        return skipOffset;
    }

    private static Integer findSkipOffset(ArrayList<Integer> values, int defaultSkipOffset) {
        int skipOffset = -1;

        for (Integer value1 : values) {
            if (value1 == null) continue;
            SkipOffset skipOffsetObj = isValidSkipOffset(value1);
            if (skipOffsetObj.isValid()) {
                skipOffset = skipOffsetObj.getSkipOffset();
                break;
            }
        }

        if (skipOffset == -1) skipOffset = defaultSkipOffset;
        // Handle maximum possible skip offset
        if (skipOffset > 30) skipOffset = globalMaximumSkipOffset;

        return skipOffset;
    }

    public static Boolean isCustomInterstitialHTMLSkipOffset() {
        return isCustomInterstitialHTMLSkipOffset;
    }

    public static Boolean isCustomInterstitialVideoSkipOffset() {
        return isCustomInterstitialVideoSkipOffset;
    }

    public static SkipOffset isValidSkipOffset(Integer skipOffset) {
        int maximumSkipOffset = globalMaximumSkipOffset;
        boolean isValid = false;
        if (skipOffset != null && skipOffset >= 0) {
            isValid = true;
            if (skipOffset > maximumSkipOffset) skipOffset = maximumSkipOffset;
        }
        return new SkipOffset(isValid, skipOffset);
    }

    public static Integer getDefaultNativeCloseButtonDelay() {
        return NATIVE_CLOSE_BUTTON_DELAY;
    }

    public static Integer getDefaultRewardedHtmlSkipOffset() {
        return REWARDED_HTML_SKIP_OFFSET;
    }

    public static Integer getDefaultHtmlInterstitialSkipOffset() {
        return INTERSTITIAL_MRAID;
    }

    public static Integer getDefaultVideoWithEndCardSkipOffset() {
        return VIDEO_WITH_ENDCARD_SKIP_OFFSET;
    }

    public static Integer getDefaultVideoWithoutEndCardSkipOffset() {
        return VIDEO_WITHOUT_ENDCARD_SKIP_OFFSET;
    }

    public static Integer getDefaultEndcardSkipOffset() {
        return ENDCARD_SKIP_OFFSET;
    }

    public static Integer getMaximumEndcardCloseDelay() {
        return ENDCARD_CLOSE_DELAY_MAXIMUM;
    }

    private static class SkipOffset {
        private Integer skipOffset;
        private Boolean isValid;

        public SkipOffset(boolean isValid, Integer skipOffset) {
            this.skipOffset = skipOffset;
            this.isValid = isValid;
        }

        public Integer getSkipOffset() {
            return skipOffset;
        }

        public void setSkipOffset(Integer skipOffset) {
            this.skipOffset = skipOffset;
        }

        public Boolean isValid() {
            return isValid;
        }

        public void setValid(Boolean valid) {
            isValid = valid;
        }
    }

//    public static int getMaximumRewardedSkipOffsetWithEndCard(){
//        return Rewarded_VIDEO_WITH_END_CARD;
//    }
//
//    public static int getMaximumRewardedSkipOffsetWithoutEndCard(){
//        return Rewarded_VIDEO_WITHOUT_END_CARD;
//    }

    public static int getMaximumRewardedSkipOffset() {
        return REWARDED_VIDEO_DEFAULT;
    }
}
