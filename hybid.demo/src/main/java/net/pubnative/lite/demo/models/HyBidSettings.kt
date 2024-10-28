package net.pubnative.lite.demo.models


class HyBidSettings private constructor(builder: Builder) {

    val appToken: String?
    val zoneIds: List<String>?
    val apiUrl: String?
    val gender: String?
    val age: String?
    val keywords: List<String>?
    val browserPriorities: List<String>?
    val coppa: Boolean?
    val testMode: Boolean?
    val topicsApi: Boolean?
    val reportingEnabled: Boolean?

    init {
        this.appToken = builder.appToken
        this.zoneIds = builder.zoneIds
        this.apiUrl = builder.apiUrl
        this.gender = builder.gender
        this.age = builder.age
        this.keywords = builder.keywords
        this.browserPriorities = builder.browserPriorities
        this.coppa = builder.coppa
        this.testMode = builder.testMode
        this.topicsApi = builder.topicsApi
        this.reportingEnabled = builder.reportingEnabled
    }

    class Builder {

        var appToken: String? = null
            private set
        var zoneIds: List<String>? = null
            private set
        var apiUrl: String? = null
            private set
        var gender: String? = null
            private set
        var age: String? = null
            private set
        var keywords: List<String>? = null
            private set
        var browserPriorities: List<String>? = null
            private set
        var coppa: Boolean? = null
            private set
        var testMode: Boolean? = null
            private set
        var topicsApi: Boolean? = null
            private set
        var reportingEnabled: Boolean? = null
            private set

        fun appToken(appToken: String) = apply { this.appToken = appToken }
        fun zoneIds(zoneIds: List<String>?) = apply { this.zoneIds = zoneIds }
        fun apiUrl(apiUrl: String) = apply { this.apiUrl = apiUrl }
        fun gender(gender: String) = apply { this.gender = gender }
        fun age(age: String) = apply { this.age = age }
        fun keywords(keywords: List<String>?) = apply { this.keywords = keywords }
        fun browserPriorities(browserPriorities: List<String>?) =
            apply { this.browserPriorities = browserPriorities }

        fun coppa(coppa: Boolean) = apply { this.coppa = coppa }
        fun testMode(testMode: Boolean) = apply { this.testMode = testMode }
        fun topicsApi(topicsApi: Boolean) = apply { this.topicsApi = topicsApi }
        fun reportingEnabled(reportingEnabled: Boolean) = apply { this.reportingEnabled = reportingEnabled }
        fun build() = HyBidSettings(this)
    }
}