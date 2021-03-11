package net.pubnative.lite.sdk.consent.model

import net.pubnative.lite.sdk.consent.db.Database
import net.pubnative.lite.sdk.consent.db.Schema

@Database(tableName = "location")
data class Location(
        @Schema(generatedId = true, field = "Id", autoIncrease = true, nonNullable = true) val id: Int? = 0,
        @Schema(field = "latitude") var latitude: Double?,
        @Schema(field = "longitude") var longitude: Double?,
        @Schema(field = "horizontal_accuracy") var horizontal_accuracy: Double?,
        @Schema(field = "connection_type") var connection_type: String?,
        @Schema(field = "session_ID") var session_ID: String?,
)