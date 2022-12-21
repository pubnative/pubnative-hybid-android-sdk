package net.pubnative.lite.demo.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import net.pubnative.lite.sdk.utils.Logger
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class JsonUtils {
    companion object {
        val TAG = JsonUtils::class.java.simpleName

        @JvmStatic
        fun toFormattedJson(json: String): String {
            try {
                val jsonObject = JsonParser.parseString(json).asJsonObject
                val gson = GsonBuilder().setLenient().setPrettyPrinting().create()
                return gson.toJson(jsonObject)
            } catch (exception: Exception) {
                Logger.e(TAG, "Error parsing json: ", exception)
                return json
            }
        }

        fun isValidJson(json: String?): Boolean {
            if (json == null || json.isEmpty()) return false

            try {
                JSONObject(json)
            } catch (ex: Exception) {
                try {
                    JSONArray(json)
                } catch (ex1: JSONException) {
                    return false
                }
            }
            return true
        }
    }
}