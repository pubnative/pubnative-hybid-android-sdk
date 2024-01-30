package net.pubnative.lite.demo.models

class AdCustomizationSettings private constructor(builder: Builder) {

    val locationTrackingEnabled: Boolean?
    val locationTracking: Boolean?
    val locationUpdatesEnabled: Boolean?
    val locationUpdates: Boolean?
    val initialAudioState: Int?

    init {
        this.locationTracking = builder.locationTracking
        this.locationUpdates = builder.locationUpdates
        this.initialAudioState = builder.initialAudioState
        this.locationTrackingEnabled = builder.locationTrackingEnabled
        this.locationUpdatesEnabled = builder.locationUpdatesEnabled
    }

    class Builder {
        var locationTracking: Boolean? = null
            private set
        var locationTrackingEnabled: Boolean? = null
            private set
        var locationUpdates: Boolean? = null
            private set
        var locationUpdatesEnabled: Boolean? = null
            private set
        var initialAudioState: Int? = null
            private set

        fun locationTrackingEnabled(locationTrackingEnabled: Boolean) =
            apply { this.locationTrackingEnabled = locationTrackingEnabled }

        fun locationTracking(locationTracking: Boolean) =
            apply { this.locationTracking = locationTracking }

        fun locationUpdatesEnabled(locationUpdatesEnabled: Boolean) =
            apply { this.locationUpdatesEnabled = locationUpdatesEnabled }

        fun locationUpdates(locationUpdates: Boolean) =
            apply { this.locationUpdates = locationUpdates }

        fun initialAudioState(initialAudioState: Int) =
            apply { this.initialAudioState = initialAudioState }

        fun build() = AdCustomizationSettings(this)
    }
}