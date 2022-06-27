package net.pubnative.lite.sdk.vpaid.response;

import android.content.Context;
import android.text.TextUtils;

import com.iab.omid.library.pubnativenet.adsession.VerificationScriptResource;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.helpers.ErrorLog;
import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.models.vast.Ad;
import net.pubnative.lite.sdk.vpaid.models.vast.AdVerifications;
import net.pubnative.lite.sdk.vpaid.models.vast.ClickThrough;
import net.pubnative.lite.sdk.vpaid.models.vast.ClickTracking;
import net.pubnative.lite.sdk.vpaid.models.vast.Companion;
import net.pubnative.lite.sdk.vpaid.models.vast.CompanionClickThrough;
import net.pubnative.lite.sdk.vpaid.models.vast.CompanionClickTracking;
import net.pubnative.lite.sdk.vpaid.models.vast.Creative;
import net.pubnative.lite.sdk.vpaid.models.vast.CreativeExtension;
import net.pubnative.lite.sdk.vpaid.models.vast.Error;
import net.pubnative.lite.sdk.vpaid.models.vast.Extension;
import net.pubnative.lite.sdk.vpaid.models.vast.HTMLResource;
import net.pubnative.lite.sdk.vpaid.models.vast.IFrameResource;
import net.pubnative.lite.sdk.vpaid.models.vast.Icon;
import net.pubnative.lite.sdk.vpaid.models.vast.Impression;
import net.pubnative.lite.sdk.vpaid.models.vast.InLine;
import net.pubnative.lite.sdk.vpaid.models.vast.JavaScriptResource;
import net.pubnative.lite.sdk.vpaid.models.vast.Linear;
import net.pubnative.lite.sdk.vpaid.models.vast.MediaFile;
import net.pubnative.lite.sdk.vpaid.models.vast.StaticResource;
import net.pubnative.lite.sdk.vpaid.models.vast.Tracking;
import net.pubnative.lite.sdk.vpaid.models.vast.Vast;
import net.pubnative.lite.sdk.vpaid.models.vast.VastAdSource;
import net.pubnative.lite.sdk.vpaid.models.vast.Verification;
import net.pubnative.lite.sdk.vpaid.models.vast.VerveCTAButton;
import net.pubnative.lite.sdk.vpaid.models.vast.Wrapper;
import net.pubnative.lite.sdk.vpaid.models.vpaid.AdSpotDimensions;
import net.pubnative.lite.sdk.vpaid.utils.Utils;
import net.pubnative.lite.sdk.vpaid.xml.XmlParser;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if ((vast.getAds() == null || vast.getAds().isEmpty())
                    || (vast.getErrors() != null && !vast.getErrors().isEmpty())) {
                if (vast.getErrors() != null) {
                    List<String> errorLogs = new ArrayList<>();
                    for (Error error : vast.getErrors()) {
                        if (!TextUtils.isEmpty(error.getText())) {
                            errorLogs.add(error.getText().trim());
                        }
                    }
                    ErrorLog.initErrorLog(errorLogs);
                    ErrorLog.postError(mContext, VastError.XML_PARSING);
                }
                if (listener != null) {
                    PlayerInfo info = new PlayerInfo("No ads found");
                    info.setNoAdsFound();
                    listener.onParseError(info);
                }
            } else {
                Ad ad = vast.getAds().get(0);
                adParams.setId(ad.getId());
                InLine inLine = ad.getInLine();
                Wrapper wrapper = ad.getWrapper();

                if (inLine != null) {
                    fillAdParams(mContext, inLine, adParams, mParseParams, response);
                    if (listener != null) {
                        listener.onParseSuccess(adParams, response);
                    }
                } else if (wrapper != null) {
                    fillAdParams(mContext, wrapper, adParams, mParseParams, response);

                    if (unwrapAttempt < UNWRAP_DEPTH) {
                        String adTagUri = wrapper.getVastAdTagURI().getText();

                        Map<String, String> headers = new HashMap<>();
                        String userAgent = HyBid.getDeviceInfo().getUserAgent();
                        if (!TextUtils.isEmpty(userAgent)) {
                            headers.put("User-Agent", userAgent);
                        }

                        PNHttpClient.makeRequest(mContext, adTagUri, headers, null, new PNHttpClient.Listener() {
                            @Override
                            public void onSuccess(String response) {
                                parseResponse(response, listener);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                ErrorLog.postError(mContext, VastError.WRAPPER);
                                Logger.e(LOG_TAG, "Parse VAST failed: ", error);
                                if (listener != null) {
                                    PlayerInfo info = new PlayerInfo("Parse VAST response failed " + error.getMessage());
                                    listener.onParseError(info);
                                }
                            }
                        });

                        unwrapAttempt++;
                    } else {
                        ErrorLog.postError(mContext, VastError.WRAPPER_LIMIT);
                        Logger.e(LOG_TAG, "Parse VAST failed: Vast processor reached wrapper limit (5)");
                        if (listener != null) {
                            PlayerInfo info = new PlayerInfo("Vast processor reached wrapper limit (5)");
                            listener.onParseError(info);
                        }
                    }
                } else {
                    ErrorLog.postError(mContext, VastError.XML_PARSING);
                    Logger.e(LOG_TAG, "Parse VAST failed: No ad source was received");
                    if (listener != null) {
                        PlayerInfo info = new PlayerInfo("No VAST ad source was received");
                        listener.onParseError(info);
                    }
                }
            }
        } catch (Exception e) {
            ErrorLog.postError(mContext, VastError.XML_PARSING);
            Logger.e(LOG_TAG, "Parse VAST failed: ", e);
            if (listener != null) {
                PlayerInfo info = new PlayerInfo("Parse VAST response failed" + e.getMessage());
                listener.onParseError(info);
            }
        }
    }

    private void fillAdParams(Context context, VastAdSource adSource, AdParams adParams, AdSpotDimensions parseParams, String response) {
        if (adSource.getErrors() != null && !adSource.getErrors().isEmpty()) {
            List<String> errorLogs = new ArrayList<>();
            for (Error error : adSource.getErrors()) {
                if (!TextUtils.isEmpty(error.getText())) {
                    errorLogs.add(error.getText().trim());
                }
            }
            ErrorLog.initErrorLog(errorLogs);
        }

        List<String> impressions = new ArrayList<>();
        if (adSource.getImpressions() != null) {
            for (Impression impression : adSource.getImpressions()) {
                if (!TextUtils.isEmpty(impression.getText())) {
                    impressions.add(impression.getText());
                }
            }
        }
        adParams.setImpressions(impressions);

        if (adSource.getCategories() != null) {
            adParams.addAdCategories(adSource.getCategories());
        }

        if (adSource.getAdServingId() != null && !TextUtils.isEmpty(adSource.getAdServingId().getText())) {
            adParams.addAdServingId(adSource.getAdServingId());
        }

        List<VerificationScriptResource> verificationScriptResources = new ArrayList<>();

        //Support for ad verification in the Inline Extensions (pre-VAST 4)
        if (adSource.getExtensions() != null && adSource.getExtensions().getExtensions() != null) {
            for (Extension extension : adSource.getExtensions().getExtensions()) {
                if (!TextUtils.isEmpty(extension.getType()) && extension.getType().equals(EXTENSION_TYPE_AD_VERIFICATION)) {
                    AdVerifications adVerifications = extension.getAdVerifications();
                    if (adVerifications != null && adVerifications.getVerificationList() != null) {
                        for (Verification verification : adVerifications.getVerificationList()) {
                            try {
                                if (verification.getJavaScriptResources() != null) {
                                    for (JavaScriptResource javaScriptResource : verification.getJavaScriptResources()) {
                                        if (!TextUtils.isEmpty(javaScriptResource.getText())) {
                                            final URL url = new URL(javaScriptResource.getText().trim());
                                            if (!TextUtils.isEmpty(verification.getVendor())
                                                    && verification.getVerificationParameters() != null
                                                    && !TextUtils.isEmpty(verification.getVerificationParameters().getText())) {
                                                final String vendorKey = verification.getVendor();
                                                final String params = verification.getVerificationParameters().getText();
                                                VerificationScriptResource resource = VerificationScriptResource.
                                                        createVerificationScriptResourceWithParameters(vendorKey, url, params);
                                                verificationScriptResources.add(resource);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception exception) {
                                Logger.e(LOG_TAG, exception.getMessage());
                            }
                        }
                    }
                }
            }
        }

        AdVerifications adVerifications = adSource.getAdVerifications();
        if (adVerifications != null && adVerifications.getVerificationList() != null) {
            for (Verification verification : adVerifications.getVerificationList()) {
                try {
                    if (verification.getJavaScriptResources() != null) {
                        for (JavaScriptResource javaScriptResource : verification.getJavaScriptResources()) {
                            if (!TextUtils.isEmpty(javaScriptResource.getText())) {
                                final URL url = new URL(javaScriptResource.getText().trim());
                                if (!TextUtils.isEmpty(verification.getVendor())
                                        && verification.getVerificationParameters() != null
                                        && !TextUtils.isEmpty(verification.getVerificationParameters().getText())) {
                                    final String vendorKey = verification.getVendor();
                                    final String params = verification.getVerificationParameters().getText();
                                    VerificationScriptResource resource = VerificationScriptResource.
                                            createVerificationScriptResourceWithParameters(vendorKey, url, params);
                                    verificationScriptResources.add(resource);
                                }
                            }
                        }
                    }
                } catch (Exception exception) {
                    Logger.e(LOG_TAG, exception.getMessage());
                }
            }
        }

        adParams.addVerificationScriptResources(verificationScriptResources);

        if (adSource.getCreatives() != null && adSource.getCreatives().getCreatives() != null) {
            List<Creative> creativeList = adSource.getCreatives().getCreatives();

            Linear linear = null;

            for (Creative creative : creativeList) {
                if (creative.getLinear() != null) {
                    linear = creative.getLinear();
                    break;
                }
            }

            if (linear != null) {
                if (!TextUtils.isEmpty(linear.getSkipOffset())) {
                    adParams.setSkipTime(linear.getSkipOffset());
                }

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

                if (linear.getAdParameters() != null && !TextUtils.isEmpty(linear.getAdParameters().getText())) {
                    adParams.setAdParams(linear.getAdParameters().getText().trim());
                }

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

                if (linear.getMediaFiles() != null
                        && linear.getMediaFiles().getMediaFiles() != null
                        && !linear.getMediaFiles().getMediaFiles().isEmpty()) {
                    List<MediaFile> mediaFileList = linear.getMediaFiles().getMediaFiles();

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
                        List<Companion> companionList = getSortedCompanions(creativeList, parseParams);
                        List<EndCardData> endCardList = new ArrayList<>();
                        for (int i = 0; i < companionList.size() && endCardList.isEmpty(); i++) {
                            Companion companion = companionList.get(i);
                            if (companion.getHtmlResources() != null && !companion.getHtmlResources().isEmpty()) {
                                for (HTMLResource htmlResource : companion.getHtmlResources()) {
                                    if (!TextUtils.isEmpty(htmlResource.getText())) {
                                        endCardList.add(new EndCardData(EndCardData.Type.HTML_RESOURCE, htmlResource.getText().trim()));
                                    }
                                }
                            }
                            if (companion.getiFrameResources() != null && !companion.getiFrameResources().isEmpty()) {
                                for (IFrameResource iFrameResource : companion.getiFrameResources()) {
                                    if (!TextUtils.isEmpty(iFrameResource.getText())) {
                                        endCardList.add(new EndCardData(EndCardData.Type.IFRAME_RESOURCE, iFrameResource.getText().trim()));
                                    }
                                }
                            }
                            if (companion.getStaticResources() != null && !companion.getStaticResources().isEmpty()) {
                                for (StaticResource staticResource : companion.getStaticResources()) {
                                    if (!TextUtils.isEmpty(staticResource.getText())) {
                                        endCardList.add(new EndCardData(EndCardData.Type.STATIC_RESOURCE, staticResource.getText().trim()));
                                    }
                                }
                            }

                        }
                        adParams.setEndCardList(endCardList);

                        if (!companionList.isEmpty()) {
                            Companion companion = companionList.get(0);
                            CompanionClickThrough clickThrough = companion.getCompanionClickThrough();
                            if (clickThrough != null && !TextUtils.isEmpty(clickThrough.getText())) {
                                String redirectUrl = clickThrough.getText().trim();
                                adParams.setEndCardRedirectUrl(redirectUrl);
                            }

                            if (companion.getCompanionClickTrackingList() != null) {
                                List<String> clickEvents = new ArrayList<>();
                                for (CompanionClickTracking tracking : companion.getCompanionClickTrackingList()) {
                                    clickEvents.add(tracking.getText());
                                }
                                adParams.setEndCardClicks(clickEvents);
                            }

                            if (companion.getTrackingEvents() != null
                                    && companion.getTrackingEvents().getTrackingList() != null) {
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

                if (linear.getIcons() != null
                        && linear.getIcons().getIcons() != null
                        && !linear.getIcons().getIcons().isEmpty()) {
                    List<Icon> icons = linear.getIcons().getIcons();
                    Icon icon = null;
                    for (int i = 0; i < icons.size() && icon == null; i++) {
                        Icon ic = icons.get(i);
                        if (ic != null && !TextUtils.isEmpty(ic.getProgram())
                                && ic.getStaticResources() != null
                                && !ic.getStaticResources().isEmpty()) {
                            icon = ic;
                        }
                    }

                    if (icon != null) {
                        adParams.setAdIcon(icon);
                    }
                }

                CreativeExtension extension = null;
                int i = 0;
                while (i < creativeList.size() && extension == null) {
                    Creative creative = creativeList.get(i);

                    if (creative != null && creative.getCreativeExtensions() != null
                            && creative.getCreativeExtensions().getCreativeExtensions() != null
                            && !creative.getCreativeExtensions().getCreativeExtensions().isEmpty()) {
                        List<CreativeExtension> creativeExtensions = creative.getCreativeExtensions().getCreativeExtensions();
                        VerveCTAButton ctaExtension = null;
                        int j = 0;
                        while (j < creativeExtensions.size() && ctaExtension == null) {
                            extension = creativeExtensions.get(j);
                            if (extension != null && extension.getType().equals("Verve") && extension.getVerveCTAButton() != null) {
                                ctaExtension = extension.getVerveCTAButton();
                            } else {
                                j++;
                            }
                        }

                        if (ctaExtension != null) {
                            if (ctaExtension.getHtmlResource() != null && !TextUtils.isEmpty(ctaExtension.getHtmlResource().getText())) {
                                adParams.setCtaExtensionHtml(ctaExtension.getHtmlResource().getText());
                            }

                            if (ctaExtension.getTrackingEvents() != null
                                    && ctaExtension.getTrackingEvents().getTrackingList() != null
                                    && !ctaExtension.getTrackingEvents().getTrackingList().isEmpty()) {
                                List<String> ctaClicks = new ArrayList<>();
                                for (Tracking tracking : ctaExtension.getTrackingEvents().getTrackingList()) {
                                    if (tracking != null && !TextUtils.isEmpty(tracking.getEvent())
                                            && tracking.getEvent().equals("CTAClick")
                                            && !TextUtils.isEmpty(tracking.getText())) {
                                        ctaClicks.add(tracking.getText());
                                    }
                                }
                                adParams.setCtaExtensionClicks(ctaClicks);
                            }
                        }
                    }
                    i++;
                }
            }
        }
    }

    private String getVpaidJsUrl(List<MediaFile> mediaFileList) {
        for (MediaFile mediaFile : mediaFileList) {
            if (mediaFile.getText() != null && mediaFile.getApiFramework() != null
                    && mediaFile.getApiFramework().equalsIgnoreCase("VPAID")) {
                return mediaFile.getText().trim();
            }
        }
        return null;
    }

    private List<MediaFile> filterNonVpaid(List<MediaFile> mediaFileList) {
        List<MediaFile> nonVpaidList = new ArrayList<>(mediaFileList);
        for (MediaFile mediaFile : mediaFileList) {
            if (mediaFile.getApiFramework() != null
                    && mediaFile.getApiFramework().equalsIgnoreCase("VPAID")) {
                nonVpaidList.remove(mediaFile);
            }
        }
        return nonVpaidList;
    }

    private String parseAdParameters(Linear linear) {
        try {
            return linear.getAdParameters().getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private List<Companion> getSortedCompanions(List<Creative> creativeList, AdSpotDimensions adSpotDimensions) {
        for (Creative creative : creativeList) {
            if (creative.getCompanionAds() != null &&
                    creative.getCompanionAds().getCompanions() != null) {
                List<Companion> companions = new ArrayList<>(creative.getCompanionAds().getCompanions());
                Collections.sort(companions, createCompanionComparator(adSpotDimensions));
                return companions;
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
        return (mediaFile1, mediaFile2) -> {
            int width1 = 0;
            int height1 = 0;
            int width2 = 0;
            int height2 = 0;

            try {
                width1 = Integer.parseInt(mediaFile1.getWidth());
                height1 = Integer.parseInt(mediaFile1.getHeight());
                width2 = Integer.parseInt(mediaFile2.getWidth());
                height2 = Integer.parseInt(mediaFile2.getHeight());
            } catch (RuntimeException e) {
                Logger.w(LOG_TAG, e.getMessage());
            }

            int delta1 = Math.abs(adSpotDimensions.getWidth() - width1) +
                    Math.abs(adSpotDimensions.getHeight() - height1);
            int delta2 = Math.abs(adSpotDimensions.getWidth() - width2) +
                    Math.abs(adSpotDimensions.getHeight() - height2);
            return Integer.compare(delta1, delta2);
        };
    }

    private Comparator<Companion> createCompanionComparator(final AdSpotDimensions adSpotDimensions) {
        return (companion1, companion2) -> {
            int width1 = 0;
            int height1 = 0;
            int width2 = 0;
            int height2 = 0;

            try {
                width1 = Integer.parseInt(companion1.getWidth());
                height1 = Integer.parseInt(companion1.getHeight());
                width2 = Integer.parseInt(companion2.getWidth());
                height2 = Integer.parseInt(companion2.getHeight());
            } catch (RuntimeException e) {
                Logger.w(LOG_TAG, e.getMessage());
            }

            int delta1 = Math.abs(adSpotDimensions.getWidth() - width1) +
                    Math.abs(adSpotDimensions.getHeight() - height1);
            int delta2 = Math.abs(adSpotDimensions.getWidth() - width2) +
                    Math.abs(adSpotDimensions.getHeight() - height2);
            return Integer.compare(delta1, delta2);
        };
    }
}
