package net.pubnative.lite.sdk.consent.model

import net.pubnative.lite.sdk.consent.db.Database
import net.pubnative.lite.sdk.consent.db.Schema

@Database(tableName = "visit")
data class Visit(
        @Schema(generatedId = true, field = "Id", autoIncrease = true, nonNullable = true) val id: Int? = 0,
        @Schema(field = "session_id") var session_id: String?,
        @Schema(field = "start_time") var start_time: Long?,
        @Schema(field = "end_time") var end_time: Long?,
        @Schema(field = "cluster_longitude") var cluster_longitude: Double?,
        @Schema(field = "cluster_latitude") var cluster_latitude: Double?,
        @Schema(field = "distance") var distance: Double?,
        @Schema(field = "usability_score") var usability_score: Double?,
        @Schema(field = "final_score") var final_score: Double?,
)
