package net.pubnative.tarantula.sdk.api;

import net.pubnative.tarantula.sdk.models.AdResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public interface ApiService {

    @POST("native")
    Call<AdResponse> getAd(
            @Query("apptoken") String apptoken,
            @Query("os") String os,
            @Query("osver") String osver,
            @Query("devicemodel") String devicemodel,
            @Query("dnt") String dnt,
            @Query("al") String al,
            @Query("mf") String mf,
            @Query("zoneid") String zoneid,
            @Query("test") String test,
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
    Call<Void> trackUrl(@Url String url);
}
