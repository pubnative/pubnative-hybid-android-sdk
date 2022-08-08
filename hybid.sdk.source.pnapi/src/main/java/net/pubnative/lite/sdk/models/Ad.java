// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.models;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.utils.ViewUtils;
import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;
import net.pubnative.lite.sdk.views.PNAPIContentInfoView;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ad extends JsonModel implements Serializable, Comparable<Ad> {

    private static final String TAG = Ad.class.getSimpleName();
    private static final String DATA_CONTENTINFO_LINK_KEY = "link";
    private static final String DATA_CONTENTINFO_ICON_KEY = "icon";
    private static final String DATA_POINTS_NUMBER_KEY = "number";
    private static final String DATA_TEXT_KEY = "text";

    private static final String PN_IMPRESSION_URL = "got.pubnative.net";
    private static final String PN_IMPRESSION_QUERY_PARAM = "t";
    private static final int MIN_POINTS = 10;

    private static final String CONTENT_INFO_LINK_URL = "https://pubnative.net/content-info";
    private static final String CONTENT_INFO_ICON_URL = "https://cdn.pubnative.net/static/adserver/contentinfo.png";
    private static final String CONTENT_INFO_TEXT = "Learn about this ad";

    //==============================================================================================
    // Fields
    //==============================================================================================
    @BindField
    public String link;
    @BindField
    public int assetgroupid;
    @BindField
    public List<AdData> assets;
    @BindField
    public List<AdData> beacons;
    @BindField
    public List<AdData> meta;

    private String zoneId;
    private String adSourceName;
    private boolean hasEndCard = false;

    //==============================================================================================
    // Interfaces
    //==============================================================================================

    /**
     * Interface containing all possible Beacons
     */
    public interface Beacon {

        String IMPRESSION = "impression";
        String CLICK = "click";
    }

    public enum AdType {
        HTML, VIDEO
    }

    public Ad() {
    }

    public Ad(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public Ad(int assetGroupId, String adValue, AdType type) {
        this.assetgroupid = assetGroupId;
        String apiAsset;
        assets = new ArrayList<>();
        AdData adData;
        if (type == AdType.VIDEO) {
            apiAsset = APIAsset.VAST;
            adData = new AdData("vast2", apiAsset, adValue);
        } else {
            apiAsset = APIAsset.HTML_BANNER;
            adData = new AdData("html", apiAsset, adValue);
        }
        assets.add(adData);
    }

    //==============================================================================================
    // Asset
    //==============================================================================================
    public AdData getAsset(String type) {

        return find(type, assets);
    }

    public AdData getMeta(String type) {

        return find(type, meta);
    }

    public List<AdData> getBeacons(String type) {

        return findAll(type, beacons);
    }

    protected AdData find(String type, List<AdData> list) {

        AdData result = null;
        if (list != null) {
            for (AdData data : list) {
                if (type.equals(data.type)) {
                    result = data;
                    break;
                }
            }
        }
        return result;
    }

    protected List<AdData> findAll(String type, List<AdData> list) {

        List<AdData> result = null;
        if (list != null) {
            for (AdData data : list) {
                if (type.equals(data.type)) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(data);
                }
            }
        }
        return result;
    }

    /**
     * Gets url of the assets (html banner page, standard banner etc.)
     *
     * @param asset asset name for which url requested.
     * @return valid String with the url value, null if not present.
     */
    public String getAssetUrl(String asset) {
        String result = null;
        AdData data = getAsset(asset);
        if (data != null) {
            result = data.getURL();
        }
        return result;
    }

    public String getAssetHtml(String asset) {
        String result = null;
        AdData data = getAsset(asset);
        if (data != null) {
            result = data.getHtml();
        }
        return result;
    }

    public int getAssetWidth(String asset) {
        int result = -1;
        AdData data = getAsset(asset);
        if (data != null) {
            result = data.getWidth();
        }
        return result;
    }

    public int getAssetHeight(String asset) {
        int result = -1;
        AdData data = getAsset(asset);
        if (data != null) {
            result = data.getHeight();
        }
        return result;
    }

    public String getVast() {
        String result = null;
        AdData data = getAsset(APIAsset.VAST);
        if (data != null) {
            result = data.getStringField("vast2");
        }
        return result;
    }

    public View getContentInfo(Context context,
                               boolean feedbackEnabled,
                               PNAPIContentInfoView.ContentInfoListener listener) {
        return getContentInfo(context, null, feedbackEnabled, listener);
    }

    public View getContentInfo(Context context,
                               ContentInfo contentInfo,
                               boolean feedbackEnabled,
                               PNAPIContentInfoView.ContentInfoListener listener) {
        PNAPIContentInfoView result = null;
        AdData data = getMeta(APIMeta.CONTENT_INFO);
        if (data == null) {
            Log.e(TAG, "getContentInfo - contentInfo data not found");
            return getDefaultContentInfo(context, feedbackEnabled, listener);
        } else if (TextUtils.isEmpty(data.getStringField(DATA_CONTENTINFO_ICON_KEY))) {
            Log.e(TAG, "getContentInfo - contentInfo icon not found");
        } else if (TextUtils.isEmpty(data.getStringField(DATA_CONTENTINFO_LINK_KEY))) {
            Log.e(TAG, "getContentInfo - contentInfo link not found");
        } else if (TextUtils.isEmpty(data.getText())) {
            Log.e(TAG, "getContentInfo - contentInfo text not found");
        } else {
            result = new PNAPIContentInfoView(context);
            result.setIconUrl(data.getStringField(DATA_CONTENTINFO_ICON_KEY));
            result.setIconClickUrl(data.getStringField(DATA_CONTENTINFO_LINK_KEY));
            result.setContextText(data.getText());
            result.setContentInfoListener(listener);
            result.setAdFeedbackEnabled(feedbackEnabled);
            result.setOnClickListener(view -> ((PNAPIContentInfoView) view).openLayout());
        }
        return result;
    }

    public FrameLayout getContentInfoContainer(Context context, boolean feedbackEnabled,
                                               PNAPIContentInfoView.ContentInfoListener listener) {
        return getContentInfoContainer(context, null, feedbackEnabled, listener);
    }

    public FrameLayout getContentInfoContainer(Context context,
                                               ContentInfo contentInfo,
                                               boolean feedbackEnabled,
                                               PNAPIContentInfoView.ContentInfoListener listener) {
        View contentInfoView = getCustomContentInfo(context, contentInfo, feedbackEnabled, listener);

        if (contentInfoView == null) {
            contentInfoView = getContentInfo(context, feedbackEnabled, listener);
        }

        if (contentInfoView != null) {
            FrameLayout contentInfoContainer = new FrameLayout(context);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);

            contentInfoContainer.setLayoutParams(layoutParams);
            contentInfoContainer.addView(contentInfoView);
            return contentInfoContainer;
        } else {
            return null;
        }
    }

    private PNAPIContentInfoView getCustomContentInfo(Context context,
                                                      ContentInfo contentInfo,
                                                      boolean feedbackEnabled,
                                                      PNAPIContentInfoView.ContentInfoListener listener) {
        if (contentInfo == null
                || TextUtils.isEmpty(contentInfo.getIconUrl())
                || TextUtils.isEmpty(contentInfo.getLinkUrl())) {
            return null;
        } else {
            PNAPIContentInfoView result = new PNAPIContentInfoView(context);
            result.setIconUrl(contentInfo.getIconUrl());
            result.setIconClickUrl(contentInfo.getLinkUrl());
            if (TextUtils.isEmpty(contentInfo.getText())) {
                result.setContextText(CONTENT_INFO_TEXT);
            } else {
                result.setContextText(contentInfo.getText());
            }
            if (contentInfo.getWidth() != -1 && contentInfo.getHeight() != -1) {
                result.setDpDimensions(contentInfo);
            }
            result.setAdFeedbackEnabled(feedbackEnabled);
            result.setContentInfoListener(listener);
            result.setOnClickListener(view -> ((PNAPIContentInfoView) view).openLayout());
            return result;
        }
    }

    private PNAPIContentInfoView getDefaultContentInfo(Context context,
                                                       boolean feedbackEnabled,
                                                       PNAPIContentInfoView.ContentInfoListener listener) {
        PNAPIContentInfoView result = new PNAPIContentInfoView(context);
        result.setIconUrl(CONTENT_INFO_ICON_URL);
        result.setIconClickUrl(CONTENT_INFO_LINK_URL);
        result.setContextText(CONTENT_INFO_TEXT);
        result.setContentInfoListener(listener);
        result.setAdFeedbackEnabled(feedbackEnabled);
        result.setOnClickListener(view -> ((PNAPIContentInfoView) view).openLayout());
        return result;
    }

    /**
     * Gets content info icon url
     *
     * @return icon url of content info
     */
    public String getContentInfoIconUrl() {
        AdData data = getMeta(APIMeta.CONTENT_INFO);
        return data.getStringField(DATA_CONTENTINFO_ICON_KEY);
    }

    /**
     * Gets content info click url
     *
     * @return click url of content info
     */
    public String getContentInfoClickUrl() {
        AdData data = getMeta(APIMeta.CONTENT_INFO);
        return data.getStringField(DATA_CONTENTINFO_LINK_KEY);
    }

    public Integer getECPM() {
        AdData adData = getMeta(APIMeta.POINTS);

        if (adData == null) {
            return MIN_POINTS;
        }

        Integer points = adData.getIntField(DATA_POINTS_NUMBER_KEY);

        return points == null ? MIN_POINTS : points;
    }

    public String getCreativeId() {
        AdData adData = getMeta(APIMeta.CREATIVE_ID);

        if (adData == null) {
            return "";
        }

        String creativeId = adData.getStringField(DATA_TEXT_KEY);

        return TextUtils.isEmpty(creativeId) ? "" : creativeId;
    }

    public Integer getUnskippableVideoDuration() {
        AdData adData = getMeta(APIMeta.RENDERING_OPTIONS);

        if (adData == null) {
            return 0;
        }

        Integer unskippableVideoDuration = adData.getIntField("unskippableVideoDuration");

        return unskippableVideoDuration == null ? 0 : unskippableVideoDuration;
    }

    public Integer getMinimumSkipOffset() {
        AdData adData = getMeta(APIMeta.RENDERING_OPTIONS);

        if (adData == null) {
            return 0;
        }

        Integer minimumSkipOffset = adData.getIntField("minimumSkipOffset");

        return minimumSkipOffset == null ? 0 : minimumSkipOffset;
    }

    public Integer getMaximumSkipOffset() {
        AdData adData = getMeta(APIMeta.RENDERING_OPTIONS);

        if (adData == null) {
            return 0;
        }

        Integer maximumSkipOffset = adData.getIntField("maximumSkipOffset");

        return maximumSkipOffset == null ? 0 : maximumSkipOffset;
    }

    public String getImpressionId() {
        List<AdData> impressionBeacons = getBeacons(Beacon.IMPRESSION);

        boolean found = false;
        int index = 0;

        String impressionId = "";

        while (index < impressionBeacons.size() && !found) {
            AdData data = impressionBeacons.get(index);

            if (!TextUtils.isEmpty(data.getURL())) {
                Uri uri = Uri.parse(data.getURL());
                if (uri.getAuthority().equals(PN_IMPRESSION_URL)) {
                    String idParam = uri.getQueryParameter(PN_IMPRESSION_QUERY_PARAM);
                    if (!TextUtils.isEmpty(idParam)) {
                        impressionId = idParam;
                        found = true;
                    }
                }
            }

            index++;
        }

        return impressionId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setAdSourceName(String adSourceName) {
        this.adSourceName = adSourceName;
    }

    public String getAdSourceName() {
        return adSourceName;
    }

    public void setHasEndCard(boolean hasEndCard) {
        this.hasEndCard = hasEndCard;
    }

    public boolean hasEndCard() {
        return hasEndCard;
    }

    @Override
    public int compareTo(Ad otherAd) {
        return (otherAd.getECPM() != null ? otherAd.getECPM() : 0) - (this.getECPM() != null ? this.getECPM() : 0);
    }
}
