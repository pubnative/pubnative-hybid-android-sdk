package net.pubnative.lite.sdk.consent.model

import net.pubnative.lite.sdk.consent.db.Database
import net.pubnative.lite.sdk.consent.db.Schema

@Database(tableName = "ad_analytics_event")
data class AdAnalyticsEvent(
        @Schema(generatedId = true, field = "Id", autoIncrease = true, nonNullable = true) val id: Int? = 0,
        @Schema(field = "placement_id") var placement_id: String?,
        @Schema(field = "event_type") var event_type: String?,
        @Schema(field = "creative_type") var creative_type: String?,
        @Schema(field = "ad_format") var ad_format: String?,
        @Schema(field = "ad_size") var ad_size: String?,
        @Schema(field = "datetime") var datetime: Long?,
        @Schema(field = "time_from_load") var time_from_load: Long?,
        @Schema(field = "time_from_show") var time_from_show: Long?,
        @Schema(field = "video_position") var video_position: Int?
)