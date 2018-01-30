package net.pubnative.lite.demo.models

/**
 * Created by erosgarciaponte on 30.01.18.
 */
data class SettingsModel(var appToken: String,
                         var zoneIds: List<String>,
                         var gender: String,
                         var age: String,
                         var keywords: List<String>,
                         var coppa: Boolean,
                         var testMode: Boolean)