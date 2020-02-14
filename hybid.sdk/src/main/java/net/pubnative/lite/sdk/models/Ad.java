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
import net.pubnative.lite.sdk.utils.text.StringEscapeUtils;
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
            if (result != null) {
                result = StringEscapeUtils.unescapeJava(result);
            }
        }
        return result;
    }

    public String getAssetHtml(String asset) {
        String result = null;
        AdData data = getAsset(asset);
        if (data != null) {
            result = data.getHtml();
            if (result != null) {
                result = StringEscapeUtils.unescapeJava(result);
            }
        }
        return result;
    }

    public String getVast() {
        String result = null;
        AdData data = getAsset(APIAsset.VAST);
        if (data != null) {
            result = data.getStringField("vast2");
        }
        result = "\u003cVAST version=\"2.0\"\u003e\u003cAd id=\"50354173\"\u003e\u003cWrapper\u003e\u003cVASTAdTagURI\u003ehttps://events-ams.bidder.kayzen.io/creative?raw=2ClRJtDf5i%2BIlTp%2FcDfAovGHZZfnkPoWXszuMcMNp6OmxIg4cauPgmoCJLKwvkCEs%2FgGwvNzeDLX6xqjOsp3YkoqpPIzNNsaoFPYjWER7hevG%2BajpmDYbfn7ucPL39xwssIgUodXGI7aPGlHT24VdDL3bBYkaZNhBgsUkK1MN9indqriRbs2VuQGbdqNULCmjbiZsGpjBSshsmTowkgAnvZ73gUXKzFrdFgfsH767g%2F%2Fox7qdjYc6yD8qGsIEqoVgO%2FvxCSg7TnolBbiRk0s4eK2kgID1Qyy37TkQNsS5LnqdLXcGkedlWCcN318Mi%2FpCJW2hkkbHeJwa98lWXJxjPLrQCva5%2Fba5rUCYoWzWxWvU2C76PkG6ZkSYDBwYKOBES38ojw52lZRWqV7WGcoyB6rU8eiwZhUh4aSkB6BZtpQ5Qv7QElVOfCGXpq47LaVk0IYAlcHUpCCSY4UIjKcxkZu5KwHe3BxkwcNrr6wpuDvw5XXPWCVPMq0oaPtdTnivK8DFIwLeqNS2pDEjDenffMJe%2FQ3uCrTtRA5E%2FEoALl8kmNV6jIhUsuwGKB57pKKkzgT7mnvflbN%2Fod6xKY3sCVLcEnkhXER5GljSJjTqSv3F1j1OiBCTFdlm%2FMt%2FdzzlTEHR33EuyxDzeGneBbdRQ%3D%3D\u003c/VASTAdTagURI\u003e\u003cAdSystem\u003eBidstalk\u003c/AdSystem\u003e\u003cImpression\u003ehttps://events-ams.bidder.kayzen.io/win?raw=2ClRJtDf5i%2BIlTp%2FcDfAovGHZZfnkPoWXszuMcMNp6OmxIg4cauPgmoCJLKwvkCEs%2FgGwvNzeDLX6xqjOsp3YkoqpPIzNNsaoFPYjWER7hevG%2BajpmDYbfn7ucPL39xwssIgUodXGI7aPGlHT24VdDL3bBYkaZNhBgsUkK1MN9indqriRbs2VuQGbdqNULCmjbiZsGpjBSshsmTowkgAnvZ73gUXKzFrdFgfsH767g%2F%2Fox7qdjYc6yD8qGsIEqoVgO%2FvxCSg7TnolBbiRk0s4eK2kgID1Qyy37TkQNsS5LnqdLXcGkedlWCcN318Mi%2FpCJW2hkkbHeJwa98lWXJxjPLrQCva5%2Fba5rUCYoWzWxWvU2C76PkG6ZkSYDBwYKOBES38ojw52lZRWqV7WGcoyB6rU8eiwZhUh4aSkB6BZtpQ5Qv7QElVOfCGXpq47LaVk0IYAlcHUpCCSY4UIjKcxkZu5KwHe3BxkwcNrr6wpuDvw5XXPWCVPMq0oaPtdTnivK8DFIwLeqNS2pDEjDenffMJe%2FQ3uCrTtRA5E%2FEoALl8kmNV6jIhUsuwGKB57pKKkzgT7mnvflbN%2Fod6xKY3sCVLcEnkhXER5GljSJjTqSv3F1j1OiBCTFdlm%2FMt%2FdzzlTEHR33EuyxDzeGneBbdRQ%3D%3D\u0026amp;p=0.268817591\u0026amp;redirect=1\u003c/Impression\u003e\u003cImpression\u003ehttps://view.adjust.com/impression/5vcyacr?kayzen_click_id=118987150-1579269835-150-3-1-44-6PTnM-3f68c0d42a0a42cd9d76f3f8d1ec44e9-34-140730-50354173\u0026amp;campaign=DACH_Video_No_ID\u0026amp;adgroup=Android_DEU_pubnative\u0026amp;creative=DACH_720x1280_15s_Swiper_W\u0026amp;idfa=\u0026amp;gps_adid=\u003c/Impression\u003e\u003cCreatives\u003e\u003cCreative id=\"50354173\"\u003e\u003cLinear\u003e\u003cDuration\u003e00:00:15\u003c/Duration\u003e\u003cTrackingEvents\u003e\u003cTracking event=\"start\"\u003ehttps://events-ams.bidder.kayzen.io/event?raw=2ClRJtDf5i%2BIlTp%2FcDfAovGHZZfnkPoWXszuMcMNp6OmxIg4cauPgmoCJLKwvkCEs%2FgGwvNzeDLX6xqjOsp3YkoqpPIzNNsaoFPYjWER7hevG%2BajpmDYbfn7ucPL39xwssIgUodXGI7aPGlHT24VdDL3bBYkaZNhBgsUkK1MN9indqriRbs2VuQGbdqNULCmjbiZsGpjBSshsmTowkgAnvZ73gUXKzFrdFgfsH767g%2F%2Fox7qdjYc6yD8qGsIEqoVgO%2FvxCSg7TnolBbiRk0s4eK2kgID1Qyy37TkQNsS5LnqdLXcGkedlWCcN318Mi%2FpCJW2hkkbHeJwa98lWXJxjPLrQCva5%2Fba5rUCYoWzWxWvU2C76PkG6ZkSYDBwYKOBES38ojw52lZRWqV7WGcoyB6rU8eiwZhUh4aSkB6BZtpQ5Qv7QElVOfCGXpq47LaVk0IYAlcHUpCCSY4UIjKcxkZu5KwHe3BxkwcNrr6wpuDvw5XXPWCVPMq0oaPtdTnivK8DFIwLeqNS2pDEjDenffMJe%2FQ3uCrTtRA5E%2FEoALl8kmNV6jIhUsuwGKB57pKKkzgT7mnvflbN%2Fod6xKY3sCVLcEnkhXER5GljSJjTqSv3F1j1OiBCTFdlm%2FMt%2FdzzlTEHR33EuyxDzeGneBbdRQ%3D%3D\u0026amp;et=video\u0026amp;p=0.268817591\u0026amp;el=start\u003c/Tracking\u003e\u003cTracking event=\"firstQuartile\"\u003ehttps://events-ams.bidder.kayzen.io/event?raw=2ClRJtDf5i%2BIlTp%2FcDfAovGHZZfnkPoWXszuMcMNp6OmxIg4cauPgmoCJLKwvkCEs%2FgGwvNzeDLX6xqjOsp3YkoqpPIzNNsaoFPYjWER7hevG%2BajpmDYbfn7ucPL39xwssIgUodXGI7aPGlHT24VdDL3bBYkaZNhBgsUkK1MN9indqriRbs2VuQGbdqNULCmjbiZsGpjBSshsmTowkgAnvZ73gUXKzFrdFgfsH767g%2F%2Fox7qdjYc6yD8qGsIEqoVgO%2FvxCSg7TnolBbiRk0s4eK2kgID1Qyy37TkQNsS5LnqdLXcGkedlWCcN318Mi%2FpCJW2hkkbHeJwa98lWXJxjPLrQCva5%2Fba5rUCYoWzWxWvU2C76PkG6ZkSYDBwYKOBES38ojw52lZRWqV7WGcoyB6rU8eiwZhUh4aSkB6BZtpQ5Qv7QElVOfCGXpq47LaVk0IYAlcHUpCCSY4UIjKcxkZu5KwHe3BxkwcNrr6wpuDvw5XXPWCVPMq0oaPtdTnivK8DFIwLeqNS2pDEjDenffMJe%2FQ3uCrTtRA5E%2FEoALl8kmNV6jIhUsuwGKB57pKKkzgT7mnvflbN%2Fod6xKY3sCVLcEnkhXER5GljSJjTqSv3F1j1OiBCTFdlm%2FMt%2FdzzlTEHR33EuyxDzeGneBbdRQ%3D%3D\u0026amp;et=video\u0026amp;p=0.268817591\u0026amp;el=firstQuartile\u003c/Tracking\u003e\u003cTracking event=\"midpoint\"\u003ehttps://events-ams.bidder.kayzen.io/event?raw=2ClRJtDf5i%2BIlTp%2FcDfAovGHZZfnkPoWXszuMcMNp6OmxIg4cauPgmoCJLKwvkCEs%2FgGwvNzeDLX6xqjOsp3YkoqpPIzNNsaoFPYjWER7hevG%2BajpmDYbfn7ucPL39xwssIgUodXGI7aPGlHT24VdDL3bBYkaZNhBgsUkK1MN9indqriRbs2VuQGbdqNULCmjbiZsGpjBSshsmTowkgAnvZ73gUXKzFrdFgfsH767g%2F%2Fox7qdjYc6yD8qGsIEqoVgO%2FvxCSg7TnolBbiRk0s4eK2kgID1Qyy37TkQNsS5LnqdLXcGkedlWCcN318Mi%2FpCJW2hkkbHeJwa98lWXJxjPLrQCva5%2Fba5rUCYoWzWxWvU2C76PkG6ZkSYDBwYKOBES38ojw52lZRWqV7WGcoyB6rU8eiwZhUh4aSkB6BZtpQ5Qv7QElVOfCGXpq47LaVk0IYAlcHUpCCSY4UIjKcxkZu5KwHe3BxkwcNrr6wpuDvw5XXPWCVPMq0oaPtdTnivK8DFIwLeqNS2pDEjDenffMJe%2FQ3uCrTtRA5E%2FEoALl8kmNV6jIhUsuwGKB57pKKkzgT7mnvflbN%2Fod6xKY3sCVLcEnkhXER5GljSJjTqSv3F1j1OiBCTFdlm%2FMt%2FdzzlTEHR33EuyxDzeGneBbdRQ%3D%3D\u0026amp;et=video\u0026amp;p=0.268817591\u0026amp;el=midpoint\u003c/Tracking\u003e\u003cTracking event=\"thirdQuartile\"\u003ehttps://events-ams.bidder.kayzen.io/event?raw=2ClRJtDf5i%2BIlTp%2FcDfAovGHZZfnkPoWXszuMcMNp6OmxIg4cauPgmoCJLKwvkCEs%2FgGwvNzeDLX6xqjOsp3YkoqpPIzNNsaoFPYjWER7hevG%2BajpmDYbfn7ucPL39xwssIgUodXGI7aPGlHT24VdDL3bBYkaZNhBgsUkK1MN9indqriRbs2VuQGbdqNULCmjbiZsGpjBSshsmTowkgAnvZ73gUXKzFrdFgfsH767g%2F%2Fox7qdjYc6yD8qGsIEqoVgO%2FvxCSg7TnolBbiRk0s4eK2kgID1Qyy37TkQNsS5LnqdLXcGkedlWCcN318Mi%2FpCJW2hkkbHeJwa98lWXJxjPLrQCva5%2Fba5rUCYoWzWxWvU2C76PkG6ZkSYDBwYKOBES38ojw52lZRWqV7WGcoyB6rU8eiwZhUh4aSkB6BZtpQ5Qv7QElVOfCGXpq47LaVk0IYAlcHUpCCSY4UIjKcxkZu5KwHe3BxkwcNrr6wpuDvw5XXPWCVPMq0oaPtdTnivK8DFIwLeqNS2pDEjDenffMJe%2FQ3uCrTtRA5E%2FEoALl8kmNV6jIhUsuwGKB57pKKkzgT7mnvflbN%2Fod6xKY3sCVLcEnkhXER5GljSJjTqSv3F1j1OiBCTFdlm%2FMt%2FdzzlTEHR33EuyxDzeGneBbdRQ%3D%3D\u0026amp;et=video\u0026amp;p=0.268817591\u0026amp;el=thirdQuartile\u003c/Tracking\u003e\u003cTracking event=\"complete\"\u003ehttps://events-ams.bidder.kayzen.io/event?raw=2ClRJtDf5i%2BIlTp%2FcDfAovGHZZfnkPoWXszuMcMNp6OmxIg4cauPgmoCJLKwvkCEs%2FgGwvNzeDLX6xqjOsp3YkoqpPIzNNsaoFPYjWER7hevG%2BajpmDYbfn7ucPL39xwssIgUodXGI7aPGlHT24VdDL3bBYkaZNhBgsUkK1MN9indqriRbs2VuQGbdqNULCmjbiZsGpjBSshsmTowkgAnvZ73gUXKzFrdFgfsH767g%2F%2Fox7qdjYc6yD8qGsIEqoVgO%2FvxCSg7TnolBbiRk0s4eK2kgID1Qyy37TkQNsS5LnqdLXcGkedlWCcN318Mi%2FpCJW2hkkbHeJwa98lWXJxjPLrQCva5%2Fba5rUCYoWzWxWvU2C76PkG6ZkSYDBwYKOBES38ojw52lZRWqV7WGcoyB6rU8eiwZhUh4aSkB6BZtpQ5Qv7QElVOfCGXpq47LaVk0IYAlcHUpCCSY4UIjKcxkZu5KwHe3BxkwcNrr6wpuDvw5XXPWCVPMq0oaPtdTnivK8DFIwLeqNS2pDEjDenffMJe%2FQ3uCrTtRA5E%2FEoALl8kmNV6jIhUsuwGKB57pKKkzgT7mnvflbN%2Fod6xKY3sCVLcEnkhXER5GljSJjTqSv3F1j1OiBCTFdlm%2FMt%2FdzzlTEHR33EuyxDzeGneBbdRQ%3D%3D\u0026amp;et=video\u0026amp;p=0.268817591\u0026amp;el=complete\u003c/Tracking\u003e\u003cTracking event=\"resume\"\u003ehttps://events-ams.bidder.kayzen.io/event?raw=2ClRJtDf5i%2BIlTp%2FcDfAovGHZZfnkPoWXszuMcMNp6OmxIg4cauPgmoCJLKwvkCEs%2FgGwvNzeDLX6xqjOsp3YkoqpPIzNNsaoFPYjWER7hevG%2BajpmDYbfn7ucPL39xwssIgUodXGI7aPGlHT24VdDL3bBYkaZNhBgsUkK1MN9indqriRbs2VuQGbdqNULCmjbiZsGpjBSshsmTowkgAnvZ73gUXKzFrdFgfsH767g%2F%2Fox7qdjYc6yD8qGsIEqoVgO%2FvxCSg7TnolBbiRk0s4eK2kgID1Qyy37TkQNsS5LnqdLXcGkedlWCcN318Mi%2FpCJW2hkkbHeJwa98lWXJxjPLrQCva5%2Fba5rUCYoWzWxWvU2C76PkG6ZkSYDBwYKOBES38ojw52lZRWqV7WGcoyB6rU8eiwZhUh4aSkB6BZtpQ5Qv7QElVOfCGXpq47LaVk0IYAlcHUpCCSY4UIjKcxkZu5KwHe3BxkwcNrr6wpuDvw5XXPWCVPMq0oaPtdTnivK8DFIwLeqNS2pDEjDenffMJe%2FQ3uCrTtRA5E%2FEoALl8kmNV6jIhUsuwGKB57pKKkzgT7mnvflbN%2Fod6xKY3sCVLcEnkhXER5GljSJjTqSv3F1j1OiBCTFdlm%2FMt%2FdzzlTEHR33EuyxDzeGneBbdRQ%3D%3D\u0026amp;et=video\u0026amp;p=0.268817591\u0026amp;el=resume\u003c/Tracking\u003e\u003cTracking event=\"creativeView\"\u003ehttps://got.pubnative.net/video/event?t=H43aE3B3YUc5u_6QsMtwELGBP6V3j6zYMPplv3vZEzW1lZv-qUgMtPQplSWRyOGCdfhBUcTeUBxH9h3SropguyAaCsX8Lw2Su_Ua8H1R-S_v__466nRaCzOwB-bPTAka9kv8Uer8_wKl3Tw44rocdfuMGOz-DnBh8n30KaIfAxEykNRV384--Mb9j3drs92Xed8umdLjVSGPZc2nC52LiBxkVsTWYRZDZwTcmJIObru_1or4YUGjH6hf76iwIE5LUAQiRC1DggMSvAGyZj4tniN3NF812o7Fm5I3ffyNLd1n1Cr9ucH1AzjqkfPjs-_62W6RI9bY7V0qWpPJHPt0UzZsskgV2TJObokiRuAf0_sfFyxvWico6UuK28p00eHPc-g3FkSmTKlOGEJkEqi49m_9v1RQxrAG4P5ZW0f9nsvdGaEMuQxffeiawy0tW_1BvPMZpb1VZbVSDTjYrcxNJymFgneCwf2UZvuPAOP0GstzeQNYSZau5gj4qrR-mup_mRBjtbe3NOj-Tu35LKiWQb6CVFOpQloLy0dDnp16Tm9E0cAYbgvOPmKRZfTs6rPK7a_S8ryOSxSaZo0U9l1p46oZg6JVSpmC-PGkZXBbxJmmMvyjsCbepNh0zA7KfDuDWWcWhh17P_N9fPPOdroMbycVTfMDxFkAib7hAwaevsnMOPlhWVWTzVOLBH6ZNDJo_4wNI7pzGnTE3R5m0u2yirN8gA2KzicV4onf1bDefhZ-WjWynaEVQ_HkijoEMm4XdJyLoO7_XyqiVo1JVaJwCxUXzLuoN91HBNB7c0aMQaCLCMaHRv2I-ZHJeRGRA-mhIfSNnlfN9Tas_cYyzJwanyobLTdFFaIHbb43IPoOCiBW0TUpI-5u0-O9zkv9C7ZlpbjlSmDN7z3mEmzBwD4iSvdoFIMjzin4ZvfP4mXfa_BWbnFx5TAZQyyBov5MJk1Hh3X12Fb_Ub5oBnwpE_GZQWbbatBulLuo0aBdCxqhiRl-TcuBPs-eIQo8U9s9BIST-35dCN09awfeX0z_J9Nk7R7vlLrxle_XKs_1_ISv2PF0eL7DFKh3z3T4k63iiCJRZse4ngmSPB-Mzg2rlX1iupbfybBbM9oVm9k__KliW2c\u003c/Tracking\u003e\u003cTracking event=\"start\"\u003ehttps://got.pubnative.net/video/event?t=k-n3CcLpUnwXcCtGdoquBrVGvoBi7cs8oKaEdTdx56MZiPrkBOnV-uh0jo_pElXN0eiznUW9QUlMBEy5WoJco0zLNJ9JOMNyeQVxVw6-W8-Bg0ZLM6bnk3btmSW5XCIUlatJJoYECxMGk9GnuqSaqFrGOKuMjg3geGqXAgfFaS4M7OslpCk1KSwhZhncvohMu8zEDjQv9Q5KbNniXPkz-3q7HnPKA6C_Kyd3YKOOXEcoaMOkrOiRB2yng66bfvFuWnumN4pgzEr8P5SCb2NzduYyqPZ97f_eUm7TIDm6kI6ZgQuIDspYuX78zqO7R4KxPcVHWf2TLPZBX76g_dyfe8-xWqYTp0CLPei-pKOI3YODAqbLpcUSoDfB1l6sLjwZd3pM9VXFZ7leML60MsJSJpyaXiQ2pOwHTMnTSWlM_fGdcpsgYQQNN6RoflT4fvNicO8f0l7mtbG_BhkgTn8mR89wWxDAX-8ujw6wR6GSh_4iviwkIKs96UOUFv1j0atEroB8rj8SIory1trRxXTlcEtOF5SJPENjS6YJuSFR6QXFsqoLRIs7JOp8QYIZ6wv4u-VeMqJNCfaXJNHORil02jkAbSuT3pPiVp-fXwneKqgkTZJbZIq50aA5AG_4ZzKcwx9I7Wx59uq7SjCKlJTy-idfiY4tjMOaeugmxc_T9zk2Hb1dEsR0UWtFQ6lnasJo4i5bxVbzKcTYMxkwkaY9SPtiPOoUzjLIdU486Z2O3BBge_5EuaogsTfbRE7bO531p5xM28rebugjqo-acx5cdQsULuqs_8d8eZ1Von_KFosblFpXIBT3ZmQfbWiqoYSS5eQnXxKG7bAQrXZprbntRWFGqUDLSVkGstLFGY4iGw6k2yYOUL4UKwwyzQGUG58EHvKtdxNlpkbK6hYXsl6cgbwFE3hxggXo7glhfdccX3icOJ2tGuy6Nj1tw5JGKcXVEecY-wGGTfNlC4HiZV3gMiZNu7naiEB2NxVko4f_9FV-JnC8HvZC5SXjbY8wy8RZGz73uv7abyJ6uQBKjSJMeoCeXsmSdiBEwuOq46fKlrcVutIbwkh_BZr0ldigAWOMyYk4MDSTg6cmMapTkyttDICaRrdNsyqpkA\u003c/Tracking\u003e\u003cTracking event=\"midpoint\"\u003ehttps://got.pubnative.net/video/event?t=HoUS3JCqwPR21YjqUABmihkvzlhMMWSza-Q2MD5MRzoJmT5QnVo41DqQMImoUcuTozS9yux2WNDkCjEYjBZ_wMXI8SGaDq2ARD8ZksXIeqze4OixASoZaKNyvYmyI04LMniVJqQffv0amUrUuhKkVrXzFEppLQSIP514HNDSxeuT60MB53UIhwWRtEzyTlprfd79QeFBYuEdadSK43zhcK_1vm8EHabDWnFmOcwr1xDS5wZrfWIs-IX9fprrmKHtzuBuqoY3v3pqkXr06YrAOOprPG-tX8_O8DdiXZ0pNzbuUcDVVffMDZidMldVrh0vS-P_L8iQ9-CkKHBaXZTOGJhUKGvTq9wVzQQZK0UKLA3RwOrsT5OPOr5swbugPOvmqUVFsOm06PJIkLXhpby1DUavu83QUlGaRoLpErGwXRWCqcDk7vlFqkA2MnLpPQ0oQHzCpvlnC9Y1daB-_h1vrwFRb3NpnXE7O0LQD_y84zjPdJ02j4_XEzKWvfhLKj4lKPm0uHlcRCBovzsKbTSDLFxKARLdtL3-SXYUjKkt3i6nIUdh2mOaiz6hY3_Z0SWcyP2Y_ENYgp73sksm9qla6eH-I4S6YTH5byeRFGlfw3fW4Iisjz4Yd1qOuwO2HNit_dJtNxjP23hF8eaHEVWQL6o1sEzcZ48miNKdsOTG7vLwG4urbLMCz2s0S3Topm2ZKa8oIu-ui2UrPkPYgTdp3gg9Fko_MPl-DyXUaLBA5Lxp_wZCFUTy9vjNxVpNbYf_BhIv2NgId_ptS2G-KsN3dldYMnFEA4t6XbY-ekVzRzB-IYXGeBC_QPCKaFiHJ3DB4wOiMRDKeVl35ueJYYT0pgObw1YHQFVPJoEDqyvoQ0nqCvVlkbq8CBydQL7ZqF6WUzH8uBOvjPI6xrwlSkSSGswsyne53WnjWOdO46EthCp_P_WwEVa7Yg7NUQ0Va2FPVQKA_hQfmfATB77D_9exJii_fwHTi-zaUwupEfymelyWokJeAzRZiXQEVVCAp9vf1vjjpzl1JfMRfCBBXvtgpBxqmiTE0gWNb1_F5a01lffMXt93o0zFa3BVjGN088KBIP__niMv6io2rSH7nMUaTnQq4gZgvAQ-9Cfy-w\u003c/Tracking\u003e\u003cTracking event=\"firstQuartile\"\u003ehttps://got.pubnative.net/video/event?t=KfiWR9I25GYjSuMH2fzxGdIhBqT6Mos0ALhfK-YSeVlZdAJugRROYjWsmVBXca4K-oo7i00LwDy-h0qOPOgSirc77ZnniIAlxhu0B5t6Sf3RfEb4KfG2rvlmBu5_ddgnOLccqofHRexHp8BqJHl6APZTigKZzOOhELLaZZgP9qcg-5tcB59rDhrqDZoBSKiXw_4KTXwL156TpsrDnpZfi9zYEIyQ2d_1jdUNwSneW_fUo_3EXXQSuXFW_CGWV1A-IAyBaWwcm_NEB8KpExGZBNZgPnQ-v4BYUjJNSVhQiEeZSYGRUtl5nVqhDu44EetVKckX6iq-uy5aHrBw72J1OaVgXyxAbayoafkOi9V5ihdk92VQc5p07PrAhUOLJpNXXmgyoThKMSLWrOhhimbQXsz-IdijWVLZyBo-bPI-nHnVPvGo2RHoa6VoQEZ_TqhfIDnDq5c1LW0CG3MMNArII-EZi-4Ri2Ex95TrWzDQ1-n-aFZlPtamgzE464gFUQrRJKJVL2SW6kmguyGd8P2iOO4R83H1EvPYxsSdsIka-voKYL6YTlDv3_xj8q9vd7n3Q65j9uJ6WNm2aERtJjvzFffYpGhzZoDapQJAonQ31g_1ZoirsCtL4kcOos6bhu4ZMMj9BCVnWMFxesJqH0zp9goEf2kQH3gOq1G5GFypy9mxIW6PRUi6IWGjJOFNvdIofl5NR13DSeijorzZ9GrJiDRvTkcqdcw542u7e87rcYhlOUU664N0qxOVwh6_8sjT7jamWtt3j3hjVuAUANE-UBcegIOZbP7W2gFp3AQNxj8RFkqjuxHXhDIDSZ6-iYSVAyOe0BGvDVJv0CUsVKyTggvnitM9OCuVkpi7c6nG-0hpD9Gb_Q7YQ1m7GPxZ1auIEfTkRx8oIuCfLG5JRqepKu5Zjsekj_JWtwVhuE-8O6vx3aV5mujlxZjVoK4b0ROo5P1ohnFeTiEniVpt2UFCmOCzXMjvcdsjGNyIbi28bY_BBTxefD02xehwzw4yCJAgLjtKDTuH2LkcjYbZolcuNRWZadXxt0huyJXKi4QJa6rbx9KGKApAQf4poxb7NO7MmDXkNQMC4a0md2l6HpgONdqXknSfp07eyKc6dAkh1pwo\u003c/Tracking\u003e\u003cTracking event=\"thirdQuartile\"\u003ehttps://got.pubnative.net/video/event?t=J9446_42sH7WZxhgzxctZpgG75DxSNV9vvIDO6SjbxUg3MYHMCoTdhdfiY3BYQR4gqd7jRj4dkFGzR_KUhA3o6Aewc1k9BO9n3NGmeLBIzeC3nkj9o_nXVlrBDrf0Rjoh4KyrFj7TA8O7xBEyvqDke_WQo2RI11tHPuVfNzgyMPIR-2MeOSglhPXLCGSeYdECC0SNu10zS_hnH-K7p-R04l7ApnPsbOqgGUsnRdv8tYy9tXtztJiNA9-uqiEBdaPGEoMRWN3etS3Kljeo6_D9EK0J1dJiPLL3gTWppFvuZa3AHZ-QdSKE7BVSHUpZRTYOw8T1zo6zzShVhQfOyjQEQhcofCVfj-scCApG8oNYPA4VDMKzBGHp-hW_H8Nx-cbY7rpn96lLuvmCMuvx4g7NSgWn1QkEqJil7QmzTXYFVp4Vyg060GgFOipqb-RCbunMKk1S4TjodOgglZk7zNj2P9e97Y2BAjGO_u2d9o-P06Sv-SCxZTHMbtLIr_EvlfSY2sM-6dRuLCHt4zy-PY2ZRquPYL0fptLXB1I8BSEZCYxj-Xr2UVG9fes4QkXm4NihWUneujdoDBWlnLNQzz3ZF55Yvor3hjg6eOU8V9Ym_GBCCBmRTMaR-exxI8Jll5WnTXHLZObclkuMJFG11Q-2zSphTctZvGFjdo7fzcim5l-21UgUEuI7mcvT_PO6CTpr5aEW0dkihbVG5nOpsB1D_m5qdGyRNzL94UsDfVIHm5JK27fv-qnkGow124ogI6pR6HQ_hAfIi1neQWQNWUHxu80J_WIYjHRTEiX4o0KRO9kbHzRe8P2W7nH8DU4_GoMUJoo8wFTPDYfOMaXLFhhQx3xB6Yop1CTivN-F8MyJEv82vlp05_oit3kul41ePLwUeye8tHaWnEsG43iuXc97iPTZra-5vWa-6hUld0RkxBHEEFp3cBB_V-dRNDMUA9uve0X4Q49KTqlxNZ81KfrbSxlHvyVQvuOBCVLD9ogaY0BT3xwsvht4iJMOyCeepYE82DOkMOPoJGCKMYBe2I0PdSIZN1zuH8AjZnpXsVg7y67jfasbsC1MKOHkqeEPJKvoaWJ16kKUKmfVUj8hnY8mUTcRlE1ANqiu-oqgGSjyDbR\u003c/Tracking\u003e\u003cTracking event=\"complete\"\u003ehttps://got.pubnative.net/video/event?t=aU4Y6vIBR2cfb2xqWtYls-fqjHk8RZYCfv5gzk5WsMtU7KQgBsclEeOFBjExhPkyRGl7FZ41EZHr8KzZgzB4GydKyZTk9Q6lbIxwhG2v0TUbCrUJCgw0E7fw9EfJtCrzoJlCBTocHOFTksilzlhZQ32otmFI4b4Pt1ep-bmWc0Se6qY_pRLRZjh7JKVNOML6z9S0JnbyibMHgrclz4vjFydJ6IuHcAtSPxb4FWOmVkeGoejbWV62q9jv-6WGLr7ImX9p6z78TPtG1R_QNGb_pH_ZrMc3tCA8UG1qqpAGE6gzSfCo4_jILUYMQp0-eGon_sVehChlCto19mPMXflB4IBeO3-HEa7haCkDLpM-o6eNEeoqMBJ7uvL6St6UU_Slwh33_FArjktpFP2ktRWqjL-yFAC00tGl6yEcmlUityK1YAYYueYfewpyfJ2tXWT4bGAEbE8LYHUV5_Vle1_BrGHI-lKMdzWHGTctfe1Jc8fgQqPgcf-byBwKD8jINE5daFc1-ebV2zAdhY3-GY-ibQdPR8NrQpp6ROvBLmT4xYiBLztdnyBsn1r_dzt5UzbU_FnWdNrHlh4_BG1yJm54fuhhDkRw3oSfX39AFla2iMf8GBvcVaNSOlVdb8ukH3ZVs4wc3CDOXZ-a2iPSbMibEygE_cvR_6gQzL_PZFhDXMPR7HD5GNmfw250MgdWUHKzit6nRE1Wq2fj9rIOTIDBiUri9fsmM8fPiX1jU8Bbg11fQa6cYq3uCRsFgG9IVQlUQAJ9UNkf_t2P0u8T46uVq97B5wY_AWNBYZB_gCkzUdcSiqGgHppJMGaryQSWt_vCrV4atgiGAPRDAgE7fbIdRkvojYZvGJPlCcYfl7TkKQZxBP1cX4DFxGWuuGZCEyQZyT9QlKLce7IZU4-szdbGfWHxhkq1uPTaXUy68Fh09whuFoajrWF43EokU9YkH7KWVuVI0suWjybevYX3AZDCpMSMtqCM4t6jyt4DmWaq4fXICLFx7tJSLAUwC9RL9MVcebV0HMvpWjghEWvWxzpR87AzEht2z3PrMcYAc1USpO0xw2RPfO2qQ1h19VbgFOsIBpafJXgtXE6T9MP-cI3ng4-e-B6EzVP6PdiDoQ\u003c/Tracking\u003e\u003cTracking event=\"mute\"\u003ehttps://got.pubnative.net/video/event?t=iFdUVU8uVB2Rs3wIFQdeSSQn0eo3xeehrKCkZJVzzacdq5RLYwlGBlQLYhgovNGG30fn0LlLJPE8DMgHcJNoreeGbD86yaGaeFJDfsJtKKWuUTBTXmrHmPJfB_sJm-tKAmaul5i9x3yFK4Dx3a8Lp8Vw67zovRO-BHAlv7M1CXbEfqLwFnup2IygCny07SAIi6p-Zq0OwMzz62FUlUUfTVgjMxLYsRsEJoVme_gqa9y7K__1e4vIL0OOnhjUCJvkjUOYprbO50oEyd-RARaVmdv2-Us5f_tweI78zZMoqVYLB8W5dqEOfTew5uFBIV9j02C9GcoPPGmQybNikal01Y1t0BC8rzHygIZkxYeVZYgi-8PoUs2KXaAU-l8tk5MB93LxZouoRQnJKL7Ov1vm5GgiRG8gDyhsJmRskZRCnlMWYYCd9QbuEmXtPGffnyuO6fem965nYDi2syNZ3wqalbXxvHdYa3MD-y1W_4_PKwP2HI9WGmp82pn4H0wCgP7mukVerTpU7Z3dSRqbDAHRD1czDOSZdDiyFsh8smGECtQgi1IiEXt3hZqli6gvFpY3YiYxnKsm9HJpT5qiLZJVzj5DNaMYLdV2gmzydws12w9qfwY_vjJou0GfPvH-2FDPp_2zoQEpEQffm1iYwgs_Cvdg52ZaHF3R2s8GZMUx2kNjwaZHYnnwdJRb-GmQSz7JjwJCCUsyvQ8EAqqrbXUvnhWq8idGjPWvxUljVvevxqJ-vCDTbsDvunQuTR5Zb_C_vqqCGkBaJ9j5LcgoD4iODKknfdt84Se3SdYYafnmLI-eupfEqR8YyCpP4smTyPKHfVVZN3rdnRiqEj6M66z1h11PK5vjFHoNrssfhGPzycLkoysIvCnmAMjtUG_LNU6gbsiy-tN0wVcAbph8nFDI5VsGUxuHLzquecAdFJSEdVD-xyr6X_RhOlFmbaJrbcBYh98_pD9x6zaMnCDGEHPCrpiPFm50H27wenL2A9x_PiAqwwUXFzSbbSyT1dZ8uQTkjc1B-Mmq5t4n-YAFvuR-HlxnXL6PxCLU3KPZAqckyWRwtT_r7jgV9_IlvvOpTTGB5yfInUjww7j2f6-vXl_1Tl4DNySmAEVB\u003c/Tracking\u003e\u003cTracking event=\"unmute\"\u003ehttps://got.pubnative.net/video/event?t=j705iFXBvKEyRoVXrmLvwDb0emV2MwEgf-_3QL5IMrYvmFZObdCAZs20pFwshP6Itl21pgfYudRUfOxXjrb8pJ9G4Cnop8o-3-0ssBonpi0QAWJpZ0zJoHcs55lytFQsegpfjmi1Tg1awXulastrGz0HwwWssJXTJvUDDBDHsXyQVQkldks8d87UZDXu3PwTlROTzxLbNkhDG0uKx2msu84ZArTVn3TQgJ72h8MMJUvM6Cv6F00GQw2zwy6aYsKUZSmUkZyY3bfr_rukdRUjqgD9A2kPZiusA8Xahjt7iaPYX5mn-9oO3YiZTKuH3E0xXJoa8t4exgumQB2QoMPIi5CNPhCHGgp3CmFw_cVFW4M6cEyysTqdggA7FzzGjO3P8fKWgTwLRGBRNNWYqE--gTKYg66vFyJKWDyYd506LtNo6zBxACocEAc72ovRNbSI_XfiJggiYllsqtERDREnx-AdsPEiUkHSPPsisr1fSas7JenvisfOyKor26qUuI-S2rmakryIaeMpJGC-zWUB9t1s3Gf87nD2a0L6B3d91m1OBZnqlKD2zXzFB1mEwh0JrxIxfTWVkUoNBnGrEu6OXMK1w62AXkCun-56rpG3dko5PSyv4gVOHyLyyNtqipO-w3J1jNPmEZ7IjeQ6QMKfM-ZeOa4HygdoMLyI1h6jvULJ_s-P8dOSx_UQcb_qCGnDnOrfSCD-g2FbQ4WNvToQ9MrPTpbkSoPjiHcalBD9O5m_KJ-dF39p2jOQXMdKP8ioTfbSez1vppVaWJII3tNYIqFuZ3GZzIPdnKxRdIxcRT-62HQt-YhrvHxTHx68F4y56DG3NaZZ7bd4U4B9NQf5RgZ2gDiO-yOAgllh-gYbFI9BjGy7xveXjeM9ttkpEmzvjk0rR4T5iVTpiIlrn9fUuwtb5AYOfYsXY_SOWg70nW1_tb2HHmqnVghMo8WPDGlAsxXnI3pI5jiofv-re62JPA0cCEYOwOv1qMi2dSohKWnG66iI5hOyArhUeXoyOI7WeUDNzIMn8gfuT75MtUlDF3zOwlrWGAJpwuz-G1veIfgaQ7EdUqe_ipEqE4pWzMPwEnjqvHe3yZGVLIZ-mKGcoLMLqgQN1jPYbUQ\u003c/Tracking\u003e\u003cTracking event=\"pause\"\u003ehttps://got.pubnative.net/video/event?t=lX4pcSS5ESKblJmHZF_BgdQ84FlTymgsQ-uDvjR4vx9PUtURxRmrEAP2_mk-Qbii9a0Ax6W3jQcRqjbGur2FtJE9Df7-FmSIrtVy-SKkMf0F39io2qJmqIsiWMnh7m78yJmvBpj9DYOXvZToQWPKJvTLheNHtsJArRsaApl8Q18jAQ4HgtJPU46w7mTgd5lzboF7VWvRO5BEtrJjAWCSRMs6smtXz7mQ25D5VXIPrRD21kbbU2SJmB8aguyGkrIxDaODllg545A6GhdCgdsoDhBNFKm1zLIZUIJlyTheM3gdMJV60IESqSP0FXSd7HGJMsQfEMjtYnrcO74efFj0Sx82zC1Bodu1vyhJInVcvrpX6gG59TjkQI37BTeI5U2JDsp06JadYbotljN-A5RCDF-gfBC5WPK5u8jg3fqFJr89CR86gIfQjDEAV7Nsor5-hsTtuDiLo5SzF1x3t2l6Gx-u6pXG6LtkvH86n3OCcQzvBX2Ifda06gOuhKXEYOuaszQYQIpi1wBbztYBNd7CBCWlpSowOFHj3S8EogRmfMdKYF_KQDhB3E5qquFzOX36RWKYm9sq6FhyAMS7eQ8k_QsH0TEJ8QKRW0jAs6mHTXmLNfcqjdDOHpZIphhssrcecbWeS0O3SKLIxI-PxHwrYO91cYZjwWgM_zxpDtJv4VoDFhQFmPfBR08Ha0O0Zt6AtOONNBtplQMT4IIdCP1lgOx1m94v6EMoaA5_L6bAGoS3_RLbMOiXO15ai5Tz3uuWCmIdUZ4eMlMjz4ol6cf4NIRxZxqjkzyiieJ9HZlccMxqSgIltIiVK3lWLhacVKlZ6-l2MiJR3wp25TNwGVv6LFJ4n1Qpa-T81wNegj92v0mzuxzH9FZF8Q7x28rOUUtub5wVcECQSIomLzxHLKP6Fg42xJa0ZY0IXUgWtfkSPowXo4LTSotnAXMimu1HF_pcOE6zxqTTJpvboajBybUNrdja0aajDBnyAYK3QCoUhCYQigQjnq3r4t8YzckQNR2_ZAosEZM8WSDwZDeCA5Gc2Nul5vklrEWVHXnYU-YHJmRM3FXO2-Vnn0iaSdrGOfpL-8EbhXG6gbzZiPElfswa5D-fBohxooEjOg\u003c/Tracking\u003e\u003cTracking event=\"rewind\"\u003ehttps://got.pubnative.net/video/event?t=eUd9ahXM6684EiK0gA_Rac9PgjZLhfppoQ1-Ugz4gQuUZJFdiDt-uhrUaggRPeHKc8ofWr3M6M9k8CXfQEyq3BYg8IOf1iX6aZnv5p3ysG8cR7u08P9j666PE7o15uH4wVrhdbgEmDk_ihhAL1Lm0HcEOHufr6VjK2wNHjuEwc86H7XcnjnraSA84Y9fF_SFbpyJ5ABDaKm8hMCFFQxc01A0cWdksK8UZx156-ONWwK22zZ5D4NW-XCZo9zuutHAP_bkp7tgASRZ3e4eEhFMntSoUl-2p1Fg9WnRS6I1eaFLH3Nl9sJctEQHkGO7SQ69Zx7YieBNywIirBTFmw3mjowyf3JfLVKnfudW1qT1kcl1d8eWALk7iLKXJ0j2BSEHP12oV63yWkOQ-giz0y_G9sxqDjy5puIuBTJO4Xl6wWl-6hWZ3of7CbSTYQgTXO1A8lDapMj8X5Wwe7t1WUU003y9lXDEESJeK1inZVicEZF1U5BuIw34U5k4RcXr2mkBacaexEnJuTzQp-XWR9XejoIuPYk9pnR0sDp5blEQA1-KNxLZjFbECCG1FtXJPnoE37vt2Pb6yzbrIXpmfoxdDpS6JFSKgdFrIJVJxj4xk6Wsjjn76wij_5Z3T3W22_j93YJVRay44AhoOvaa6PFx3SdjFrtpuKf_Ve_uH8_o9S1UKmAilT2mwA-ECrOEdY4pbnc06m44yADbWCTRF3-dlAkT6NktbCbGCx2_QUxKQ3Ctlj3ua8T_WJWIZlUDUm-vqB-mbLBrmJHNkUq3e-3r1yGKzvwe8mno_pHVlHK5b7_glzkYunuUAWRtyGYFLlP5_r45BNAHjXlYwhenLBT7dtbsJ7mkpQmwsIWNmclS8z96ui26zFswfQ2xdBCOKhdYg-hplt6N6OCH_k_x51MiVw4HQXp6ySVjl73G_v-hTNeS6pzK8pgNWHYp_zImrPtvXpp0k8tvp1rlhItWEu_mvSVHe8d9N64q2bl_yY8keoUREVtYxy85QZYtRkZDtPGpuQhlzmblRumMLV5nIykdyrPQlh3xzh9KHaZqhLpDe_2_okj41UCcA5mo8x3D9R39HBmu5SANxgf2xj5GHmUXYzkqct8Ba--z4_c\u003c/Tracking\u003e\u003cTracking event=\"resume\"\u003ehttps://got.pubnative.net/video/event?t=SA5qhuXrsSI2Cga6ZkCqJz4X5mNc9MAncLUVr9s9ilIHkvKmDh21yIfVkxgCf2IyZ6_oOI0qGsloqtZOoaPczqSxHeMPmnfOyXBc7i_SlpH9vGr24jiteSwSGlE6FhkTwAw8ZHAlBrDYVatE2qZYX8Qqmc_H4Kp5PtmRnYG5VBmUx1zA5UDhDkZ771DdVnK0fVqwAO7EE8N9HWy2_uuLKxwMHgCiuegt28DE9qAJU3_vVnr6Y8eOpmD6xLeIixZ4i7nkHStCWg4HXbQr3o7t92kLXASMJMBmxBNUNHx97rQjJi9RItx5fTm_SuNzD8fHq4yhUPu4rDgNfNTh92aaRtzC0aUSIE_S1B4PgXn80FhgmE1ESOL-QFvWPnhCJPkDdh3t0p8OFh2TAKRXQfI_tyzltwslflILoOlTQPLMarKg-KQbpoYRjHaSbOl545vXf1GQoqq3xyWSTeQqaiGgz5V81szVRoadjMOn3qiNfHrVm4u1VqMJcgOIRlzshqS-YHlHYNWRSC-52Y6XBTOuSnfqBTjD4UgTM48R1cRfyHvhtT5RDGQ2hVfuVkzI1EVZs-4Luflx9HQMBpVHieJptzXFRSpez029tkqfM-suLZc2_d__Wjp3t6jVbZNXlM79e_tytuAM7NGHVka4riILgb61FuW05wkROf4SDJuKeTq25j71lWcMs6V1fGZOFbyfgBKYaSI6ml2ek1q8wGCSIm6hq23vL1z4J_QT_4hxU9Xow-wcuuBJh-kN_WOkudLT9GE55PfsY39KnOt59t2Vjxk_d0tcuaYeOB6Wu0LH0RG0ORIrersuG2yLUmtb4Hfhufu55ffRYSBW8nC6r4Ll_PR5nyjVD8db9aUAL8lr6o-J5CNrFzbZO8W5mQg6y340De0lw6EgO6RlCdFXnHbCMsU3AEtoM4m_6yWtCgGFosrNeA0vN2Ngk3OkFQZZ_TpuwPc172kf1xoA3E7Tb8xH2eLx4OPis2rRK5LWgr_xRBczeLTOyC-fSXieSsslufuxipJ8YTz5pWCxNkaPRmHMBOxwYFIWSwQNozDkqs88JPjhZhQCjvZQyN0Bd7dlPSeZbwjvO_HHUkMeAYeSngGpX5JvHwjhrf1GQzk\u003c/Tracking\u003e\u003cTracking event=\"fullscreen\"\u003ehttps://got.pubnative.net/video/event?t=9vNWu_RKs_HCy5lksEFMCDmz-AVq0MWJisvfex0bid08Kl1S6Tl2hR53qEjyBkibBgw-BlXKxsRJvY9rJWG-hvHEaEP_krpTuIqJ-mg2Dil68mfFm07P-fA1xDnwhCznzRDFA_OMaoaMqLwGHhpUP5lDJc9t5PggQscVUg0kvCahY2if-MrWsP_kk-MeoRpwbdOM1AuAkM9MJ2uyB53XhU0GBlHcYazmk4FMTSa0iIiLi12fzs6bQQ2X1G_htNjQ7EeB-KLcWvo9DHbVmQlnAYX1F-iS3naRmouvBNHrq_yWNy4_KDXlK6y4ubQdFAKYj6nQ6ipTSvfvQsInL0-fE615t23n8jpyGRqN3tFCzjnBieYOmwHW8alat5O1lmy1J-gdKnatEiORwSa5aMnmzWupmAwy6jZxqB5OxyDh2B3G-YNwqDMlq3IOgWxoVzrDNNiLCqrdfcZCq3nJOy38yR5PX42eEq8YAjEAM9DPltbs9n_ajHP4DKfthW1KSIAJ-sRExnAsXZ7tWqIiu1qI7f37FuPu1CXKiFHixIBf37adcgQ07NRmOH7943yVxznkZmFBRMoKaOAUziWETxGYwzy1mNQOkxdok9WIFox-9RuXevPOv8r8feRqk_wblggwTkr7QjeziP9HC5oidzMD7Ru09ro98qu8HCLZ326uze0oJLa7bLzXydfhH5yxl6D-rU-Zf6Gmn35LveKTA1ZZyg_EQ4Zp4LlF712LYyFK4WJuXyOM1_vbRTJqhxOSaEGaUBFp127Ad1UBLslPrCDYz-5ZPRweNw8uqCg6lvJygXhYdgsNSKjLc-yulsOMtJpY7-Vq0y6SAcKdXbBWHkuHQDNoeDlBe-Hc5c3G0SHRO4m7Vxw-n9y_gyDvUfqrzbiPsnxzsreSPfvNsboOWB3xb_nwpd0AKtttpiO-AVxrdxDcM5ACiewalat7TPtd38iGMUNkJTKMPwtXu3ST72b6XzMxYI_OalvKBAu5jbv3qErvHlilrNhRmz0e0Btd-a9ZajfdIaXt5KyWCvcaVmXrS1OTaae4rB5LKo2TvDdURUX3fSjF7aNVcWgzxthaNSmchYbaFrPweHs5Fv9Ny6GhfXVDyC6SJH1sF060-joy\u003c/Tracking\u003e\u003cTracking event=\"expand\"\u003ehttps://got.pubnative.net/video/event?t=ChpHIBishfW7CaqM12jkTXExkradHGUHf-c9xuFpnxj-Rkp3s8VR98np672JjQQUG0zBLMVoax_zAteyFo_BDxUJpdee8VojU3iBZa-EwwHBxgErm-QCbw8toiTnkuz1a8JsuWFnbbq0b3PlzOeYpzgvvb5PRIE92BZVmFZwY2a580yt-EUI2raprS62yoXX_jF3g7j7E7eWKSlzhQWX1R8Cg350XBbrS9i68Gnq8BG4Yqa3fKMx4y5KsKHcFKe4PKtCvFETj23X3HZuXbTUhxphAK8BtnfmR2WEaoQUA_ngBgJGC0SXV2-kzoesY3HTsyOae3HkHOl0sRk3CBXPNqC6SBWzUPjGU8MoiYgnP3zNOHo0hypZpwfVBP60iGYOc2IEPDdH5M1isBj0Amphl0uFMrAiqvuBgcwQPqvFm4YF2mtyFq935bBD7aydDjIE-v-EECPlCcXChcrGnXyqjGQShFPlKfTcoih8nNEpB8U3uoD4rt62kf-QzlkeAhMmQLq4yVefXJsfsG-XA8jUtBshf21XDTvA73gMn7FXdghlrTJiDHIDK9qOeC1J3jxaMMWlGYR55rcUNziMlYsfpk5ZrpBmDBPistm3HuuUtSvrxHXJyipgBCqCh1Lil311LbgpSe3bVja5zX0mr8_b6b894fQV-xu6bF_yLb7XCN3qyGt12fdpUNHM4aC3pFxo7W-A0G1TanJP5AwbJmpztBtvmwl9EQfH153Rs9iB_5Ksa6fqebvN6bIuqy4_HGUTRZTgPat1C1ZRcUn3-NDM2dMMzONwRpkfonCx1NKUU_PS_OLaGZx2jvUwwkmUh-TyEQW65QcqxwCnJJcQ9aNKwLrWmpWIJRzRIJJ_PwgBAJ1Y3iDfJ2Scn5f10FuMP3-Gk0X7zWw7oeqPCKWMpHYF2gUC4BrWGSnisIaymShc5vpXHe_t7c3fzmJgAjnYgpmyf77jOUUi5mRzHpqo7hf6eq0elgZNPWFOcj3azHUbReFMUh82U4kf9y0X7yMmSa3dXOAfOcgBJzVtMnOhNfLrt2Cq483Ag3Zqmf7Ku0pkVIwNt1OPWn9_lq8vDz_0yjzUFRUVchiDQPsVlRuOnSXa9gUhod_dYCv6kpE\u003c/Tracking\u003e\u003cTracking event=\"collapse\"\u003ehttps://got.pubnative.net/video/event?t=LCv2Gv0bGFGU7JJzDaZZuvPKth95TCJSiArqqMI_v_6d-ThmKB8i69JlxjvLwW4IS2L8BxBfkRUUrr4cCH7x6oPAje0Eq0ByfHrKaL_ssRaJNVFPzHAh6rY6t_npG0YrzMEVBSHpSaQKfYeFjBb0iaAindcqclO-j6Ejm-B2rTaa6vHUejJyfPZpKTmAxBo69UKH9hWBH2o-CndmN1Q5xIJr3SChcY0aGsxjIU-hB52--9W6pOPvUCCwMHW13E1pxPW5_z91LnhB99M5yqOIJz8U-Ab_5SjTl6p6KrZWYizKgRHN5F7Y4RST7cRe0aSw7UlHBxYcm8u2T8eku4913HSk1Yua_2JLiucDCL5LlBDVwvQn-mbUrXu-typE7FxPMmLULC0W6JHnaDase4mIU4ZyVqFuvtjPrNTRpuE7kzn8SZjF8Rzqe87XiI5x0NT08DlaoRUJUGW1qtn2CKbsF85frpZagq75j6KnTyKdiLKWVoPE8OavVgLPamTkXlc0-9-lMiubh6Al38T0jDsrMmXE2w-r9QblRGPp-VseJF1x5rBaewlSwV3DiEOtVdJ3ZkPKeVkDW004BpHAm3JEmcSGiZ3--ICp584ncLJPmGYat2J_nAVZs8GWOf8HvT1u7NqFxQrrIUhVudqC0YYeE-72hBHvVannMMgIX5w6KMmE1DgZILXi8k-7m592h7hgZvpcudwVCQg1naDjP4Mue4-t5rgRZpowQ9PXRdv6g2MVx2ebCsDe-qMJ34mXBn6YvT2GiM_yM7WgqpOEGK-jR-efO9DDD2PZ0ok9jyX_QWpRtknF0EwxRTHspIIR7QBVybfHyzpJ5ZDOv5AEl8J-ltWG77HdlfmHkOMe2nId6tNyJDbKp086VTNSVO87oVteu0k4muK267srvhLjgvwzKVMQsLg7zPaV0mQulltwdoL0BSDS9dTVwe8mqSiO0JDUur_EKk3Wm7SyLmRzEJjY8PtVCxmZCnXPvToEM3Ft1cOP8nm42k1KNMRe1R0dXmKUe1eCBGfLlV6eWvpTJ7ehXETn4eOvvRPDtdohkfZpoeLSbzlLw6pPq0wtxj7OpNtgtBbdTD04W3-5E7FFreYoEYMdaW3T5uIjR-6rNg\u003c/Tracking\u003e\u003cTracking event=\"acceptInvitation\"\u003ehttps://got.pubnative.net/video/event?t=HpYVdzWiuwilKA26kC79HJIkTmwzPvLf5g0KRw_BOQn8Cs5sNWz7bSKrVsusJnG16ARhAZIM3wP9xS6dXnYS5mhCLJ6t-SGiFyS7P5e3sgcAmO70938o0Y2bAzO4Jdf70qzX0mBpqJ9AIGrFSwa5YLS_Gkq0o-5f0QKxomm1TNXKJ5B0T0BZmwvhaPJNmqNjJGPqLuNBMqTkzESPG85XnaZ_Do2GzhQ0qqWVjiVBOJA3WDcT8fZAQWjJyLSxhZZBkKjpAKST-e92R0_psyZFk4r48EAST6xV9FCHCT-UongV-9hRVqMnfWR5WXOav086Gq8F4SqFz0CC5Tu3LKi-oKY4kjBZKNgY4EQ7N2DFE3rPZ6x3I6nmbaUNl46IFM8kiLN1yZWGWJ_CXzzgDER6Tn2C2lc2Hpas4DvYnlqv8H-x1BaJW82mOwPygz14yfNDz8Ulj2RClWmXxHqZuX9oZJdy8pCynhNRhDB8_WoOVHtpPDpr7wg_YlCKN1mICQ0Ophvwj3E0elmLVDiaNlS-AzV09Y_OAEGu7yEhMviEmd6rIN5mI0nrHmbE3FA047il8VTmeWC1NFTIG-aigRqgT2TIbCaJ5nXEKBiFrqpIJkwshDiwBaS6Tiu2toVr4qwXNuvemsVAFz-bqM-TpghBj94TAY4LgCCPefIkzABkO3ihpepvFXA1a8Wi21iZ42tk6K5cDXQEBPl3qF3oltOo8uUBGTVNNx-D1V3BwDHOCTPu8pnDk_bXpSFR3ceVdLpmMANJvTgqFYfA8kEgSO6p0lY0fpKL-jEpWV_-022o3JVz-7xiZolH5me0bdzrdTHV8F1jpVIz3qzFrtNul2QV3xqc1Cb8rMDcD_5153BwzUuBKDY1-_mC_Ez85QBOgBg5ETjVOG8O-BUFlttV7h82iBu5Bckuzi2ho4yTPsrvP7nNv7fi6WvmrMmaS-0gvef9hdD3_oFiX12FiuVLREMjO8DznPvj-Wc1Q3PwX5q2MPoOiDDrLVrfn8z_rp3Lf2rl1UdGCY0FE-64IMI80i7cwJVL7zTw6xKgfLn1-GUn3t1G6xmW-F877as0Olx_MRfr2AICFXyFyrvdd6bEvJnAh6ZWhCkBOKFESOzZ6I6eHKWLm_BX\u003c/Tracking\u003e\u003cTracking event=\"close\"\u003ehttps://got.pubnative.net/video/event?t=lf5u2n4ffMamZuMnLd_yV6L6WCIC5VGpqttG9gsgkZ17ttG_nPfjkqnDerogUXSlH9hKu4coK4Ou9rbll8eApwh9NgG60psO_2TjBt6X69ZiAnPduBxZTn3xr6yDZMkmAe2LC5Y5bK-liYBRKCXwNFmltdhPro5Zi4FNdRZ2t0EmNkVOcuuxn0yrKOFGF5Rk0Z5z1pg86AWb7EfRpAy1rq7JuJLjkVfVOaYMfcfjEqzH0wjHSYPnqmr5gGN6o60_Ly5fuhaI3UIpgm0-r4V492uVPa-6qcpkVavtBvrGA8TJ8YvPzMKMVkvB5O_byFivaAeqNHyT8yNX8169lcKFv2L1mv_lfTIHab9gV6yyvso7U7BQGsGIpkEa5i6kEk1mZcgNvOcwE2ZNcThtoobVcFfgOMbAnHiaC7G2kHr8LV8YNNVQxgL0xx9gyJlHaMXCtHKUfigQn6RyfTVtVFBrNBCXm1V1q01jmL76OoflPA5EC2gEWgwG1V-S3UDOKuIxAXFAOt7n3Y6-MabqLOf0QNX4gV41gY2oeeo_VxjbAH7VCoiYoQXIYujbrJpehDfIEkoD1CqyS-q5OogZz3HAviry7-xqlZEPqKpKUGJfRSczJAbjEJkI3q6dgPph_NypSzp7_uZGjpFmbfmISgUH8mzUlvZtgSlGExT8Akw7ztRnNefIJjvQtOjyMfYePxVyPlRr8iizN6Fuv_nmC63WJbL46nP2-dLIc9Nm1XdHNj4sFfoK2yJXxrjCcEs0sBI_I4BVoQW3keRRi8Zio0iSt7wfxzmFNW5LOxdUKU5air383E_WXcB8Ma3hQWniQxqnn6X6Zcmry6oRQGsMt6PdwcbJWIKltxaXXuZNUXZRcfyYxJnmzVw-sO75vZXDM0oCES1-mZ3A5fnYJ_mAr6yy94SDy3fWHNO7HpaLn_n2TlONge6JLaNDTWa9f2mVVaxP2Zt0250NhgMAWe8Ej4efagd-9wRR4EB0XKUb_WeGZf7ARp7VF9zbf3Zu9zssnNPWUaqQvnAHVM0qXfwhBe8AgfRPGFxnaVuYPYoKolTCAupzs-F5fWVOCYf2N6qt2TRrqsHkyIGJ72OF0xmHIjgh6HhLW2U396gbgg\u003c/Tracking\u003e\u003c/TrackingEvents\u003e\u003cVideoClicks\u003e\u003cClickTracking\u003ehttps://events-ams.bidder.kayzen.io/click?raw=2ClRJtDf5i%2BIlTp%2FcDfAovGHZZfnkPoWXszuMcMNp6OmxIg4cauPgmoCJLKwvkCEs%2FgGwvNzeDLX6xqjOsp3YkoqpPIzNNsaoFPYjWER7hevG%2BajpmDYbfn7ucPL39xwssIgUodXGI7aPGlHT24VdDL3bBYkaZNhBgsUkK1MN9indqriRbs2VuQGbdqNULCmjbiZsGpjBSshsmTowkgAnvZ73gUXKzFrdFgfsH767g%2F%2Fox7qdjYc6yD8qGsIEqoVgO%2FvxCSg7TnolBbiRk0s4eK2kgID1Qyy37TkQNsS5LnqdLXcGkedlWCcN318Mi%2FpCJW2hkkbHeJwa98lWXJxjPLrQCva5%2Fba5rUCYoWzWxWvU2C76PkG6ZkSYDBwYKOBES38ojw52lZRWqV7WGcoyB6rU8eiwZhUh4aSkB6BZtpQ5Qv7QElVOfCGXpq47LaVk0IYAlcHUpCCSY4UIjKcxkZu5KwHe3BxkwcNrr6wpuDvw5XXPWCVPMq0oaPtdTnivK8DFIwLeqNS2pDEjDenffMJe%2FQ3uCrTtRA5E%2FEoALl8kmNV6jIhUsuwGKB57pKKkzgT7mnvflbN%2Fod6xKY3sCVLcEnkhXER5GljSJjTqSv3F1j1OiBCTFdlm%2FMt%2FdzzlTEHR33EuyxDzeGneBbdRQ%3D%3D\u003c/ClickTracking\u003e\u003cClickTracking\u003ehttps://got.pubnative.net/click/rtb?aid=1507451\u0026amp;t=kfvjSUX_6CV5nG35741KHq7Iw6iQ3KHIy7VXxHuHpmxXZwMxwTklidaUY3vvffx7Mw4JEH5MmzyBrYMQjftN9daFFoA8_ft0Eq_QKWNxOxmS2y99O_nVpYLcyxxEQ7Vu0diZzVv_lwCtTaunes_SK1ocNc1x0LGIQZNE3w0LJDn3qJju1mM2v2SlxKCY2gxGyt995JnowgVC8KmvYiwfXD5lCvf35H9BK7YVhJwQaohxWzFiWh3ZVpNvaEZYwL-8dQNnI-i9VofVKGsUWVBIZ0N7GaaXqu8W6dNDc0mcUABe-gcqkYPigg8j0hLdu_ZIJBsd4RRzaGo7HAPOqh7LIhkLM5YGRWqtcgQrGuMJak6qFHnaXPzBn6bqakvD3Gig8WCGY9PmtzRpZiBYRoNF_tM7cBmeGzlcoIfx8k7AjVWyrmoZ05z1yXNw9NWDhfWjstpBQ_hcGfZkNBybcOfGQCHmL7K-dG3h9CKmVsHuvI7Mf_keRuD_NWHoV722lMYdW5HRHko4LATwDzJo77aTg4uOozztvRUFmUMCZI7E7a-U6rkjEmmBn6aiWiNIKuMKAxEsP0h--dmkMf5lbDJkT3ocXUkVZgxh1bwHwvj3Err6GvXoqGH2oYv8Bioe6oaVIIS2WFcBegC8CSAXPhqooX5LKlq62HTxXDlFdXS1qRFn4n2i74KVSZk4-DKWOwrO-yDCmgMxTL8y6Q76J4eQm_gYoUeKHkzJulSUQAoFNUYUpqxSG-QtrQFu5M6mrNGUU5QZ0TUr3AABjJkJQfNNbBalz1nj0VeLLfOHcstyEIm-JA5X2kocRC65qIUgqIwpa2syoP_MO7eeUzEyB-Sv82Av1OKIehQlolATevSTYsQ8UHoJ4HzucHwq0fmt4e-uf7wPzG6Ryo_bpg\u003c/ClickTracking\u003e\u003c/VideoClicks\u003e\u003c/Linear\u003e\u003c/Creative\u003e\u003c/Creatives\u003e\u003c/Wrapper\u003e\u003c/Ad\u003e\u003c/VAST\u003e";
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


}
