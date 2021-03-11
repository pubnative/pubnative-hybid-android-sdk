package com.monet.bidder.mapper;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class PlacementMappingManager {
    private static final String TAG = PlacementMappingManager.class.getSimpleName();
    private static final String MAPPING_FILE_NAME = "hybid_mapping.json";

    private static volatile PlacementMappingManager sInstance;
    private ZoneIdMappingModel mModel;

    private PlacementMappingManager(Context context) {
        String mappingJson = textFromAsset(context);
        if (!TextUtils.isEmpty(mappingJson)) {
            try {
                mModel = new ZoneIdMappingModel(new JSONObject(mappingJson));
            } catch (Exception exception) {
                Logger.e(TAG, exception.getMessage());
            }
        }
    }

    public static PlacementMappingManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PlacementMappingManager.class) {
                if (sInstance == null) {
                    sInstance = new PlacementMappingManager(context);
                }
            }
        }
        return sInstance;
    }

    private String textFromAsset(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            inputStream = context.getResources().getAssets().open(MAPPING_FILE_NAME);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception exception) {
            Logger.e(TAG, "Error parsing mapping file: ", exception);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception exception) {
                Logger.e(TAG, exception.getMessage());
            }
        }

        return stringBuilder.toString();
    }

    public AdRequestInfo getEcpmMapping(AdSize adSize, Double ecpm) {
        if (mModel == null
                || mModel.getAdSizes() == null
                || mModel.getAdSizes().isEmpty()
                || TextUtils.isEmpty(mModel.getAppToken())
                || ecpm == null) {
            return null;
        }

        String adSizeKey = adSizeLabel(adSize);
        Mappings sizeMappings = mModel.getAdSizes().get(adSizeKey);

        if (sizeMappings == null || sizeMappings.getMappings() != null && sizeMappings.getMappings().isEmpty()) {
            return null;
        }

        String zoneId = sizeMappings.getMappings().get(ecpm);
        if (TextUtils.isEmpty(zoneId)) {
            return null;
        }

        return new AdRequestInfo(mModel.getAppToken(), zoneId);
    }

    private String adSizeLabel(AdSize adSize) {
        switch (adSize) {
            case SIZE_INTERSTITIAL: return "fullscreen";
            case SIZE_768x1024: return "768x1024";
            case SIZE_1024x768: return "1024x768";
            case SIZE_300x600: return "300x600";
            case SIZE_160x600: return "160x600";
            case SIZE_320x480: return "320x480";
            case SIZE_480x320: return "480x320";
            case SIZE_300x250: return "300x250";
            case SIZE_250x250: return "250x250";
            case SIZE_320x100: return "320x100";
            case SIZE_728x90: return "728x90";
            case SIZE_300x50: return "300x50";
            default: return "320x50";
        }
    }
}
