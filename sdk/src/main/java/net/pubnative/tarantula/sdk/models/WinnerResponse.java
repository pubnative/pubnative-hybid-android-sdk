package net.pubnative.tarantula.sdk.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class WinnerResponse {
    @SerializedName("creative_type")
    @Expose
    @NonNull public String creativeType;
}
