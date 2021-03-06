package net.pubnative.lite.sdk.vpaid.response;

import android.content.Context;
import android.text.TextUtils;

import com.iab.omid.library.pubnativenet.adsession.VerificationScriptResource;

import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.helpers.ErrorLog;
import net.pubnative.lite.sdk.vpaid.models.vast.AdVerifications;
import net.pubnative.lite.sdk.vpaid.models.vast.ClickThrough;
import net.pubnative.lite.sdk.vpaid.models.vast.ClickTracking;
import net.pubnative.lite.sdk.vpaid.models.vast.Companion;
import net.pubnative.lite.sdk.vpaid.models.vast.CompanionClickThrough;
import net.pubnative.lite.sdk.vpaid.models.vast.CompanionClickTracking;
import net.pubnative.lite.sdk.vpaid.models.vast.Creative;
import net.pubnative.lite.sdk.vpaid.models.vast.Extension;
import net.pubnative.lite.sdk.vpaid.models.vast.Impression;
import net.pubnative.lite.sdk.vpaid.models.vast.InLine;
import net.pubnative.lite.sdk.vpaid.models.vast.Linear;
import net.pubnative.lite.sdk.vpaid.models.vast.MediaFile;
import net.pubnative.lite.sdk.vpaid.models.vast.Tracking;
import net.pubnative.lite.sdk.vpaid.models.vast.Vast;
import net.pubnative.lite.sdk.vpaid.models.vast.VastAdSource;
import net.pubnative.lite.sdk.vpaid.models.vast.Verification;
import net.pubnative.lite.sdk.vpaid.models.vast.Wrapper;
import net.pubnative.lite.sdk.vpaid.models.vpaid.AdSpotDimensions;
import net.pubnative.lite.sdk.vpaid.utils.Utils;
import net.pubnative.lite.sdk.vpaid.xml.XmlParser;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VastProcessor {
    private static final String LOG_TAG = VastProcessor.class.getSimpleName();
    private static final int UNWRAP_DEPTH = 5; //The processor will go maximum 5 wrappers deep to avoid having infinite loops.

    private static final String EXTENSION_TYPE_AD_VERIFICATION = "AdVerifications";

    public interface Listener {
        void onParseSuccess(AdParams adParams, String vastFileContent);

        void onParseError(PlayerInfo message);
    }

    private final Context mContext;
    private final AdSpotDimensions mParseParams;
    private final AdParams adParams = new AdParams();
    private int unwrapAttempt = 0;

    public VastProcessor(Context context, AdSpotDimensions parseParams) {
        this.mContext = context;
        this.mParseParams = parseParams;
    }

    public void parseResponse(String response, final Listener listener) {
        try {
            Vast vast = XmlParser.parse(response, Vast.class);
            if ((vast.getStatus() == null
                    || vast.getStatus().getText().equalsIgnoreCase("NO_AD"))
                    && vast.getAd() == null) {
                PlayerInfo info = new PlayerInfo("No ads found");
                info.setNoAdsFound();
                listener.onParseError(info);
            } else {
                adParams.setId(vast.getAd().getId());
                InLine inLine = vast.getAd().getInLine();
                Wrapper wrapper = vast.getAd().getWrapper();

                if (inLine != null) {
                    fillAdParams(mContext, inLine, adParams, mParseParams);
                    listener.onParseSuccess(adParams, response);
                } else if (wrapper != null) {
                    fillAdParams(mContext, wrapper, adParams, mParseParams);

                    if (unwrapAttempt < UNWRAP_DEPTH) {
                        String adTagUri = wrapper.getVastAdTagURI().getText();

                        PNHttpClient.makeRequest(mContext, adTagUri, null, null, new PNHttpClient.Listener() {
                            @Override
                            public void onSuccess(String response) {
                                parseResponse(response, listener);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                ErrorLog.postError(mContext, VastError.XML_PARSING);
                                Logger.e(LOG_TAG, "Parse VAST failed: " , error);
                                PlayerInfo info = new PlayerInfo("Parse VAST response failed " +error.getMessage());
                                listener.onParseError(info);
                            }
                        });

                        unwrapAttempt++;
                    } else {
                        PlayerInfo info = new PlayerInfo("Vast processor reached wrapper limit (5)");
                        listener.onParseError(info);
                    }
                }
            }
        } catch (Exception e) {
            ErrorLog.postError(mContext, VastError.XML_PARSING);
            Logger.e(LOG_TAG, "Parse VAST failed: ", e );
            PlayerInfo info = new PlayerInfo("Parse VAST response failed"+ e.getMessage());
            listener.onParseError(info);
        }
    }

    private void fillAdParams(Context context, VastAdSource adSource, AdParams adParams, AdSpotDimensions parseParams) {
        if (adSource.getError() != null && adSource.getError().getText() != null) {
            ErrorLog.initErrorLog(adSource.getError().getText().trim());
        }

        List<String> impressions = new ArrayList<>();
        if (adSource.getImpressionList() != null) {
            for (Impression impression : adSource.getImpressionList()) {
                impressions.add(impression.getText());
            }
        }
        adParams.setImpressions(impressions);

        if (adSource.getCreatives() != null && adSource.getCreatives().getCreativeList() != null) {
            List<Creative> creativeList = adSource.getCreatives().getCreativeList();

            Linear linear = null;
            for (Creative creative : creativeList) {
                if (creative.getLinear() != null) {
                    linear = creative.getLinear();
                    break;
                }
            }

            adParams.setSkipTime(linear.getSkipoffset());

            if (linear.getTrackingEvents() != null) {
                adParams.addEvents(linear.getTrackingEvents().getTrackingList());
            }

            String durationText;
            if (linear.getDuration() != null) {
                durationText = linear.getDuration().getText();
            } else {
                durationText = "00:00:10";
            }
            int duration = Utils.parseDuration(durationText);
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

            if (linear.getMediaFiles() != null) {
                List<MediaFile> mediaFileList = linear.getMediaFiles().getMediaFileList();

                String vpaidJsUrl = getVpaidJsUrl(mediaFileList);
                List<MediaFile> nonVpaidMediaFiles = filterNonVpaid(mediaFileList);
                if (!TextUtils.isEmpty(vpaidJsUrl) && nonVpaidMediaFiles.isEmpty()) {
                    adParams.setVpaid();
                    adParams.setVpaidJsUrl(vpaidJsUrl);
                } else {
                    List<MediaFile> sortedVideoFilesList = sortedMediaFiles(nonVpaidMediaFiles, parseParams);
                    List<String> videoFileUrlsList = new ArrayList<>();
                    for (MediaFile mediaFile : sortedVideoFilesList) {
                        if (mediaFile.getText() != null) {
                            videoFileUrlsList.add(mediaFile.getText().trim());
                        }
                    }
                    adParams.setVideoFileUrlsList(videoFileUrlsList);
                    if (videoFileUrlsList.isEmpty()) {
                        ErrorLog.postError(context, VastError.MEDIA_FILE_NO_SUPPORTED_TYPE);
                    }
                }

                try {
                    List<Companion> companionList = getSortedCompanions(creativeList);
                    List<String> endCardUrlList = new ArrayList<>();
                    for (Companion companion : companionList) {
                        if (companion.getStaticResource() != null && companion.getStaticResource().getText() != null && !TextUtils.isEmpty(companion.getStaticResource().getText())) {
                            endCardUrlList.add(companion.getStaticResource().getText().trim());
                        }
                    }
                    adParams.setEndCardUrlList(endCardUrlList);

                    if (!companionList.isEmpty()) {
                        Companion companion = companionList.get(0);
                        CompanionClickThrough clickThrough = companion.getCompanionClickThrough();
                        if (clickThrough != null && clickThrough.getText() != null) {
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
                    }
                } catch (Exception e) {
                    // Do nothing, companion is optional
                    Logger.e(LOG_TAG, e.getMessage());
                }
            }
        }

        List<VerificationScriptResource> verificationScriptResources = new ArrayList<>();
        adParams.setVerificationScriptResources(verificationScriptResources);

        if (adSource.getExtensions() != null && adSource.getExtensions().getExtensions() != null) {
            for (Extension extension : adSource.getExtensions().getExtensions()) {
                if (!TextUtils.isEmpty(extension.getType()) && extension.getType().equals(EXTENSION_TYPE_AD_VERIFICATION)) {
                    AdVerifications adVerifications = extension.getAdVerifications();
                    if (adVerifications != null) {
                        for (Verification verification : adVerifications.getVerificationList()) {
                            try {
                                final URL url = new URL(verification.getJavaScriptResource().getText());
                                final String vendorKey = verification.getVendor();
                                final String params = verification.getVerificationParameters().getText();
                                VerificationScriptResource resource = VerificationScriptResource.
                                        createVerificationScriptResourceWithParameters(vendorKey, url, params);
                                verificationScriptResources.add(resource);
                            } catch (Exception exception) {
                                Logger.e(LOG_TAG, exception.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    private String getVpaidJsUrl(List<MediaFile> mediaFileList) {
        for (MediaFile mediaFile : mediaFileList) {
            if (mediaFile.getText() != null && mediaFile.getApiFramework() != null && mediaFile.getApiFramework().equalsIgnoreCase("VPAID")) {
                return mediaFile.getText().trim();
            }
        }
        return null;
    }

    private List<MediaFile> filterNonVpaid(List<MediaFile> mediaFileList) {
        List<MediaFile> nonVpaidList = new ArrayList<>(mediaFileList);
        for (MediaFile mediaFile : mediaFileList) {
            if (mediaFile.getApiFramework() != null && mediaFile.getApiFramework().equalsIgnoreCase("VPAID")) {
                nonVpaidList.remove(mediaFile);
            }
        }
        return nonVpaidList;
    }

    private String parseAdParameters(Linear linear) {
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

    private List<MediaFile> sortedMediaFiles(List<MediaFile> mediaFileList, AdSpotDimensions adSpotDimensions) {
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

    private Comparator<MediaFile> createComparator(final AdSpotDimensions adSpotDimensions) {
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
