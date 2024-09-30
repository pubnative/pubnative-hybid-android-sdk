package net.pubnative.lite.sdk.models;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.DisplayManager;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.TopicManager;
import net.pubnative.lite.sdk.UserDataManager;
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.models.bidstream.BidstreamConstants;
import net.pubnative.lite.sdk.models.request.App;
import net.pubnative.lite.sdk.models.request.Banner;
import net.pubnative.lite.sdk.models.request.Data;
import net.pubnative.lite.sdk.models.request.DataExtension;
import net.pubnative.lite.sdk.models.request.Device;
import net.pubnative.lite.sdk.models.request.Ext;
import net.pubnative.lite.sdk.models.request.Format;
import net.pubnative.lite.sdk.models.request.Geo;
import net.pubnative.lite.sdk.models.request.Imp;
import net.pubnative.lite.sdk.models.request.Metric;
import net.pubnative.lite.sdk.models.request.Native;
import net.pubnative.lite.sdk.models.request.OpenRTBAdRequest;
import net.pubnative.lite.sdk.models.request.Regs;
import net.pubnative.lite.sdk.models.request.Segment;
import net.pubnative.lite.sdk.models.request.User;
import net.pubnative.lite.sdk.models.request.Video;
import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNAsyncUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OpenRTBAdRequestFactory extends BaseRequestFactory implements AdRequestFactory {
    private static final String TAG = OpenRTBAdRequestFactory.class.getSimpleName();

    private final DeviceInfo mDeviceInfo;
    private final HyBidLocationManager mLocationManager;
    private final UserDataManager mUserDataManager;
    private final TopicManager mTopicManager;
    private final DisplayManager mDisplayManager;
    private IntegrationType mIntegrationType = IntegrationType.HEADER_BIDDING;
    private String mMediationVendor;
    private boolean mIsRewarded;
    private boolean mLimitTracking;
    private String mAdvertisingId;
    private boolean mIsCCPAOptOut;
    private String mAdFormat;

    public OpenRTBAdRequestFactory() {
        this(HyBid.getDeviceInfo(), HyBid.getLocationManager(), HyBid.getUserDataManager(), new DisplayManager(), HyBid.getTopicManager());
    }

    OpenRTBAdRequestFactory(DeviceInfo deviceInfo, HyBidLocationManager locationManager, UserDataManager userDataManager, DisplayManager displayManager, TopicManager topicManager) {
        mDeviceInfo = deviceInfo;
        mLocationManager = locationManager;
        mUserDataManager = userDataManager;
        mDisplayManager = displayManager;
        mTopicManager = topicManager;
    }

    @Override
    public void createAdRequest(String appToken, String zoneid, AdSize adSize, boolean isRewarded, boolean protectedAudiencesAvailable, Callback callback) {
        mAdvertisingId = mDeviceInfo.getAdvertisingId();
        mLimitTracking = mDeviceInfo.limitTracking();
        Context context = mDeviceInfo.getContext();
        mIsRewarded = isRewarded;
        if (TextUtils.isEmpty(mAdvertisingId) && context != null) {
            try {
                PNAsyncUtils.safeExecuteOnExecutor(new HyBidAdvertisingId(context, new HyBidAdvertisingId.Listener() {
                    @Override
                    public void onHyBidAdvertisingIdFinish(String advertisingId, Boolean limitTracking) {
                        processAdvertisingId(appToken, zoneid, adSize, advertisingId, limitTracking, protectedAudiencesAvailable, callback);
                    }
                }));
            } catch (Exception exception) {
                Logger.e(TAG, "Error executing HyBidAdvertisingId AsyncTask");
            }
        } else {
            processAdvertisingId(appToken, zoneid, adSize, mAdvertisingId, mLimitTracking, protectedAudiencesAvailable, callback);
        }
    }

    @Override
    public AdRequest buildRequest(String appToken, String zoneid, AdSize adSize, String advertisingId, boolean limitTracking, IntegrationType integrationType, String mediationVendor, Integer impDepth, boolean paAvailable) {
        mIsCCPAOptOut = mUserDataManager.isCCPAOptOut();
        OpenRTBAdRequest bidRequest = new OpenRTBAdRequest(appToken, zoneid);
        bidRequest.setId("92d6421e44a44dff9f05b29be0ca5bef");
        bidRequest.setImp(getImpressions(adSize, mediationVendor, integrationType));
        bidRequest.setApp(getApp());
        bidRequest.setDevice(getDevice());
        bidRequest.setUser(getUser());
        bidRequest.setTest(getTestInt());
        bidRequest.setAt(2);
        bidRequest.setTmax(1500);
        bidRequest.setAllimps(0);
        bidRequest.setRegs(getRegs());

        List<String> currencies = new ArrayList<>();
        currencies.add("USD");
        bidRequest.setCur(currencies);

        bidRequest.isInterstitial = adSize == AdSize.SIZE_INTERSTITIAL;

        return bidRequest;
    }

    @Override
    public void setMediationVendor(String mediationVendor) {
        this.mMediationVendor = mediationVendor;
    }

    @Override
    public void setIntegrationType(IntegrationType integrationType) {
        this.mIntegrationType = integrationType;
    }

    @Override
    public void setAdFormat(String adFormat) {
        this.mAdFormat = adFormat;
    }

    private void processAdvertisingId(String appToken, String zoneId, AdSize adSize, String advertisingId, boolean limitTracking, boolean paAvailable, PNAdRequestFactory.Callback callback) {
        if (callback != null) {
            callback.onRequestCreated(buildRequest(appToken, zoneId, adSize, advertisingId, limitTracking, mIntegrationType, mMediationVendor, 0, paAvailable));
        }
    }

    List<Imp> getImpressions(AdSize adSize, String mediationVendor, IntegrationType integrationType) {
        List<Imp> imps = new ArrayList<>();
        boolean isVideoCompatible = adSize == AdSize.SIZE_INTERSTITIAL || adSize == AdSize.SIZE_300x250
                || adSize == AdSize.SIZE_320x480 || adSize == AdSize.SIZE_480x320
                || adSize == AdSize.SIZE_768x1024 || adSize == AdSize.SIZE_1024x768;

        if (mAdFormat != null && mAdFormat.equals("video") && isVideoCompatible) {
            imps.add(getVideoImpression(adSize, mediationVendor, integrationType));
        } else if (mAdFormat != null && mAdFormat.equals("html")) {
            imps.add(getBannerImpression(adSize, mediationVendor, integrationType));
        } else {
            if (isVideoCompatible) {
                imps.add(getVideoImpression(adSize, mediationVendor, integrationType));
            }
            imps.add(getBannerImpression(adSize, mediationVendor, integrationType));
        }
        return imps;
    }

    Imp getBannerImpression(AdSize adSize, String mediationVendor, IntegrationType integrationType) {
        Imp imp = new Imp();
        imp.setId("94628ee5-fe99-436d-94b5-f3270ad06530");

        List<Metric> metrics = new ArrayList<>();
        imp.setMetric(metrics);

        imp.setBanner(getBanner(adSize));
        imp.setDisplaymanager(mDisplayManager.getDisplayManager());
        imp.setDisplaymanagerver(mDisplayManager.getDisplayManagerVersion(mediationVendor, integrationType));
        int instl = 0;
        if (adSize == AdSize.SIZE_INTERSTITIAL) instl = 1;
        imp.setInstl(instl);
        imp.setClickbrowser(1);
        imp.setSecure(1);

        return imp;
    }

    Imp getVideoImpression(AdSize adSize, String mediationVendor, IntegrationType integrationType) {
        Imp imp = new Imp();
        imp.setId("94628ee5-fe99-436d-94b5-f3270ad06529");

        List<Metric> metrics = new ArrayList<>();
        imp.setMetric(metrics);

        imp.setVideo(getVideo(adSize));
        imp.setDisplaymanager(mDisplayManager.getDisplayManager());
        imp.setDisplaymanagerver(mDisplayManager.getDisplayManagerVersion(mediationVendor, integrationType));
        int instl = 0;
        if (adSize == AdSize.SIZE_INTERSTITIAL) instl = 1;
        imp.setInstl(instl);
        imp.setClickbrowser(1);
        imp.setSecure(1);
        return imp;
    }

    Banner getBanner(AdSize adSize) {
        Banner banner = new Banner();

        List<Format> formats = new ArrayList<>();
        banner.setFormat(formats);

        if (adSize == AdSize.SIZE_INTERSTITIAL) {
            banner.setW(320);
            banner.setH(480);
        } else {
            banner.setW(adSize.getWidth());
            banner.setH(adSize.getHeight());
        }

        List<Integer> blacklistedTypes = new ArrayList<>();
        banner.setBtype(blacklistedTypes);

        List<Integer> blacklistedAttributes = new ArrayList<>();
        banner.setBattr(blacklistedAttributes);


        if (adSize != AdSize.SIZE_INTERSTITIAL) {
            List<Integer> expDirs = new ArrayList<>();
            expDirs.add(BidstreamConstants.ExpandableDirections.FULLSCREEN);
            expDirs.add(BidstreamConstants.ExpandableDirections.RESIZE_MINIMIZE);
            banner.setExpdir(expDirs);
        }

        banner.setPos(adSize == AdSize.SIZE_INTERSTITIAL ? BidstreamConstants.PlacementPosition.FULLSCREEN : BidstreamConstants.PlacementPosition.UNKNOWN);

        List<String> mimes = new ArrayList<>();
        mimes.add("text/html");
        mimes.add("text/javascript");
        banner.setMimes(mimes);

        banner.setTopframe(1);

        List<Integer> apis = new ArrayList<>();
        banner.setApi(apis);

        banner.setId("");
        banner.setVcm(0);

        banner.setApi(getSupportedApis());

        return banner;
    }

    Video getVideo(AdSize adSize) {
        Video video = new Video();
        if (adSize == AdSize.SIZE_INTERSTITIAL) {
            video.setWidth(320);
            video.setHeight(480);
        } else {
            video.setWidth(adSize.getWidth());
            video.setHeight(adSize.getHeight());
        }

        List<Integer> playbackMethods = new ArrayList<>();
        if (adSize != AdSize.SIZE_INTERSTITIAL) {
            video.setPlacementSubtype(BidstreamConstants.VideoPlacementSubtype.STANDALONE);
            playbackMethods.add(BidstreamConstants.VideoPlaybackMethod.ENTER_VIEWPORT_SOUND_ON);
            playbackMethods.add(BidstreamConstants.VideoPlaybackMethod.ENTER_VIEWPORT_SOUND_OFF);
        } else {
            video.setPlacement(BidstreamConstants.VideoPlacement.INTERSTITIAL);
            video.setPlacementSubtype(BidstreamConstants.VideoPlacementSubtype.INTERSTITIAL);
            playbackMethods.add(BidstreamConstants.VideoPlaybackMethod.PAGE_LOAD_SOUND_ON);
            playbackMethods.add(BidstreamConstants.VideoPlaybackMethod.PAGE_LOAD_SOUND_OFF);

        }

        video.setPlaybackMethod(playbackMethods);

        video.setPos(adSize == AdSize.SIZE_INTERSTITIAL ? BidstreamConstants.PlacementPosition.FULLSCREEN : BidstreamConstants.PlacementPosition.UNKNOWN);

        List<String> mimes = new ArrayList<>();
        mimes.add("video/mp4");
        mimes.add("video/webm");
        mimes.add("video/3gpp");
        mimes.add("video/3gpp2");
        mimes.add("video/x-m4v");
        video.setMimes(mimes);

        video.setBoxingAllowed(0);
        video.setLinearity(1);
        video.setPlaybackEnd(1);
        video.setMraidEndcard(true);
        video.setClickType(3);
        List<Integer> delivery = new ArrayList<>();
        delivery.add(3);
        video.setDelivery(delivery);

        video.setProtocols(getSupportedProtocols());

        return video;
    }

    Native getNative() {
        Native nativeAd = new Native();
        return nativeAd;
    }

    App getApp() {
        App app = new App();
        app.setBundle(HyBid.getBundleId());

        List<String> categories = new ArrayList<>();
        app.setCategories(categories);

        List<String> sectionCategories = new ArrayList<>();
        app.setSectionCategories(sectionCategories);

        List<String> pageCategories = new ArrayList<>();
        app.setPageCategories(pageCategories);
        app.setKeywords(HyBid.getKeywords());

        return app;
    }

    Device getDevice() {
        Device device = new Device();
        device.setOs("Android");
        if (mDeviceInfo != null) {
            device.setOsVersion(mDeviceInfo.getOSVersion());
            device.setUserAgent(mDeviceInfo.getUserAgent());
            device.setModel(mDeviceInfo.getModel());
            device.setMake(mDeviceInfo.getMake());
            device.setDeviceType(mDeviceInfo.getDeviceType());
            device.setCarrier(mDeviceInfo.getCarrier());
            device.setMccmnc(mDeviceInfo.getMccmnc());
            device.setMccmncsim(mDeviceInfo.getMccmncsim());
            device.setPpi(Integer.parseInt(mDeviceInfo.getPpi()));
            device.setPxratio(Float.parseFloat(mDeviceInfo.getPxratio()));
            device.setH(Integer.parseInt(mDeviceInfo.getDeviceHeight()));
            device.setW(Integer.parseInt(mDeviceInfo.getDeviceWidth()));
            device.setLanguage(mDeviceInfo.getLocale().toString());
            device.setConnectiontype(mDeviceInfo.getConnectionType());
            device.setIfa(mDeviceInfo.getAdvertisingId());
            device.setDpidsha1(mDeviceInfo.getAdvertisingIdSha1());
            device.setDpidmd5(mDeviceInfo.getAdvertisingIdMd5());
            if (mDeviceInfo.getLocale() != null) {
                mDeviceInfo.getLocale().getLanguage();
                if (!mDeviceInfo.getLocale().getLanguage().isEmpty()) {
                    device.setLanguage(mDeviceInfo.getLocale().getLanguage());
                }
            }
            if (mDeviceInfo.getStructuredUserAgent() != null) {
                device.setSua(mDeviceInfo.getStructuredUserAgent());
            }
        }
        device.setGeofetch(getGeofetch());
        device.setGeo(getDeviceGeo());
        device.setDnt(getDnt());
        device.setJs(1);
        device.setIp("107.219.186.28");
        device.setMacsha1("");
        device.setMacmd5("");
        device.setExt(fillBidStreamExtensionsObject(mDeviceInfo));
        return device;
    }

    Geo getDeviceGeo() {
        Geo geo = new Geo();
        geo.setLat(getLatitude());
        geo.setLon(getLongitude());
        geo.setAccuracy(getAccuracy());
        geo.setUtcoffset(getUTcOffset());
        geo.setType(1);
        return geo;
    }

    private Integer getUTcOffset() {
        return formatUTCTime();
    }

    private Integer getAccuracy() {
        if (mLocationManager != null) {
            Location location = mLocationManager.getUserLocation();
            if (location != null) return Math.round(location.getAccuracy());
        }
        return null;
    }

    User getUser() {
        User user = new User();
        user.setYearOfBirth(getYearOfBirth());
        user.setGender(getGender());
        user.setData(getUserData());

        return user;
    }

    private List<Data> getUserData() {

        ArrayList<Data> dataList = new ArrayList<>();

        if (mTopicManager != null && mTopicManager.getTopics() != null && !mTopicManager.getTopics().isEmpty()) {

            Map<Long, ArrayList<Topic>> sortedTopics = new HashMap<>();
            for (Topic topic : mTopicManager.getTopics()) {
                Long taxonomyVersion = topic.getTaxonomyVersion();
                if (!sortedTopics.containsKey(taxonomyVersion)) {
                    sortedTopics.put(taxonomyVersion, new ArrayList<>());
                }
                Objects.requireNonNull(sortedTopics.get(taxonomyVersion)).add(topic);
            }

            for (Map.Entry<Long, ArrayList<Topic>> entry : sortedTopics.entrySet()) {
                Long key = entry.getKey();
                ArrayList<Topic> topics = entry.getValue();
                if (!topics.isEmpty()) {
                    Data topicData = new Data();
                    DataExtension ext = new DataExtension(topics.get(0).getTaxonomyVersion(), topics.get(0).getTaxonomyVersionName());
                    topicData.setExt(ext);
                    List<Segment> segment = new ArrayList<>();
                    for (Topic topic : topics) {
                        Segment seg = new Segment();
                        seg.setId(String.valueOf(topic.getId()));
                        segment.add(seg);
                    }
                    topicData.setSegment(segment);
                    dataList.add(topicData);
                }
            }
        }

        return dataList;
    }

    private Float getLatitude() {
        if (mLocationManager.getUserLocation() != null) {
            return roundToTwoDecimalPlaces((float) mLocationManager.getUserLocation().getLatitude());
        } else {
            return null;
        }
    }

    private Float getLongitude() {
        if (mLocationManager.getUserLocation() != null) {
            return roundToTwoDecimalPlaces((float) mLocationManager.getUserLocation().getLongitude());
        } else {
            return null;
        }
    }

    private int getDnt() {
        if (HyBid.isCoppaEnabled() || mLimitTracking || TextUtils.isEmpty(mAdvertisingId) || mIsCCPAOptOut || mUserDataManager.isConsentDenied()) {
            return 1;
        } else return 0;
    }

    private Integer getYearOfBirth() {
        int age;
        String ageString = HyBid.getAge();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int yearOfBirth;

        if (!TextUtils.isEmpty(ageString)) {
            age = Integer.parseInt(ageString);
            yearOfBirth = currentYear - age;
            return yearOfBirth;
        } else return null;
    }

    private Regs getRegs() {
        Regs regs = new Regs();
        if (mUserDataManager != null) {
            regs.setExt(getExt());
        }
        return regs;
    }

    private Ext getExt() {
        Ext ext = new Ext();
        if (mUserDataManager != null) {
            String gppString = mUserDataManager.getGppString();
            if (!TextUtils.isEmpty(gppString)) {
                ext.setGpp(gppString);
            }
            String gppIdString = mUserDataManager.getGppSid();
            if (!TextUtils.isEmpty(gppIdString)) {
                String[] splitResult = gppIdString.split("_");
                ArrayList<Integer> gppsid = new ArrayList<>();
                for (String s : splitResult) {
                    try {
                        Integer id = Integer.parseInt(s);
                        gppsid.add(id);
                    } catch (Exception e) {
                        Logger.e(TAG, e.getMessage());
                    }
                }
                if (!gppsid.isEmpty()) ext.setGppSid(gppsid);
            }
        }
        return ext;
    }

    private int getTestInt() {
        return HyBid.isTestMode() ? 1 : 0;
    }

    private String getGender() {
        String gender = HyBid.getGender();
        if (!TextUtils.isEmpty(gender)) {
            return gender;
        } else return null;
    }

    private List<Integer> getSupportedProtocols() {
        List<Integer> supportedProtocols = new ArrayList<>();
        supportedProtocols.add(Integer.parseInt(Protocol.VAST_1_0));
        supportedProtocols.add(Integer.parseInt(Protocol.VAST_2_0));
        supportedProtocols.add(Integer.parseInt(Protocol.VAST_3_0));
        supportedProtocols.add(Integer.parseInt(Protocol.VAST_1_0_WRAPPER));
        supportedProtocols.add(Integer.parseInt(Protocol.VAST_2_0_WRAPPER));
        supportedProtocols.add(Integer.parseInt(Protocol.VAST_3_0_WRAPPER));
        supportedProtocols.add(Integer.parseInt(Protocol.VAST_4_0));
        supportedProtocols.add(Integer.parseInt(Protocol.VAST_4_0_WRAPPER));
        supportedProtocols.add(Integer.parseInt(Protocol.VAST_4_1));
        supportedProtocols.add(Integer.parseInt(Protocol.VAST_4_1_WRAPPER));
        supportedProtocols.add(Integer.parseInt(Protocol.VAST_4_2));
        supportedProtocols.add(Integer.parseInt(Protocol.VAST_4_2_WRAPPER));

        return supportedProtocols;
    }

    private List<Integer> getSupportedApis() {
        List<Integer> supportedApis = new ArrayList<>();
        supportedApis.add(Integer.parseInt(Api.MRAID_1));
        supportedApis.add(Integer.parseInt(Api.MRAID_2));
        supportedApis.add(Integer.parseInt(Api.MRAID_3));
        supportedApis.add(Integer.parseInt(Api.OMID_1));

        return supportedApis;
    }

    private Integer getGeofetch() {
        return 1;
    }

    private Float roundToTwoDecimalPlaces(Float value) {
        if (value != null) {
            return Math.round(value * 100.0f) / 100.0f;
        } else {
            return null;
        }
    }
}
