package net.pubnative.lite.sdk.consent.model

import net.pubnative.lite.sdk.consent.db.Database
import net.pubnative.lite.sdk.consent.db.Schema

@Database(tableName = "ad_analytics_aggregated")
data class AdAnalyticsEventAggregated(
        @Schema(generatedId = true, field = "Id", autoIncrease = true, nonNullable = true) val id: Int? = 0,
        @Schema(field = "creative_id") val creative_id: Int? = 0,
        @Schema(field = "placement_id") val placement_id: Int? = 0,
        @Schema(field = "impressions") var impressions: Int?,
        @Schema(field = "clicks") var clicks: Int?,
        @Schema(field = "video_started") var video_started: Long?,
        @Schema(field = "video_finished") var video_finished: Long?,
        @Schema(field = "video_dismissed") var video_dismissed: Long?,
        @Schema(field = "video_view_time") var video_view_time: Long?,
        @Schema(field = "interstitial_visible_time") var interstitial_visible_time: Long?,
        @Schema(field = "banner_visible_time") var banner_visible_time: Long?,
        @Schema(field = "video_muted") var video_muted: Long?,
        @Schema(field = "video_unmuted") var video_unmuted: Long?,
        @Schema(field = "time_to_click_html") var time_to_click_html: Long?,
        @Schema(field = "time_to_click_vast") var time_to_click_vast: Long?,
        @Schema(field = "video_position_click") var video_position_click: Long?,
        @Schema(field = "video_position_dismiss") var video_position_dismiss: Long?
)