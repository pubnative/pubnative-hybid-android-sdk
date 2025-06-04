// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class SdkConfig extends JsonModel {

    @BindField
    public List<AdData> app_level;

    public SdkConfig() {
    }

    public SdkConfig(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public Boolean isAtomEnabled() {
        if (app_level == null || app_level.isEmpty())
            return false;
        AdData adData = null;
        for (AdData data : app_level) {
            if (data.type.equals(ConfigAssets.ATOM_ENABLED)) {
                adData = data;
                break;
            }
        }
        return adData != null && adData.getBoolean();
    }

    public Boolean isExperienceEnabled() {
        if (app_level == null || app_level.isEmpty())
            return false;
        AdData adData = null;
        for (AdData data : app_level) {
            if (data.type.equals(ConfigAssets.EXPERIENCE_ENABLED)) {
                adData = data;
                break;
            }
        }
        return adData != null && adData.getBoolean();
    }
}
