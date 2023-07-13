package net.pubnative.lite.sdk.models;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.DisplayManager;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.UserDataManager;
import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.models.request.App;
import net.pubnative.lite.sdk.models.request.Banner;
import net.pubnative.lite.sdk.models.request.Device;
import net.pubnative.lite.sdk.models.request.Format;
import net.pubnative.lite.sdk.models.request.Geo;
import net.pubnative.lite.sdk.models.request.Imp;
import net.pubnative.lite.sdk.models.request.Metric;
import net.pubnative.lite.sdk.models.request.Native;
import net.pubnative.lite.sdk.models.request.OpenRTBAdRequest;
import net.pubnative.lite.sdk.models.request.User;
import net.pubnative.lite.sdk.models.request.Video;
import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNAsyncUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OpenRTBAdRequestFactory implements AdRequestFactory {
    private static final String TAG = OpenRTBAdRequestFactory.class.getSimpleName();

    private final DeviceInfo mDeviceInfo;
    private final HyBidLocationManager mLocationManager;
    private final UserDataManager mUserDataManager;
    private final DisplayManager mDisplayManager;
    private IntegrationType mIntegrationType = IntegrationType.HEADER_BIDDING;
    private String mMediationVendor;
    private boolean mIsRewarded;
    private boolean mLimitTracking;
    private String mAdvertisingId;
    private boolean mIsCCPAOptOut;

    public OpenRTBAdRequestFactory() {
        this(HyBid.getDeviceInfo(), HyBid.getLocationManager(), HyBid.getUserDataManager(), new DisplayManager());
    }

    OpenRTBAdRequestFactory(DeviceInfo deviceInfo, HyBidLocationManager locationManager, UserDataManager userDataManager, DisplayManager displayManager) {
        mDeviceInfo = deviceInfo;
        mLocationManager = locationManager;
        mUserDataManager = userDataManager;
        mDisplayManager = displayManager;
    }

    @Override
    public void createAdRequest(String appToken, String zoneid, AdSize adSize, boolean isRewarded, Callback callback) {
        mAdvertisingId = mDeviceInfo.getAdvertisingId();
        mLimitTracking = mDeviceInfo.limitTracking();
        Context context = mDeviceInfo.getContext();
        mIsRewarded = isRewarded;
        if (TextUtils.isEmpty(mAdvertisingId) && context != null) {
            try {
                PNAsyncUtils.safeExecuteOnExecutor(new HyBidAdvertisingId(context, new HyBidAdvertisingId.Listener() {
                    @Override
                    public void onHyBidAdvertisingIdFinish(String advertisingId, Boolean limitTracking) {
                        processAdvertisingId(appToken, zoneid, adSize, advertisingId, limitTracking, callback);
                    }
                }));
            } catch (Exception exception) {
                Logger.e(TAG, "Error executing HyBidAdvertisingId AsyncTask");
            }
        } else {
            processAdvertisingId(appToken, zoneid, adSize, mAdvertisingId, mLimitTracking, callback);
        }
    }

    @Override
    public AdRequest buildRequest(String appToken, String zoneid, AdSize adSize, String advertisingId, boolean limitTracking, IntegrationType integrationType, String mediationVendor, Integer impDepth) {
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

    private void processAdvertisingId(String appToken, String zoneId, AdSize adSize, String advertisingId, boolean limitTracking, PNAdRequestFactory.Callback callback) {
        if (callback != null) {
            callback.onRequestCreated(buildRequest(appToken, zoneId, adSize, advertisingId, limitTracking, mIntegrationType, mMediationVendor, 0));
        }
    }

    List<Imp> getImpressions(AdSize adSize, String mediationVendor, IntegrationType integrationType) {
        List<Imp> imps = new ArrayList<>();

        if (adSize == AdSize.SIZE_INTERSTITIAL
                || adSize == AdSize.SIZE_300x250
                || adSize == AdSize.SIZE_320x480
                || adSize == AdSize.SIZE_480x320
                || adSize == AdSize.SIZE_768x1024
                || adSize == AdSize.SIZE_1024x768) {
            imps.add(getVideoImpression(adSize, mediationVendor, integrationType));
        }
        imps.add(getBannerImpression(adSize, mediationVendor, integrationType));

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
        imp.setInstl(0);
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
        imp.setInstl(0);
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

        banner.setPos(0);

        List<String> mimes = new ArrayList<>();
        banner.setMimes(mimes);

        banner.setTopframe(1);

        List<Integer> expdir = new ArrayList<>();
        banner.setExpdir(expdir);

        List<Integer> apis = new ArrayList<>();
        banner.setApi(apis);

        banner.setId("");
        banner.setVcm(0);

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
        device.setUserAgent(mDeviceInfo.getUserAgent());
        device.setGeo(getDeviceGeo());
        device.setDnt(getDnt());
        device.setDeviceType(1);
        device.setIp("107.219.186.28");
        device.setModel(mDeviceInfo.getModel());
        device.setOs("Android");
        device.setOsVersion(mDeviceInfo.getOSVersion());
        device.setH(Integer.parseInt(mDeviceInfo.getDeviceHeight()));
        device.setW(Integer.parseInt(mDeviceInfo.getDeviceWidth()));
        device.setLanguage(mDeviceInfo.getLocale().toString());
        device.setConnectiontype(2);
        device.setIfa(mDeviceInfo.getAdvertisingId());
        device.setDpidsha1(mDeviceInfo.getAdvertisingIdSha1());
        device.setDpidmd5(mDeviceInfo.getAdvertisingIdMd5());
        device.setMacsha1("");
        device.setMacmd5("");
        return device;
    }

    Geo getDeviceGeo() {
        Geo geo = new Geo();
        geo.setLat(getLatitude());
        geo.setLon(getLongitude());
        geo.setType(1);
        return geo;
    }

    User getUser() {
        User user = new User();
        user.setYearOfBirth(getYearOfBirth());
        user.setGender(getGender());

        return user;
    }

    private Float getLatitude() {
        if (mLocationManager.getUserLocation() != null) {
            return (float) mLocationManager.getUserLocation().getLatitude();
        } else return null;
    }

    private Float getLongitude() {
        if (mLocationManager.getUserLocation() != null) {
            return (float) mLocationManager.getUserLocation().getLongitude();
        } else return null;
    }

    private int getDnt() {
        if (HyBid.isCoppaEnabled() || mLimitTracking || TextUtils.isEmpty(mAdvertisingId)
                || mIsCCPAOptOut || mUserDataManager.isConsentDenied()) {
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

    private int getTestInt() {
        return HyBid.isTestMode() ? 1 : 0;
    }

    private String getGender() {
        String gender = HyBid.getGender();
        if (!TextUtils.isEmpty(gender)) {
            return gender;
        } else return null;
    }
}
