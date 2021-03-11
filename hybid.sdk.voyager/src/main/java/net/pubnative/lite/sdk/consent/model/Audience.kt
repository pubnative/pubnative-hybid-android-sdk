package net.pubnative.lite.sdk.consent.model

import net.pubnative.lite.sdk.consent.db.Database
import net.pubnative.lite.sdk.consent.db.Schema

@Database(tableName = "audience")
data class Audience(
        @Schema(generatedId = true, field = "Id", autoIncrease = true, nonNullable = true) val id: Int? = 0,
        @Schema(field = "audience_id") var audience_id: String?,
        @Schema(field = "taxonomy2_ids") var taxonomy2_ids: String?,
        @Schema(field = "taxonomy3_ids") var taxonomy3_ids: String?,
        @Schema(field = "start_time") var start_time: Int?,
        @Schema(field = "end_time") var end_time: Int?,
        @Schema(field = "upper_limit") var upper_limit: Int?,
        @Schema(field = "name_query") var name_query: String?,
)