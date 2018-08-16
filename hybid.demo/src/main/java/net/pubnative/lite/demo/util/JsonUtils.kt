package net.pubnative.lite.demo.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

class JsonUtils {
    companion object {
        @JvmStatic
        fun toFormattedJson(json: String): String {
            val parser = JsonParser()
            val jsonObject = parser.parse(json).asJsonObject

            val gson = GsonBuilder().setPrettyPrinting().create()
            return gson.toJson(jsonObject)
        }
    }
}