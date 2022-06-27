package net.pubnative.lite.sdk.analytics;

public class Reporting {
    public static final class Key {
        public static final String CATEGORY_ID = "category_id";
        public static final String CAMPAIGN_ID = "campaign_id";
        public static final String CREATIVE_ID = "creative_id";
        public static final String APP_TOKEN = "app_token";
        public static final String PLACEMENT_ID = "placement_id";
        public static final String EVENT_TYPE = "event_type";
        public static final String INTEGRATION_TYPE = "integration_type";
        public static final String CREATIVE_TYPE = "creative_type";
        public static final String CREATIVE = "creative";
        public static final String AD_FORMAT = "ad_format";
        public static final String AD_SIZE = "ad_size";
        public static final String AD_TYPE = "ad_type";
        public static final String ZONE_ID = "zone_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String BID_PRICE = "bid_price";
        public static final String PARTICIPANTS = "participants";
        public static final String VAST = "vast";
        public static final String CACHE_TIME = "cache_time";
        public static final String AD_REQUEST = "ad_request";
        public static final String AD_RESPONSE = "ad_response";
        public static final String ERROR_CODE = "error_code";
        public static final String ERROR_MESSAGE = "error_message";
        public static final String HYBID_VERSION = "hybid_version";
        public static final String MEDIATION_VENDOR = "mediation_vendor";
        public static final String AD_POSITION = "ad_position";
        public static final String OM_ENABLED = "om_enabled";
        public static final String OM_VENDORS = "om_vendors";
        public static final String HAS_END_CARD = "has_end_card";
        public static final String RESPONSE_TIME = "response_time";
        public static final String TIME_TO_LOAD = "time_to_load";
        public static final String TIME_TO_LOAD_FAILED = "time_to_load_failed";
        public static final String RENDER_TIME = "render_time";
        public static final String FIRED_IMPRESSION_BEACONS = "fired_impressions";
        public static final String FIRED_CLICK_BEACONS = "fired_clicks";
    }

    public static final class EventType {
        public static final String SDK_INIT = "init";
        public static final String REQUEST = "request";
        public static final String RESPONSE = "response";
        public static final String CACHE = "cache";
        public static final String CACHE_FAIL = "cache_fail";
        public static final String LOAD = "load";
        public static final String RENDER = "render";
        public static final String LOAD_FAIL = "load_fail";
        public static final String IMPRESSION = "impression";
        public static final String ERROR = "error";
        public static final String CLICK = "click";
        public static final String INTERSTITIAL_CLOSED = "interstitial_closed";
        public static final String VIDEO_STARTED = "video_started";
        public static final String VIDEO_DISMISSED = "video_dismissed";
        public static final String VIDEO_FINISHED = "video_finished";
        public static final String VIDEO_PAUSE = "video_pause";
        public static final String VIDEO_RESUME = "video_resume";
        public static final String VIDEO_MUTE = "video_mute";
        public static final String VIDEO_UNMUTE = "video_unmute";
        public static final String RENDER_ERROR = "render_error";
        public static final String AUCTION_START = "auction_start";
        public static final String AUCTION_FINISHED = "auction_finished";
        public static final String AUCTION_NEXT_ITEM = "auction_next_item";
        public static final String FILL = "fill";
        public static final String NO_FILL = "no_fill";
        public static final String WINNER = "winner";
        public static final String NEXT_AD_SOURCE = "next_ad_source";
        public static final String VIDEO_AD_SESSION_INITIALIZED = "session_initialized";
        public static final String VIDEO_AD_SESSION_LOADED = "session_loaded";
        public static final String VIDEO_AD_SESSION_STARTED = "session_started";
        public static final String VIDEO_AD_FIRST_QUARTILE = "first_quartile";
        public static final String VIDEO_AD_MIDPOINT = "midpoint";
        public static final String VIDEO_AD_THIRD_QUARTILE = "third_quartile";
        public static final String VIDEO_AD_COMPLETE = "ad_complete";
        public static final String VIDEO_AD_PAUSE = "pause";
        public static final String VIDEO_AD_RESUME = "resume";
        public static final String VIDEO_AD_BUFFER_START = "buffer_start";
        public static final String VIDEO_AD_BUFFER_FINISH = "buffer_finish";
        public static final String VIDEO_AD_VOLUME_CHANGE = "volume_change";
        public static final String VIDEO_AD_SKIPPED = "skipped";
        public static final String VIDEO_AD_CLICKED = "clicked";
        public static final String VIDEO_AD_IMPRESSION = "impression";
        public static final String WEB_AD_SESSION_INITIALIZED = "web_ad_session_started";
        public static final String VIDEO_AD_SESSION_STOPPED = "session_stopped";
    }

    public static final class CreativeType {
        public static final String STANDARD = "standard";
        public static final String VIDEO = "video";
    }

    public static final class AdFormat {
        public static final String NATIVE = "native";
        public static final String BANNER = "banner";
        public static final String FULLSCREEN = "fullscreen";
        public static final String REWARDED = "rewarded";
    }
}
