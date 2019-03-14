package net.pubnative.lite.sdk.vpaid.response;

import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.helpers.ErrorLog;
import net.pubnative.lite.sdk.vpaid.models.AdSpotDimensions;
import net.pubnative.lite.sdk.vpaid.models.vast.ClickThrough;
import net.pubnative.lite.sdk.vpaid.models.vast.ClickTracking;
import net.pubnative.lite.sdk.vpaid.models.vast.Companion;
import net.pubnative.lite.sdk.vpaid.models.vast.CompanionClickThrough;
import net.pubnative.lite.sdk.vpaid.models.vast.CompanionClickTracking;
import net.pubnative.lite.sdk.vpaid.models.vast.Creative;
import net.pubnative.lite.sdk.vpaid.models.vast.Impression;
import net.pubnative.lite.sdk.vpaid.models.vast.InLine;
import net.pubnative.lite.sdk.vpaid.models.vast.Linear;
import net.pubnative.lite.sdk.vpaid.models.vast.MediaFile;
import net.pubnative.lite.sdk.vpaid.models.vast.Tracking;
import net.pubnative.lite.sdk.vpaid.models.vast.Vast;
import net.pubnative.lite.sdk.vpaid.utils.Utils;
import net.pubnative.lite.sdk.vpaid.xml.XmlParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VastParser {
    private static final String LOG_TAG = VastParser.class.getSimpleName();

    public interface Listener {
        void onParseSuccess(AdParams adParams, String vastFileContent);

        void onParseError(PlayerInfo message);
    }

    public static void parseResponse(String response, AdSpotDimensions parseParams, Listener listener) {
        try {
            Vast vast = XmlParser.parse(response, Vast.class);
            if (vast.getStatus() != null && vast.getStatus().getText().equalsIgnoreCase("NO_AD")) {
                PlayerInfo info = new PlayerInfo("No ads found");
                info.setNoAdsFound();
                listener.onParseError(info);
            } else {
                AdParams adParams = new AdParams();
                fillAdParams(vast, adParams, parseParams);
                listener.onParseSuccess(adParams, response);
            }
        } catch (Exception e) {
            ErrorLog.postError(VastError.XML_PARSING);
            Logger.e(LOG_TAG, "Parse VAST failed: " + e.getMessage());
            PlayerInfo info = new PlayerInfo("Parse VAST response failed");
            listener.onParseError(info);
        }
    }

    private static void fillAdParams(Vast vast, AdParams adParams, AdSpotDimensions parseParams) {
        adParams.setId(vast.getAd().getId());

        InLine inLine = vast.getAd().getInLine();
        if (inLine.getError() != null) {
            ErrorLog.initErrorLog(inLine.getError().getText().trim());
        }

        List<Creative> creativeList = inLine.getCreatives().getCreativeList();

        Linear linear = null;
        for (Creative creative : creativeList) {
            if (creative.getLinear() != null) {
                linear = creative.getLinear();
                break;
            }
        }

        List<String> impressions = new ArrayList<>();
        if (inLine.getImpressionList() != null) {
            for (Impression impression : inLine.getImpressionList()) {
                impressions.add(impression.getText());
            }
        }
        adParams.setImpressions(impressions);

        adParams.setSkipTime(linear.getSkipoffset());

        if (linear.getTrackingEvents() != null) {
            adParams.setEvents(linear.getTrackingEvents().getTrackingList());
        }

        int duration = Utils.parseDuration(linear.getDuration().getText());
        adParams.setDuration(duration);

        String adParameters = parseAdParameters(linear);
        adParams.setAdParams(adParameters);

        if (linear.getVideoClicks() != null) {
            ClickThrough clickThrough = linear.getVideoClicks().getClickThrough();
            if (clickThrough != null) {
                adParams.setVideoRedirectUrl(clickThrough.getText());
            }

            List<ClickTracking> trackingList = linear.getVideoClicks().getClickTrackingList();
            List<String> clickEvents = new ArrayList<>();
            if (trackingList != null) {
                for (ClickTracking tracking : trackingList) {
                    clickEvents.add(tracking.getText());
                }
            }
            adParams.setVideoClicks(clickEvents);
        }

        List<MediaFile> mediaFileList = linear.getMediaFiles().getMediaFileList();

        String vpaidJsUrl = getVpaidJsUrl(mediaFileList);
        if (!TextUtils.isEmpty(vpaidJsUrl)) {
            adParams.setVpaid();
            adParams.setVpaidJsUrl(vpaidJsUrl);
        } else {
            List<MediaFile> sortedVideoFilesList = sortedMediaFiles(mediaFileList, parseParams);
            List<String> videoFileUrlsList = new ArrayList<>();
            for (MediaFile mediaFile : sortedVideoFilesList) {
                videoFileUrlsList.add(mediaFile.getText().trim());
            }
            adParams.setVideoFileUrlsList(videoFileUrlsList);
            if (videoFileUrlsList.isEmpty()) {
                ErrorLog.postError(VastError.MEDIA_FILE_NO_SUPPORTED_TYPE);
            }
        }

        try {
            List<Companion> companionList = getSortedCompanions(creativeList);
            List<String> endCardUrlList = new ArrayList<>();
            for (Companion companion : companionList) {
                endCardUrlList.add(companion.getStaticResource().getText().trim());
            }
            adParams.setEndCardUrlList(endCardUrlList);

            Companion companion = companionList.get(0);
            CompanionClickThrough clickThrough = companion.getCompanionClickThrough();
            if (clickThrough != null) {
                String redirectUrl = clickThrough.getText().trim();
                adParams.setEndCardRedirectUrl(redirectUrl);
            }

            if (companion.getCompanionClickTracking() != null) {
                List<String> clickEvents = new ArrayList<>();
                for (CompanionClickTracking tracking : companion.getCompanionClickTracking()) {
                    clickEvents.add(tracking.getText());
                }
                adParams.setEndCardClicks(clickEvents);
            }

            if (companion.getTrackingEvents() != null) {
                List<String> events = new ArrayList<>();
                for (Tracking tracking : companion.getTrackingEvents().getTrackingList()) {
                    events.add(tracking.getText());
                }
                adParams.setCompanionCreativeViewEvents(events);
            }
        } catch (Exception e) {
            // Do nothing, companion is optional
        }

    }

    private static String getVpaidJsUrl(List<MediaFile> mediaFileList) {
        for (MediaFile mediaFile : mediaFileList) {
            if (mediaFile.getApiFramework() != null && mediaFile.getApiFramework().equalsIgnoreCase("VPAID")) {
                return mediaFile.getText().trim();
            }
        }
        return null;
    }

    private static String parseAdParameters(Linear linear) {
        try {
            return linear.getAdParameters().getText().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private static List<Companion> getSortedCompanions(List<Creative> creativeList) {
        for (Creative creative : creativeList) {
            if (creative.getCompanionAds() != null &&
                    creative.getCompanionAds().getCompanionList() != null) {
                return creative.getCompanionAds().getCompanionList();
            }
        }
        return new ArrayList<>();
    }

    private static List<MediaFile> sortedMediaFiles(List<MediaFile> mediaFileList, AdSpotDimensions adSpotDimensions) {
        List<MediaFile> supportedMediaFilesList = new ArrayList<>();
        for (MediaFile mediaFile : mediaFileList) {
            if (mediaFile.getType().equalsIgnoreCase("video/mp4") ||
                    mediaFile.getType().equalsIgnoreCase("video/webm")) {
                supportedMediaFilesList.add(mediaFile);
            }
        }

        if (supportedMediaFilesList.size() > 1) {
            Collections.sort(supportedMediaFilesList, createComparator(adSpotDimensions));
        }
        return supportedMediaFilesList;
    }

    private static Comparator<MediaFile> createComparator(final AdSpotDimensions adSpotDimensions) {
        return new Comparator<MediaFile>() {
            @Override
            public int compare(MediaFile mediaFile1, MediaFile mediaFile2) {
                int delta1 = Math.abs(adSpotDimensions.getWidth() - mediaFile1.getWidth()) +
                        Math.abs(adSpotDimensions.getHeight() - mediaFile1.getHeight());
                int delta2 = Math.abs(adSpotDimensions.getWidth() - mediaFile2.getWidth()) +
                        Math.abs(adSpotDimensions.getHeight() - mediaFile2.getHeight());
                return (delta1 < delta2) ? -1 : ((delta1 == delta2) ? 0 : 1);
            }
        };
    }

}
