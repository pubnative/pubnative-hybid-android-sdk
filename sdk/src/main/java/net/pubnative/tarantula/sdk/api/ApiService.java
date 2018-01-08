package net.pubnative.tarantula.sdk.api;

import android.support.annotation.NonNull;

import net.pubnative.tarantula.sdk.models.AdRequest;
import net.pubnative.tarantula.sdk.models.AdResponse;
import net.pubnative.tarantula.sdk.models.ErrorRequest;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public interface ApiService {
    @NonNull String AD_REQUEST_PATH = "/ads/req/";

    @POST(AD_REQUEST_PATH + "{adUnitId}")
    Observable<Response<AdResponse>> getAd(@Path("adUnitId") String adUnitId, @Body AdRequest adRequest);

    @GET
    Observable<Response<Void>> trackUrl(@Url String url);

    @POST("/events/client-error")
    Observable<Response<Void>> trackError(@Body ErrorRequest errorRequest);
}
