// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.api;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.core.BuildConfig;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdResponse;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.request.Imp;
import net.pubnative.lite.sdk.models.request.Macros;
import net.pubnative.lite.sdk.models.request.OpenRTBAdRequest;
import net.pubnative.lite.sdk.models.response.Bid;
import net.pubnative.lite.sdk.models.response.OpenRTBResponse;
import net.pubnative.lite.sdk.models.response.SeatBid;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.AdRequestRegistry;
import net.pubnative.lite.sdk.utils.MarkupUtils;
import net.pubnative.lite.sdk.utils.OpenRTBApiUrlComposer;
import net.pubnative.lite.sdk.utils.OpenRTBAssetsGroup;
import net.pubnative.lite.sdk.utils.SignalDataProcessor;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class OpenRTBApiClient implements ApiClient {

    private final Context mContext;
    private String mApiUrl = BuildConfig.BASE_RTB_URL;
    private String mCustomUrl;
    private JSONObject mPlacementParams;

    @Override
    public String getApiUrl() {
        return mApiUrl;
    }

    @Override
    public void setApiUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            mApiUrl = url;
        }
    }

    @Override
    public void setCustomUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            mCustomUrl = url;
        }
    }

    public OpenRTBApiClient(Context context) {
        this.mContext = context;
    }


    @Override
    public void getAd(AdRequest request, String userAgent, AdRequestListener listener) {

        if (request instanceof OpenRTBAdRequest) {
            final OpenRTBAdRequest adRequest = (OpenRTBAdRequest) request;

            final String url;
            if (!TextUtils.isEmpty(mCustomUrl)) {
                url = mCustomUrl;
            } else {
                url = getAdRequestURL(adRequest);
            }
            if (url == null) {
                if (listener != null) {
                    listener.onFailure(new Exception("PNApiClient - Error: invalid request URL"));
                }
            } else {
                try {
                    String postBody = adRequest.toJson().toString();

                    if (!TextUtils.isEmpty(postBody)) {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("x-openrtb-version", "2.3");
                        headers.put("Content-Type", "application/json");
                        headers.put("Accept-Charset", "utf-8");

                        final long initTime = System.currentTimeMillis();

                        PNHttpClient.makeRequest(mContext, url, null, postBody, new PNHttpClient.Listener() {

                            @Override
                            public void onSuccess(String response, Map<String, List<String>> headers) {
                                registerAdRequest(url, response, postBody, initTime);
                                processStream(response, adRequest, null, null, listener);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                registerAdRequest(url, error.getMessage(), postBody, initTime);

                                if (listener != null) {
                                    listener.onFailure(error);
                                }
                            }

                            @Override
                            public void onFinally(String requestUrl, int responseCode) {
                                PNHttpClient.Listener.super.onFinally(requestUrl, responseCode);
                            }
                        });
                    } else {
                        if (listener != null) {
                            listener.onFailure(new Exception("Invalid post body for OpenRTB request"));
                        }
                    }
                } catch (Exception exception) {
                    if (listener != null) {
                        listener.onFailure(new Exception("Error processing OpenRTB ad request"));
                    }
                }
            }
        } else {
            if (listener != null) {
                listener.onFailure(new Exception("Invalid ad request. Make sure you have initialized HyBid SDK properly."));
            }
        }
    }

    private String getAdRequestURL(OpenRTBAdRequest adRequest) {
        return OpenRTBApiUrlComposer.buildUrl(mApiUrl, adRequest);
    }

    private void registerAdRequest(String url, String response, String postBody, long initTime) {
        AdRequestRegistry.getInstance().setLastAdRequest(url, response, postBody, System.currentTimeMillis() - initTime);
    }

    @Override
    public void getAd(String url, String userAgent, AdRequestListener listener) {

    }

    @Override
    public void trackUrl(String url, String userAgent, String trackTypeName, TrackUrlListener listener) {

    }

    @Override
    public void trackJS(String js, TrackJSListener listener) {

    }

    @Override
    public void processStream(AdResponse apiResponseModel, Exception parseException, AdRequestListener listener) {

    }

    @Override
    public void processStream(String result, AdRequest request, Integer width, Integer height, AdRequestListener listener) {

        OpenRTBResponse apiResponseModel = null;
        Exception parseException = null;
        try {
            apiResponseModel = new OpenRTBResponse(new JSONObject(result));
        } catch (Exception exception) {
            parseException = exception;
        } catch (Error error) {
            parseException = new Exception("Response cannot be parsed", error);
        }
        if (parseException != null) {
            listener.onFailure(parseException);
        } else if (apiResponseModel == null) {
            listener.onFailure(new Exception("OpenRTBApiClient - Parse error"));
        } else {
            if (apiResponseModel.getSeatBids() != null && !apiResponseModel.getSeatBids().isEmpty()) {
                SeatBid seatBid = apiResponseModel.getSeatBids().get(0);
                if (seatBid.getBids() != null
                        && !seatBid.getBids().isEmpty()
                        && seatBid.getBids().get(0) != null) {
                    buildAd(apiResponseModel, request, seatBid.getBids().get(0), width, height, listener);
                } else {
                    listener.onFailure(new Exception("HyBid - No fill"));
                }
            } else {
                listener.onFailure(new Exception("HyBid - No fill"));
            }
        }
    }

    @Override
    public void processStream(String result, AdRequestListener listener) {

    }

    private void buildAd(OpenRTBResponse apiResponseModel, AdRequest request, Bid bid, Integer width, Integer height, AdRequestListener listener) {
        OpenRTBAdRequest adRequest = null;
        boolean isSignalDataProcessing = false;

        if (request != null) {
            adRequest = (OpenRTBAdRequest) request;
        }

        Ad.AdType adType = null;
        Imp imp = null;
        if (adRequest != null && adRequest.getImp() != null && !adRequest.getImp().isEmpty()) {
            Iterator<Imp> iterator = adRequest.getImp().iterator();
            boolean typeDefined = false;
            while (iterator.hasNext() && !typeDefined) {
                imp = iterator.next();
                if (!TextUtils.isEmpty(imp.getId())
                        && !TextUtils.isEmpty(bid.getImpressionid())
                        && imp.getId().equals(bid.getImpressionid())) {
                    if (imp.getBanner() != null) {
                        adType = Ad.AdType.HTML;
                        typeDefined = true;
                    } else if (imp.getVideo() != null) {
                        adType = Ad.AdType.VIDEO;
                        typeDefined = true;
                    }
                }
            }
        } else {
            if (!TextUtils.isEmpty(bid.getAdMarkup())) {
                if (MarkupUtils.isVastXml(bid.getAdMarkup())) {
                    adType = Ad.AdType.VIDEO;
                } else {
                    adType = Ad.AdType.HTML;
                }
            } else {
                if (bid.getExt() != null && !TextUtils.isEmpty(bid.getExt().getSignaldata())) {
                    isSignalDataProcessing = true;
                    SignalDataProcessor signalDataProcessor = new SignalDataProcessor();
                    signalDataProcessor.processSignalData(bid.getExt().getSignaldata(), new SignalDataProcessor.Listener() {
                        @Override
                        public void onProcessed(Ad ad) {
                            listener.onSuccess(ad);
                        }

                        @Override
                        public void onError(Throwable error) {
                            listener.onFailure(new Exception("no ads found"));
                        }
                    });
                }
            }
        }

        if (isSignalDataProcessing)
            return;

        if (adType == null) {
            listener.onFailure(new HyBidError(HyBidErrorCode.NO_FILL));
            return;
        }

        boolean isInterstitial = false;
        if (request != null) {
            isInterstitial = request.isInterstitial;
        } else if (width != null && height != null) {
            isInterstitial = width == 320 && height == 480;
        }
        Integer assetGroup = OpenRTBAssetsGroup.get(imp, width, height, adType, isInterstitial);

        // to check asset group id
        if (assetGroup == null) {
            listener.onFailure(new HyBidError(HyBidErrorCode.INVALID_ASSET));
            return;
        }

        Ad ad = new Ad(assetGroup, bid.getAdMarkup(), adType);

        ad.setZoneId(request != null ? request.zoneId : "100");

        String winUrl = replaceMacros(bid.getNoticeUrl(), request, apiResponseModel, bid);

        if (!TextUtils.isEmpty(bid.getAdMarkup())) {
            listener.onSuccess(ad);

            if (!TextUtils.isEmpty(winUrl)) {
                // Notify the auction win
                PNHttpClient.makeRequest(mContext, winUrl, null, null, new PNHttpClient.Listener() {

                    @Override
                    public void onSuccess(String response, Map<String, List<String>> headers) {

                    }

                    @Override
                    public void onFailure(Throwable error) {

                    }
                });
            }
        } else {
            if (!TextUtils.isEmpty(winUrl)) {
                PNHttpClient.makeRequest(mContext, winUrl, null, null, new PNHttpClient.Listener() {
                    @Override
                    public void onSuccess(String response, Map<String, List<String>> headers) {
                        if (listener != null) {
                            if (!TextUtils.isEmpty(response)) {
                                String creative = replaceMacros(response, request, apiResponseModel, bid);
                                ad.getAssetHtml(creative);
                                listener.onSuccess(ad);
                            } else {
                                listener.onFailure(new Exception("No creative was returned on the bid"));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        if (listener != null) {
                            listener.onFailure(new Exception("No creative was returned on the bid"));
                        }
                    }
                });
            } else {
                listener.onFailure(new Exception("No creative was returned on the bid"));
            }
        }
    }

    private String replaceMacros(String text, AdRequest bidRequest, OpenRTBResponse response, Bid bid) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }

        String replaced = text.replace(Macros.AUCTION_PRICE, String.valueOf(bid.getPrice()));

        if (bidRequest != null) {
            OpenRTBAdRequest adRequest = (OpenRTBAdRequest) bidRequest;

            if (!TextUtils.isEmpty(adRequest.getId())) {
                replaced = replaced.replace(Macros.AUCTION_ID, adRequest.getId());
            }

            if (adRequest.getImp() != null
                    && !adRequest.getImp().isEmpty()
                    && !TextUtils.isEmpty(adRequest.getImp().get(0).getId())) {
                replaced = replaced.replace(Macros.AUCTION_IMP_ID, adRequest.getImp().get(0).getId());
            }
        }

        if (response.getSeatBids() != null
                && !response.getSeatBids().isEmpty()
                && !TextUtils.isEmpty(response.getSeatBids().get(0).getSeat())) {
            replaced = replaced.replace(Macros.AUCTION_SEAT_ID, response.getSeatBids().get(0).getSeat());
        }

        if (!TextUtils.isEmpty(bid.getAdId())) {
            replaced = replaced.replace(Macros.AUCTION_AD_ID, bid.getAdId());
        }

        return replaced;
    }

    @Override
    public JSONObject getPlacementParams() {
        return null;
    }

    public Context getContext() {
        return mContext;
    }
}
