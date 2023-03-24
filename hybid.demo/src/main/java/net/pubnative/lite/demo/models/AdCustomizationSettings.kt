package net.pubnative.lite.demo.models

class AdCustomizationSettings private constructor(builder: Builder){

    val mraidExpanded: Boolean?
    val locationTracking: Boolean?
    val locationUpdates: Boolean?
    val customSkipOffsetDisabled: Boolean?
    val closeVideoAfterFinish: Boolean?
    val closeVideoAfterFinishForRewardedVideo: Boolean?
    val enableEndcard: Boolean?
    val skipOffset: Int?
    val videoSkipOffset: Int?
    val endCardCloseButtonDelay: Int?
    val videoClickBehaviour: Boolean?
    val initialAudioState: Int?
    val countdownStyle: String?

    init {
        this.mraidExpanded = builder.mraidExpanded
        this.locationTracking = builder.locationTracking
        this.locationUpdates = builder.locationUpdates
        this.closeVideoAfterFinish = builder.closeVideoAfterFinish
        this.closeVideoAfterFinishForRewardedVideo = builder.closeVideoAfterFinishForRewardedVideo
        this.enableEndcard = builder.enableEndcard
        this.skipOffset = builder.skipOffset
        this.videoSkipOffset = builder.videoSkipOffset
        this.endCardCloseButtonDelay = builder.endCardCloseButtonDelay
        this.videoClickBehaviour = builder.videoClickBehaviour
        this.initialAudioState = builder.initialAudioState
        this.countdownStyle = builder.countdownStyle
        this.customSkipOffsetDisabled = builder.customSkipOffsetDisabled
    }

    class Builder {
        var mraidExpanded: Boolean? = null
            private set
        var locationTracking: Boolean? = null
            private set
        var customSkipOffsetDisabled: Boolean? = null
            private set
        var locationUpdates: Boolean? = null
            private set
        var closeVideoAfterFinish: Boolean? = null
            private set
        var closeVideoAfterFinishForRewardedVideo: Boolean? = null
            private set
        var enableEndcard: Boolean? = null
            private set
        var skipOffset: Int? = null
            private set
        var videoSkipOffset: Int? = null
            private set
        var endCardCloseButtonDelay: Int? = null
            private set
        var videoClickBehaviour: Boolean? = null
            private set
        var initialAudioState: Int? = null
            private set
        var countdownStyle: String? = null
            private set

        fun mraidExpanded(mraidExpanded: Boolean) = apply { this.mraidExpanded = mraidExpanded }
        fun locationTracking(locationTracking: Boolean) =
            apply { this.locationTracking = locationTracking }

        fun customSkipOffsetDisabled(disabled: Boolean) =
            apply { this.customSkipOffsetDisabled = disabled }

        fun locationUpdates(locationUpdates: Boolean) =
            apply { this.locationUpdates = locationUpdates }

        fun closeVideoAfterFinish(closeVideoAfterFinish: Boolean) =
            apply { this.closeVideoAfterFinish = closeVideoAfterFinish }

        fun closeVideoAfterFinishForRewardedVideo(closeVideoAfterFinishForRewardedVideo: Boolean) =
            apply {
                this.closeVideoAfterFinishForRewardedVideo = closeVideoAfterFinishForRewardedVideo
            }

        fun enableEndcard(enableEndcard: Boolean) =
            apply { this.enableEndcard = enableEndcard }

        fun skipOffset(skipOffset: Int) =
            apply { this.skipOffset = skipOffset }

        fun videoSkipOffset(videoSkipOffset: Int) =
            apply { this.videoSkipOffset = videoSkipOffset }

        fun endCardCloseButtonDelay(endCardCloseButtonDelay: Int) =
            apply { this.endCardCloseButtonDelay = endCardCloseButtonDelay }

        fun videoClickBehaviour(videoClickBehaviour: Boolean) =
            apply { this.videoClickBehaviour = videoClickBehaviour }

        fun initialAudioState(initialAudioState: Int) =
            apply { this.initialAudioState = initialAudioState }

        fun countdownStyle(countdownStyle: String) =
            apply { this.countdownStyle = countdownStyle }

        fun build() = AdCustomizationSettings(this)
    }
}