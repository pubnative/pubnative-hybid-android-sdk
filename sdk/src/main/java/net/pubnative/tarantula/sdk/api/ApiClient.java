package net.pubnative.tarantula.sdk.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.models.ErrorRequest;
import net.pubnative.tarantula.sdk.models.ErrorRequestFactory;
import net.pubnative.tarantula.sdk.models.AdRequest;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.AdResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class ApiClient {
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(builder.build())
                .build();
        mApiService = retrofit.create(ApiService.class);
    }

    public Observable<Ad> getAd(@NonNull final AdRequest adRequest) {
        Tarantula.getSessionDepthManager().incrementSessionDepth();
        return mApiService.getAd(adRequest.apptoken, adRequest.os, adRequest.osver,
                adRequest.devicemodel, adRequest.dnt, adRequest.al, adRequest.mf, adRequest.zoneid,
                adRequest.testMode, adRequest.locale, adRequest.latitude,
                adRequest.longitude, adRequest.gender, adRequest.age, adRequest.bundleid,
                adRequest.keywords, adRequest.coppa, adRequest.gid, adRequest.gidmd5,
                adRequest.gidsha1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new ExponentialBackoff(Jitter.DEFAULT, 2, 30, TimeUnit.SECONDS, 100))
                .map(new Function<Response<AdResponse>, Ad>() {
                    @Nullable
                    @Override
                    public Ad apply(Response<AdResponse> response) throws Exception {
                        final AdResponse adResponse = response.body();
                        if (adResponse == null || adResponse.ads == null || adResponse.ads.isEmpty()) {
                            return null;
                        }

                        return adResponse.ads.get(0);
                    }
                });
    }

    public void trackUrl(@NonNull String url) {
        mApiService.trackUrl(url)
                .subscribeOn(Schedulers.io())
                .retryWhen(new ExponentialBackoff(Jitter.DEFAULT, 2, 30, TimeUnit.SECONDS, 100))
                .subscribe();
    }

    public void trackError(@NonNull String message) {
        new ErrorRequestFactory()
                .createErrorRequest(message)
                .subscribe(new Consumer<ErrorRequest>() {
                    @Override
                    public void accept(ErrorRequest errorRequest) throws Exception {
                        mApiService.trackError(errorRequest)
                                .subscribeOn(Schedulers.io())
                                .retryWhen(new ExponentialBackoff(Jitter.DEFAULT, 2, 30, TimeUnit.SECONDS, 100))
                                .subscribe();
                    }
                });
    }
}
