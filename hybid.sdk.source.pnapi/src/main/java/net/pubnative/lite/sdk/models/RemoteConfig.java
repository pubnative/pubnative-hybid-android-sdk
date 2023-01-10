package net.pubnative.lite.sdk.models;

public enum RemoteConfig {

    AUDIO_STATE("audiostate", String.class),
    END_CARD_ENABLED("endcardenabled", Boolean.class),
    END_CARD_CLOSE_DELAY("endcard_close_delay", Integer.class),
    HTML_SKIP_OFFSET("html_skip_offset", Integer.class),
    VIDEO_SKIP_OFFSET("video_skip_offset", Integer.class),
    CLOSE_INTER_AFTER_FINISH("close_inter_after_finished", Boolean.class),
    CLOSE_REWARD_AFTER_FINISH("close_reward_after_finished", Boolean.class),
    IMP_TRACKING_METHOD("imp_tracking", String.class),
    IMP_TRACKING_VISIBLE_TIME("min_visible_time", Integer.class),
    IMP_TRACKING_VISIBLE_PERCENT("min_visible_percent", Double.class),
    FULL_SCREEN_CLICKABILITY("fullscreen_clickability", Boolean.class);

    public final String fieldName;
    public final Class<?> type;

    RemoteConfig(String fieldName, Class<?> type) {
        this.fieldName = fieldName;
        this.type = type;
    }
}
