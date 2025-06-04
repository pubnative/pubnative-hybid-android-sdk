// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.clients;

import android.adservices.adselection.AdSelectionConfig;
import android.adservices.adselection.AdSelectionManager;
import android.adservices.adselection.AdSelectionOutcome;
import android.adservices.adselection.ReportImpressionRequest;
import android.content.Context;
import android.os.OutcomeReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.concurrent.futures.CallbackToFutureAdapter;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Objects;
import java.util.concurrent.Executor;

@RequiresApi(api = 34)
public class AdSelectionClient {
    private static final String TAG = AdSelectionClient.class.getSimpleName();
    private final AdSelectionManager mAdSelectionManager;
    private final Executor mExecutor;

    private AdSelectionClient(@NonNull Context context, @NonNull Executor executor) {
        mExecutor = executor;
        mAdSelectionManager = context.getSystemService(AdSelectionManager.class);
    }

    @NonNull
    public ListenableFuture<AdSelectionOutcome> selectAds(
            @NonNull AdSelectionConfig adSelectionConfig) {
        return CallbackToFutureAdapter.getFuture(
                completer -> {
                    mAdSelectionManager.selectAds(
                            adSelectionConfig,
                            mExecutor,
                            new OutcomeReceiver<>() {

                                @Override
                                public void onResult(@NonNull AdSelectionOutcome result) {
                                    completer.set(
                                            new AdSelectionOutcome.Builder()
                                                    .setAdSelectionId(result.getAdSelectionId())
                                                    .setRenderUri(result.getRenderUri())
                                                    .build());
                                }

                                @Override
                                public void onError(@NonNull Exception error) {
                                    completer.setException(error);
                                }
                            });
                    return "Ad Selection";
                });
    }

    @NonNull
    public ListenableFuture<Void> reportImpression(
            @NonNull ReportImpressionRequest input) {
        return CallbackToFutureAdapter.getFuture(
                completer -> {
                    mAdSelectionManager.reportImpression(
                            input,
                            mExecutor,
                            new OutcomeReceiver<>() {
                                @Override
                                public void onResult(@NonNull Object ignoredResult) {
                                    completer.set(null);
                                }

                                @Override
                                public void onError(@NonNull Exception error) {
                                    completer.setException(error);
                                }
                            });
                    return "reportImpression";
                });
    }

    public boolean isApiAvailable() {
        return mAdSelectionManager != null;
    }

    public static final class Builder {
        private Context mContext;
        private Executor mExecutor;

        public Builder() {
        }

        @NonNull
        public AdSelectionClient.Builder setContext(@NonNull Context context) {
            Objects.requireNonNull(context);

            mContext = context;
            return this;
        }

        @NonNull
        public AdSelectionClient.Builder setExecutor(@NonNull Executor executor) {
            Objects.requireNonNull(executor);

            mExecutor = executor;
            return this;
        }

        @NonNull
        public AdSelectionClient build() {
            Objects.requireNonNull(mContext);
            Objects.requireNonNull(mExecutor);

            return new AdSelectionClient(mContext, mExecutor);
        }
    }
}
