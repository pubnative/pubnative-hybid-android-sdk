package net.pubnative.tarantula.sdk.api;

import android.support.annotation.NonNull;

import net.pubnative.tarantula.sdk.models.AdRequest;
import net.pubnative.tarantula.sdk.models.AdResponse;
import net.pubnative.tarantula.sdk.models.ErrorRequest;
import net.pubnative.tarantula.sdk.models.api.PNAPIV3ResponseModel;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public interface ApiService {

    @POST("native")
    Observable<Response<PNAPIV3ResponseModel>> getAd(
            @Query("apptoken") String apptoken,
            @Query("os") String os,
            @Query("osver") String osver,
            @Query("devicemodel") String devicemodel,
            @Query("dnt") String dnt,
            @Query("al") String al,
            @Query("mf") String mf,
            @Query("zoneid") String zoneid,
            @Query("uid") String uid,
            @Query("adcount") String adcount,
            @Query("locale") String locale,
            @Query("lat") String latitude,
            @Query("long") String longitude,
            @Query("gender") String gender,
            @Query("age") String age,
            @Query("bundleid") String bundleid,
            @Query("keywords") String keywords,
            @Query("coppa") String coppa,
            @Query("gid") String gid,
            @Query("gidmd5") String gidmd5,
            @Query("gidsha1") String gidsha1
    );

    @GET
    Observable<Response<Void>> trackUrl(@Url String url);

    @POST("/events/client-error")
    Observable<Response<Void>> trackError(@Body ErrorRequest errorRequest);
}
