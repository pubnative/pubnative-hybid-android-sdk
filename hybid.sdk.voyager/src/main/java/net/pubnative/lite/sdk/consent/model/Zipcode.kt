package net.pubnative.lite.sdk.consent.model

import net.pubnative.lite.sdk.consent.db.Database
import net.pubnative.lite.sdk.consent.db.Schema

@Database(tableName = "zipcode")
data class Zipcode(
        @Schema(generatedId = true, field = "Id", autoIncrease = true, nonNullable = true) val id: Int? = 0,
        @Schema(field = "zipcode") var zipcode: String?,
        @Schema(field = "zipcode_suffix") var zipcode_suffix: String?,
        @Schema(field = "date_created") var date_created: String?,
        @Schema(field = "date_updated") var date_updated: String?,
        @Schema(field = "poi_count") var poi_count: Int?,
)
