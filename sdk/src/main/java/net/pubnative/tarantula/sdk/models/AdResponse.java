package net.pubnative.tarantula.sdk.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdResponse {
    @SerializedName("creative")
    @Expose
    @Nullable public String creative;

    @SerializedName("prebid_keywords")
    @Expose
    @NonNull public String prebidKeywords;

    @SerializedName("refresh")
    @Expose
    @NonNull public Integer refresh;

    @SerializedName("impression_urls")
    @Expose
    @Nullable public List<String> impressionUrls;

    @SerializedName("click_urls")
    @Expose
    @Nullable public List<String> clickUrls;

    @SerializedName("selected_urls")
    @Expose
    @Nullable public List<String> selectedUrls;

    @SerializedName("error_urls")
    @Expose
    @Nullable public List<String> errorUrls;

    @SerializedName("winner")
    @Expose
    @Nullable public WinnerResponse winnerResponse;

}
