package net.pubnative.lite.sdk.models

enum class AdFormat(private val adFormatValue: String) {
    NATIVE("native"),
    BANNER("banner"),
    FULL_SCREEN("fullscreen"),
    REWARDED("rewarded");

    override fun toString(): String {
        return adFormatValue
    }
}
