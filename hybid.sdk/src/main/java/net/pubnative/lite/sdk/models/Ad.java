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
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;
import net.pubnative.lite.sdk.views.PNAPIContentInfoView;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ad extends JsonModel implements Serializable {

    private static final String TAG = Ad.class.getSimpleName();
    private static final String DATA_CONTENTINFO_LINK_KEY = "link";
    private static final String DATA_CONTENTINFO_ICON_KEY = "icon";
    private static final String DATA_POINTS_NUMBER_KEY = "number";
    private static final String DATA_TEXT_KEY = "text";

    private static final String PN_IMPRESSION_URL = "got.pubnative.net";
    private static final String PN_IMPRESSION_QUERY_PARAM = "t";
    private static final int MIN_POINTS = 10;

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

    public Ad() {
    }

    public Ad(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
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
            result = "<![CDATA[\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta name=\"HandheldFriendly\" content=\"true\"/>\n" +
                    "    <meta name=\"MobileOptimized\" content=\"width\"/>\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, minimum-scale=1, maximum-scale=1, user-scalable=no\"/>\n" +
                    "</head>\n" +
                    "<body style=\"margin: 0; padding: 0\">\n" +
                    "<div class=\"vrvWrap\" style=\"width: 100%; height: 100%;\"><img style=\"display: none;\" onerror=\"(function(w, d, i) { var tagElement = i.parentNode;\n" +
                    "\t\t\tvar c = {\n" +
                    "\t\t\t'creativeParams': {\n" +
                    "\t\t\t'variant': '',\n" +
                    "\t\t\t'landingPageUrl': 'https://www.peapod.com/pages/summer-grilling?utm_source=Verve&amp;utm_medium=Display&amp;utm_campaign=Universal&amp;c3ch=Mobile&amp;c3nid=VERVE-',\n" +
                    "\t\t\t'segmentId': ''\n" +
                    "\t\t\t},\n" +
                    "\t\t\t'trackImp': 'https://414-img.c3tag.com/v.gif?cid=414&amp;c3ch=Mobile&amp;c3nid=VERVE-',\n" +
                    "\t\t\t'trackClick': 'https://www.peapod.com/pages/summer-grilling?utm_source=Verve&amp;utm_medium=Display&amp;utm_campaign=Universal&amp;c3ch=Mobile&amp;c3nid=VERVE-',\n" +
                    "\t\t\t'type': 'expandable',\n" +
                    "\t\t\t'width': '320',\n" +
                    "\t\t\t'height': '50',\n" +
                    "\t\t\t'bannerUrl': 'https://ad.vrvm.com/creative/custom/giantfood/nat8233-p6/NAT-8237/expandable-banner/banner.html',\n" +
                    "\t\t\t'panelUrl': 'https://ad.vrvm.com/creative/custom/giantfood/nat8233-p6/NAT-8237/expandable-banner/panel.html',\n" +
                    "\t\t\t'expandedUrl': 'https://tags-prod.vrvm.com/tags/dYpGlm/expanded.html?tagjs_version=1.6.0',\n" +
                    "\t\t\t'transition': 'fade'\n" +
                    "\t\t\t};\n" +
                    "\t\t\t'function'!=typeof Object.assign&amp;&amp;!function(){Object.assign=function(n){'use strict';if(void 0===n||null===n)throw new TypeError('Cannot convert undefined or null to object');for(var t=Object(n),r=1;r<arguments.length;r++){var e=arguments[r];if(void 0!==e&amp;&amp;null!==e)for(var o in e)e.hasOwnProperty(o)&amp;&amp;(t[o]=e[o])}return t}}();\n" +
                    "\t\t\tvar d = {\n" +
                    "\t\t\t'trackBase': 'http%3A%2F%2Fgo.vrvm.com%2Ft%3Fc%3D2785659%26f%3D1%26adnet%3D54%26paid%3D5723%26poid%3D38%26r%3D0000015b5ed5e071114333ba94d5feda%26uis%3Di%26countryCode%3DUS',\n" +
                    "\t\t\t'appmw': 'app',\n" +
                    "\t\t\t'latitude': '32.95',\n" +
                    "\t\t\t'longitude': '-117.22',\n" +
                    "\t\t\t'flightId': '1',\n" +
                    "\t\t\t'creativeId': '2785659',\n" +
                    "\t\t\t'requestId': '0000015b5ed5e071114333ba94d5feda',\n" +
                    "\t\t\t'adnetId': '54',\n" +
                    "\t\t\t'dfpUrl': ''\n" +
                    "\t\t\t};\n" +
                    "\t\t\tObject.assign(c, d);\n" +
                    "\t\t\tvar script = document.createElement('script'); script.src = 'https://creative-platform.vrvm.com/tagjs/tag.1.6.0.js'; script.onload = function() { Verve.controllers.factories.AdFactory.makeAd(tagElement, c); }; tagElement.appendChild(script); })(window, document, this);\"\n" +
                    "                                                             src=\"data:image/png,vrvm\"/></div>\n" +
                    "<div style=\"position:absolute; z-index:-9999; display:none;\">\n" +
                    "    <!-- DELETE THIS LINE AND ADD ALL THIRD PARTY TRACKERS HERE -->\n" +
                    "    <img src=\"https://p.placed.com/api/v2/sync/impression?partner=verve&amp;version=1.0&amp;plaid=giantfood_csmp6_951&amp;payload_campaign_identifier=&amp;payload_device_identifier=&amp;payload_timestamp=&amp;payload_type=impression&amp;t_creative=\"\n" +
                    "         border=\"0\" width=\"1\" height=\"1\"/>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html><img\n" +
                    "        src=\"http://go.vrvm.com/t?r=0000015b5ed5e071114333ba94d5feda&amp;e=AdImpInternal&amp;adnet=54&amp;countryCode=US&amp;paid=5723&amp;poid=38&amp;uis=I&amp;f=1&amp;c=2785659\"\n" +
                    "        width=\"1\" height=\"1\" style=\"display:none;\"/>]]>\n";
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

    public View getContentInfo(Context context) {
        PNAPIContentInfoView result = null;
        AdData data = getMeta(APIMeta.CONTENT_INFO);
        if (data == null) {
            Log.e(TAG, "getContentInfo - contentInfo data not found");
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
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PNAPIContentInfoView) view).openLayout();
                }
            });
        }
        return result;
    }

    public RelativeLayout getContentInfoContainer(Context context) {
        View contentInfo = getContentInfo(context);
        if (contentInfo != null) {
            RelativeLayout contentInfoContainer = new RelativeLayout(context);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            contentInfoContainer.setLayoutParams(layoutParams);
            contentInfoContainer.addView(contentInfo);
            return contentInfoContainer;
        } else {
            return null;
        }
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
}
