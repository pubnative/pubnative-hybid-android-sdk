package net.pubnative.lite.sdk.utils;

import java.util.ArrayList;

public class SkipOffsetManager {

    //All the below skip offsets are represented in seconds
    //TODO : Check the default values for interstitial and rewarded ads

    private static final int BACK_BUTTON_DELAY = 3;

    private static final int BACK_BUTTON_DELAY_MAXIMUM = 30;

    private static final int NATIVE_CLOSE_BUTTON_DELAY = 15;

    private static final int REWARDED_HTML_SKIP_OFFSET = 3;

    private static final int Rewarded_VIDEO_DEFAULT = 30;

    private static final int INTERSTITIAL_VIDEO_WITHOUT_END_CARD = 15;
    private static final int INTERSTITIAL_VIDEO_WITH_END_CARD = 10;

    public static final int INTERSTITIAL_MRAID = 3;

    private static Boolean isCustomInterstitialVideoSkipOffset = false;
    private static Boolean isCustomInterstitialHTMLSkipOffset = false;

    private static final int VIDEO_WITH_ENDCARD_SKIP_OFFSET = 10;

    private static final int VIDEO_WITHOUT_ENDCARD_SKIP_OFFSET = 15;

    private static final int ENDCARD_SKIP_OFFSET = 4;

//    private static final int globalMaximumSkipOffsetWithoutEndCard = 30;
//
//    private static final int globalMaximumSkipOffsetWithEndCard = 30;

    private static final int globalMaximumSkipOffset = 30;

    public static Integer getBackButtonDelay(Integer remoteConfigDelay) {
        Integer backButtonDelay = remoteConfigDelay;
        if (backButtonDelay == null || backButtonDelay < 0 || backButtonDelay > SkipOffsetManager.BACK_BUTTON_DELAY_MAXIMUM) {
            backButtonDelay = SkipOffsetManager.BACK_BUTTON_DELAY;
        }
        return backButtonDelay;
    }

    public static Integer getNativeCloseButtonDelay(Integer remoteConfigDelay) {
        Integer nativeCloseButtonDelay = remoteConfigDelay;
        if (nativeCloseButtonDelay == null || nativeCloseButtonDelay < 0 || nativeCloseButtonDelay > SkipOffsetManager.NATIVE_CLOSE_BUTTON_DELAY) {
            nativeCloseButtonDelay = SkipOffsetManager.NATIVE_CLOSE_BUTTON_DELAY;
        }
        return nativeCloseButtonDelay;
    }


    //Mraid interstitial ads has no end card !.
    public static Integer getInterstitialHTMLSkipOffset(Integer remoteConfigSkipOffset, Integer renderingSkipOffset) {

        ArrayList<Integer> values = new ArrayList<>();

        values.add(remoteConfigSkipOffset);
        values.add(renderingSkipOffset);

        Integer skipOffset = findSkipOffset(values, INTERSTITIAL_MRAID);
        isCustomInterstitialHTMLSkipOffset = skipOffset != INTERSTITIAL_MRAID;

        return skipOffset;
    }

    public static Integer getRewardedHTMLSkipOffset(Integer remoteConfigSkipOffset) {

        ArrayList<Integer> values = new ArrayList<>();

        values.add(remoteConfigSkipOffset);

        return findSkipOffset(values, REWARDED_HTML_SKIP_OFFSET);
    }

    public static Integer getInterstitialVideoSkipOffset(Integer remoteConfigSkipOffset, Integer renderingSkipOffset, Boolean isRenderingOffsetCustom, Integer publisherSkipSeconds, Integer adParamsSkipSeconds, Boolean hasEndCard) {
        int defaultSkipOffset;

        if (hasEndCard) defaultSkipOffset = INTERSTITIAL_VIDEO_WITH_END_CARD;
        else defaultSkipOffset = INTERSTITIAL_VIDEO_WITHOUT_END_CARD;

        ArrayList<Integer> values = new ArrayList<>();

        values.add(remoteConfigSkipOffset);
        if (isRenderingOffsetCustom != null && !isRenderingOffsetCustom) {
            values.add(defaultSkipOffset);
        } else {
            values.add(renderingSkipOffset);
        }
        values.add(publisherSkipSeconds);
        values.add(adParamsSkipSeconds);

        Integer skipOffset = findSkipOffset(values, defaultSkipOffset);
        isCustomInterstitialVideoSkipOffset = skipOffset != defaultSkipOffset;

        return skipOffset;
    }

    public static Integer getRewardedSkipOffset(Integer remoteConfigSkipOffset, Integer publisherSkipSeconds, Integer adParamsSkipSeconds, Boolean hasEndCard) {

        ArrayList<Integer> values = new ArrayList<>();

        values.add(remoteConfigSkipOffset);
        values.add(publisherSkipSeconds);
        values.add(adParamsSkipSeconds);

        return findSkipOffset(values, Rewarded_VIDEO_DEFAULT);
    }

    private static Integer findSkipOffset(ArrayList<Integer> values, int defaultSkipOffset) {
        int skipOffset = -1;

        for (Integer value1 : values) {
            if (value1 == null)
                continue;
            SkipOffset skipOffsetObj = isValidSkipOffset(value1);
            if (skipOffsetObj.isValid()) {
                skipOffset = skipOffsetObj.getSkipOffset();
                break;
            }
        }

        if (skipOffset == -1) skipOffset = defaultSkipOffset;

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
//        if (hasEndCard == null || hasEndCard)
//            maximumSkipOffset = globalMaximumSkipOffsetWithEndCard;
//        else
//            maximumSkipOffset = globalMaximumSkipOffsetWithoutEndCard;
        boolean isValid = false;
        if (skipOffset != null && skipOffset >= 0) {
            isValid = true;
            if (skipOffset > maximumSkipOffset)
                skipOffset = maximumSkipOffset;
        }
        return new SkipOffset(isValid, skipOffset);
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
        return Rewarded_VIDEO_DEFAULT;
    }
}
