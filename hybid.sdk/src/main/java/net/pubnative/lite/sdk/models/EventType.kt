package net.pubnative.lite.sdk.models

enum class EventType(private val eventTypeValue: String) {
    IMPRESSION("impression"),
    CLICK("click"),
    VIDEO_STARTED("video_started"),
    VIDEO_DISMISSED("video_dismissed"),
    VIDEO_FINISHED("video_finished"),
    VIDEO_MUTE("video_mute"),
    VIDEO_UNMUTE("video_unmute"), ;

    override fun toString(): String {
        return eventTypeValue
    }
}
