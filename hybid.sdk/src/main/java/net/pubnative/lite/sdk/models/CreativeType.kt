package net.pubnative.lite.sdk.models

enum class CreativeType(private val creativeTypeValue: String) {
    HTML("HTML"),
    VAST("VAST");

    override fun toString(): String {
        return this.creativeTypeValue
    }
}