package net.pubnative.lite.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.text.TextUtils;

import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.models.IdApp;
import net.pubnative.lite.sdk.models.IdBattery;
import net.pubnative.lite.sdk.models.IdDevice;
import net.pubnative.lite.sdk.models.IdGgl;
import net.pubnative.lite.sdk.models.IdLocation;
import net.pubnative.lite.sdk.models.IdModel;
import net.pubnative.lite.sdk.models.IdOs;
import net.pubnative.lite.sdk.models.IdPrivacy;
import net.pubnative.lite.sdk.models.IdUser;
import net.pubnative.lite.sdk.models.IdUserVendor;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNCrypto;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class VgiIdManager {
    private static final String TAG = VgiIdManager.class.getSimpleName();

    private static final String PREFERENCES_VGI_ID = "net.pubnative.lite.vgiid";
    private static final String KEY_VGI_ID = "VGI_id";

    private final Context mContext;
    private final SharedPreferences mPreferences;
    private final String mAppToken;

    public VgiIdManager(Context context) {
        mContext = context.getApplicationContext();
        mPreferences = mContext.getSharedPreferences(PREFERENCES_VGI_ID, Context.MODE_PRIVATE);
        mAppToken = HyBid.getAppToken();
    }

    public void init(){
        UserDataManager userDataManager = HyBid.getUserDataManager();
        DeviceInfo deviceInfo = HyBid.getDeviceInfo();
        HyBidLocationManager locationManager = HyBid.getLocationManager();

        IdModel mIdModel = new IdModel();

        mIdModel.apps = getApps(userDataManager, deviceInfo);
        mIdModel.device = getDevice(deviceInfo);
        mIdModel.users = getUsers(deviceInfo, locationManager);

        setVgiIdModel(mIdModel);
    }

    public IdModel getVgiIdModel() {
        String vgiId = mPreferences.getString(KEY_VGI_ID, null);
        JSONObject vgiIdJson;
        IdModel vgiIdModel = null;

        if (!TextUtils.isEmpty(vgiId)) {
            try {
                String decryptedVgiIdString = PNCrypto.decryptString(vgiId, mAppToken);

                if (decryptedVgiIdString != null) {
                    vgiIdJson = new JSONObject(decryptedVgiIdString);
                } else {
                    vgiIdJson = new JSONObject();
                }
                vgiIdModel = new IdModel(vgiIdJson);
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
            }
        }
        return vgiIdModel;
    }

    public void setVgiIdModel(IdModel idModel){
        if (idModel != null) {
            String idModelString = null;
            try {
                idModelString = idModel.toJson().toString();
                String encryptedIdModel = PNCrypto.encryptString(idModelString, mAppToken);

                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(KEY_VGI_ID, encryptedIdModel);
                editor.apply();
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
            }
        }
    }

    private List<IdApp> getApps(UserDataManager userDataManager, DeviceInfo deviceInfo){
        List<IdApp> apps = new ArrayList<>();
        IdApp app = new IdApp();

        IdPrivacy privacy = new IdPrivacy();
        privacy.iab_ccpa = userDataManager.getIABUSPrivacyString();
        // todo Rework the getIABGDPRConsentString() into two, so we can get tcfv1 and tcfv2 separately
        privacy.tcfv2 = userDataManager.getIABGDPRConsentString();
        privacy.lat = deviceInfo.limitTracking();

        app.bundle_id = HyBid.getBundleId();
        app.privacy = privacy;

        apps.add(app);

        return apps;
    }

    private IdDevice getDevice(DeviceInfo deviceInfo){
        IdDevice idDevice = new IdDevice();
        idDevice.id = Build.ID;

        IdOs idOs = new IdOs();
        idOs.name = "Android";
        idOs.version = deviceInfo.getOSVersion();

        IdBattery idBattery = new IdBattery();
        Long batteryCapacity = getBatteryCapacity(mContext);
        if (batteryCapacity != -1) {
            idBattery.capacity = String.valueOf(batteryCapacity);
        }
        idBattery.charging = isBatteryCharging(mContext);

        idDevice.os = idOs;
        idDevice.manufacture = Build.MANUFACTURER;
        idDevice.model = Build.MODEL;
        idDevice.brand = Build.BRAND;
        idDevice.battery = idBattery;

        return idDevice;
    }

    private List<IdUser> getUsers(DeviceInfo deviceInfo, HyBidLocationManager locationManager){
        List<IdUser> users = new ArrayList<>();
        IdUser user = new IdUser();

        IdUserVendor vendor = new IdUserVendor();
        IdGgl idGgl = new IdGgl();

        idGgl.GAID = deviceInfo.getAdvertisingId();

        vendor.GGL = idGgl;

        List<IdLocation> locations = new ArrayList<>();
        IdLocation location = new IdLocation();

        if (locationManager != null && locationManager.getUserLocation() != null) {
            Location loc = locationManager.getUserLocation();

            location.lat = String.valueOf(loc.getLatitude());
            location.lon = String.valueOf(loc.getLongitude());
            location.accuracy = String.valueOf(loc.getAccuracy());
            location.ts = String.valueOf(loc.getTime());
        }
        locations.add(location);

        user.vendors = vendor;
        user.locations = locations;

        users.add(user);

        return users;
    }

    private long getBatteryCapacity(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            if (mBatteryManager != null) {
                Integer chargeCounter = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                Integer capacity = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                if (chargeCounter == Integer.MIN_VALUE || capacity == Integer.MIN_VALUE)
                    return -1;

                return (chargeCounter / capacity) * 100L;
            }
        }
        return -1;
    }

    private Boolean isBatteryCharging(Context context){
        Boolean isCharging = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BatteryManager mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            if (mBatteryManager != null) {
                isCharging = mBatteryManager.isCharging();
            }
        }
        return isCharging;
    }

}
