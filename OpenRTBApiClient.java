package net.pubnative.lite.sdk.api;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdResponse;
import net.pubnative.lite.sdk.models.ortb.request.Macros;
import net.pubnative.lite.sdk.models.ortb.request.OpenRTBAdRequest;
import net.pubnative.lite.sdk.models.ortb.response.Bid;
import net.pubnative.lite.sdk.models.ortb.response.OpenRTBResponse;
import net.pubnative.lite.sdk.models.ortb.response.SeatBid;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.source.pnapi.BuildConfig;
import net.pubnative.lite.sdk.utils.AdRequestRegistry;
import net.pubnative.lite.sdk.utils.OpenRTBApiUrlComposer;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class OpenRTBApiClient implements ApiClient {

    private final Context mContext;
    private String mApiUrl = BuildConfig.BASE_RTB_URL;
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

    public OpenRTBApiClient(Context context) {
        this.mContext = context;
    }

    @Override
    public void getAd(AdRequest request, String userAgent, AdRequestListener listener) {

        if (request instanceof OpenRTBAdRequest) {
            final OpenRTBAdRequest adRequest = (OpenRTBAdRequest) request;
            final String url = getAdRequestURL(adRequest);
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
                                registerAdRequest(url, response, initTime);
                                processStream(response, adRequest, listener);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                registerAdRequest(url, error.getMessage(), initTime);

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

    private void registerAdRequest(String url, String response, long initTime) {
        AdRequestRegistry.getInstance().setLastAdRequest(url, response, System.currentTimeMillis() - initTime);
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
    public void processStream(String result, AdRequest request, AdRequestListener listener) {

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
                if (seatBid.getBids() != null && !seatBid.getBids().isEmpty()) {
                    buildAd(apiResponseModel, (OpenRTBAdRequest) request, seatBid.getBids().get(0), listener);
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

    private void buildAd(OpenRTBResponse apiResponseModel, OpenRTBAdRequest request, Bid bid, AdRequestListener listener){

        Ad.AdType adType = Ad.AdType.HTML;
        if(request != null && request.getImp() != null && !request.getImp().isEmpty() && request.getImp().get(0).getVideo() != null){
            adType = Ad.AdType.VIDEO;
        }

        final Ad ad = new Ad(12, bid.getAdMarkup(), adType);

        //ad.setZoneId(request.zoneId);

        String winUrl = replaceMacros(bid.getNoticeUrl(), request, apiResponseModel, bid);

        if (!TextUtils.isEmpty(bid.getAdMarkup())) {
            String markup = replaceMacros(bid.getAdMarkup(), request, apiResponseModel, bid);
            //ad.setD(markup);
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

        /*if (!TextUtils.isEmpty(bid.getId())) {
            ad.setAuctionId(bid.getId());
        }

        if (!TextUtils.isEmpty(bid.getImpressionid())) {
            ad.setImpressionId(bid.getImpressionid());
        }

        if (!TextUtils.isEmpty(bid.getNoticeUrl())) {
            String winUrl = replaceMacros(bid.getNoticeUrl(), bidRequest, bidResponse, bid);
            ad.setWinUrl(winUrl);
        }

        if (!TextUtils.isEmpty(bid.getIurl())) {
            ad.setCampaignImageUrl(bid.getIurl());
        }

        if (!TextUtils.isEmpty(bid.getCampaignId())) {
            ad.setCampaingId(bid.getCampaignId());
        }

        if (!TextUtils.isEmpty(bid.getCreativeId())) {
            ad.setCreativeId(bid.getCreativeId());
        }

        if (!TextUtils.isEmpty(bid.getAdId())) {
            ad.setAdId(bid.getAdId());
        }

        if (bid.getWidth() > 0 && bid.getHeight() > 0) {
            ad.setWidth(bid.getWidth());
            ad.setHeight(bid.getHeight());
        }

        ad.setPrice(bid.getPrice());

        if (!TextUtils.isEmpty(bid.getAdMarkup())) {
            String markup = replaceMacros(bid.getAdMarkup(), bidRequest, bidResponse, bid);
            ad.setCreativeHtml(markup);
            listener.onSuccess(ad);

            if (!TextUtils.isEmpty(ad.getWinUrl())) {
                // Notify the auction win
                PNHttpClient.makeRequest(mContext, ad.getWinUrl(), null, null, new PNHttpClient.Listener() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onFailure(Throwable error) {

                    }
                });
            }
        } else {
            if (!TextUtils.isEmpty(ad.getWinUrl())) {
                PNHttpClient.makeRequest(mContext, ad.getWinUrl(), null, null, new PNHttpClient.Listener() {
                    @Override
                    public void onSuccess(String response) {
                        if (listener != null) {
                            if (!TextUtils.isEmpty(response)) {
                                String creative = replaceMacros(response, bidRequest, bidResponse, bid);
                                ad.setCreativeHtml(creative);
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
        }*/
    }

    private String replaceMacros(String text, OpenRTBAdRequest bidRequest, OpenRTBResponse response, Bid bid) {
        String replaced = text.replace(Macros.AUCTION_PRICE, String.valueOf(bid.getPrice()));

        if (!TextUtils.isEmpty(bidRequest.getId())) {
            replaced = replaced.replace(Macros.AUCTION_ID, bidRequest.getId());
        }

        if (bidRequest.getImp() != null
                && !bidRequest.getImp().isEmpty()
                && !TextUtils.isEmpty(bidRequest.getImp().get(0).getId())) {
            replaced = replaced.replace(Macros.AUCTION_IMP_ID, bidRequest.getImp().get(0).getId());
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
