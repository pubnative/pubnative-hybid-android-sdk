package net.pubnative.lite.sdk.consent.model

import net.pubnative.lite.sdk.consent.db.Database
import net.pubnative.lite.sdk.consent.db.Schema

@Database(tableName = "ad_analytics")
data class AdAnalytics(
        @Schema(generatedId = true, field = "Id", autoIncrease = true, nonNullable = true) val id: Int = 0,
        @Schema(field = "impressions") var impressions: Int?,
        @Schema(field = "clicks") var clicks: Int?,
        @Schema(field = "time_between_impression_and_click") var time_between_impression_and_click: String?,
        @Schema(field = "average_view_time") var average_view_time: String?,
        @Schema(field = "time_to_close") var time_to_close: String?,
        @Schema(field = "percentage_of_view") var percentage_of_view: Double?,
        @Schema(field = "percentage_before_skip") var percentage_before_skip: Double?,
)