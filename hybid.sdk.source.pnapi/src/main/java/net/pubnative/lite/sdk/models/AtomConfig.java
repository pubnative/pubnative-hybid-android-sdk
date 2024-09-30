package net.pubnative.lite.sdk.models;

import static net.pubnative.lite.sdk.models.APIAsset.ATOM_ENABLED;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class AtomConfig extends JsonModel {

    @BindField
    public List<AdData> app_level;

    public AtomConfig() {
    }

    public AtomConfig(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public Boolean isAtomEnabled() {
        if (app_level == null || app_level.isEmpty())
            return false;
        AdData adData = null;
        for (AdData data : app_level) {
            if (data.type.equals(ATOM_ENABLED)) {
                adData = data;
                break;
            }
        }
        return adData != null && adData.getBooleanField("boolean");
    }
}
