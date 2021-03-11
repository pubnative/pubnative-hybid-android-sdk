package net.pubnative.lite.sdk.consent.model

import net.pubnative.lite.sdk.consent.db.Database
import net.pubnative.lite.sdk.consent.db.Schema

@Database(tableName = "poi")
data class POI(
        @Schema(generatedId = true, field = "Id", autoIncrease = true, nonNullable = true) val id: Int? = 0,
        @Schema(field = "name") var name: String?,
        @Schema(field = "place_taxonomy2_id") var place_taxonomy2_id: Int?,
        @Schema(field = "place_taxonomy3_id") var place_taxonomy3_id: Int?,
        @Schema(field = "zipcode") var zipcode: String?,
        @Schema(field = "zipcode_suffix") var zipcode_suffix: String?,
        @Schema(field = "country") var country: String?,
        @Schema(field = "latitude") var latitude: Double?,
        @Schema(field = "longitude") var longitude: Double?,
        @Schema(field = "is_active") var is_active: Boolean?,
)
