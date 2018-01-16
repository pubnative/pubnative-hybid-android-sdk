package net.pubnative.tarantula.sdk.api;

import android.support.annotation.NonNull;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.models.AdRequest;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.AdResponse;

import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class ApiClient {
    public interface AdRequestListener {
        void onSuccess(Ad ad);
        void onFailure(Throwable throwable);
    }

    public interface TrackUrlListener {
        void onSuccess();
        void onFailure(Throwable throwable);
    }

    @NonNull
    private final ApiService mApiService;

    public ApiClient(@NonNull List<Interceptor> applicationInterceptors, @NonNull List<Interceptor> networkInterceptors) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        for (Interceptor applicationInterceptor : applicationInterceptors) {
            builder.addInterceptor(applicationInterceptor);
        }
        for (Interceptor networkInterceptor : networkInterceptors) {
            builder.addNetworkInterceptor(networkInterceptor);
        }

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + Tarantula.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .build();
        mApiService = retrofit.create(ApiService.class);
    }

    public void getAd(@NonNull final AdRequest adRequest, final AdRequestListener listener) {
        Tarantula.getSessionDepthManager().incrementSessionDepth();
        Call<AdResponse> call = mApiService.getAd(adRequest.apptoken, adRequest.os, adRequest.osver,
                adRequest.devicemodel, adRequest.dnt, adRequest.al, adRequest.mf, adRequest.zoneid,
                adRequest.testMode, adRequest.locale, adRequest.latitude,
                adRequest.longitude, adRequest.gender, adRequest.age, adRequest.bundleid,
                adRequest.keywords, adRequest.coppa, adRequest.gid, adRequest.gidmd5,
                adRequest.gidsha1);
        call.enqueue(new Callback<AdResponse>() {
            @Override
            public void onResponse(Call<AdResponse> call, Response<AdResponse> response) {
                if (listener != null) {
                    if (response != null
                            && response.body() != null
                            && response.body().ads != null
                            && !response.body().ads.isEmpty()) {
                        listener.onSuccess(response.body().ads.get(0));
                    } else {
                        listener.onFailure(new Throwable("Tarantula - No fill"));
                    }
                }
            }

            @Override
            public void onFailure(Call<AdResponse> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(t);
                }
            }
        });
    }

    public void trackUrl(@NonNull String url, final TrackUrlListener listener) {
        Call<Void> call = mApiService.trackUrl(url);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure(t);
                }
            }
        });
    }
}
