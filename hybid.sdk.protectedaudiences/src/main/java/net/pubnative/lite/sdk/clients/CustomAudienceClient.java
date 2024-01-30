// The MIT License (MIT)
//
// Copyright (c) 2023 PubNative GmbH
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
package net.pubnative.lite.sdk.clients;

import android.adservices.common.AdTechIdentifier;
import android.adservices.customaudience.CustomAudience;
import android.adservices.customaudience.CustomAudienceManager;
import android.adservices.customaudience.JoinCustomAudienceRequest;
import android.adservices.customaudience.LeaveCustomAudienceRequest;
import android.content.Context;
import android.os.OutcomeReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.concurrent.futures.CallbackToFutureAdapter;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Objects;
import java.util.concurrent.Executor;

@RequiresApi(api = 34)
public class CustomAudienceClient {
    private static final String TAG = CustomAudienceClient.class.getSimpleName();
    private final CustomAudienceManager mCustomAudienceManager;
    private final Executor mExecutor;

    private CustomAudienceClient(@NonNull Context context, @NonNull Executor executor) {
        mExecutor = executor;
        mCustomAudienceManager = context.getSystemService(CustomAudienceManager.class);
    }

    @NonNull
    public ListenableFuture<Void> joinCustomAudience(CustomAudience customAudience) {
        return CallbackToFutureAdapter.getFuture(
                completer -> {
                    JoinCustomAudienceRequest request =
                            new JoinCustomAudienceRequest.Builder()
                                    .setCustomAudience(customAudience)
                                    .build();
                    mCustomAudienceManager.joinCustomAudience(
                            request,
                            mExecutor,
                            new OutcomeReceiver<>() {
                                @Override
                                public void onResult(Object ignoredResult) {
                                    completer.set(null);
                                }

                                @Override
                                public void onError(Exception error) {
                                    completer.setException(error);
                                }
                            });
                    // This value is used only for debug purposes: it will be used in toString()
                    // of returned future or error cases.
                    return "joinCustomAudience";
                });
    }

    @NonNull
    public ListenableFuture<Void> leaveCustomAudience(
            @NonNull String owner, @NonNull AdTechIdentifier buyer, @NonNull String name) {
        return CallbackToFutureAdapter.getFuture(
                completer -> {
                    LeaveCustomAudienceRequest request =
                            new LeaveCustomAudienceRequest.Builder()
                                    .setBuyer(buyer)
                                    .setName(name)
                                    .build();
                    mCustomAudienceManager.leaveCustomAudience(
                            request,
                            mExecutor,
                            new OutcomeReceiver<>() {
                                @Override
                                public void onResult(Object ignoredResult) {
                                    completer.set(null);
                                }

                                @Override
                                public void onError(Exception error) {
                                    completer.setException(error);
                                }
                            });
                    // This value is used only for debug purposes: it will be used in toString()
                    // of returned future or error cases.
                    return "leaveCustomAudience";
                });
    }

    public boolean isApiAvailable() {
        return mCustomAudienceManager != null;
    }

    public static final class Builder {
        private Context mContext;
        private Executor mExecutor;

        public Builder() {
        }

        @NonNull
        public Builder setContext(@NonNull Context context) {
            Objects.requireNonNull(context);
            mContext = context;
            return this;
        }

        @NonNull
        public Builder setExecutor(@NonNull Executor executor) {
            Objects.requireNonNull(executor);
            mExecutor = executor;
            return this;
        }

        @NonNull
        public CustomAudienceClient build() {
            Objects.requireNonNull(mContext);
            Objects.requireNonNull(mExecutor);

            return new CustomAudienceClient(mContext, mExecutor);
        }
    }
}
