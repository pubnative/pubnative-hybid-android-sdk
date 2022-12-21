package net.pubnative.lite.sdk.models;

public enum RemoteConfig {

    AUDIO_STATE("audio_state", String.class),
    END_CARD_ENABLED("endcard_enabled", Boolean.class),
    END_CARD_CLOSE_DELAY("endcard_close_delay", Integer.class),
    HTML_SKIP_OFFSET("html_skip_offset", Integer.class),
    VIDEO_SKIP_OFFSET("video_skip_offset", Integer.class),
    CLOSE_INTER_AFTER_FINISH("close_inter_after_finished", Boolean.class),
    CLOSE_REWARD_AFTER_FINISH("close_reward_after_finished", Boolean.class);

    public final String fieldName;
    public final Class<?> type;

    RemoteConfig(String fieldName, Class<?> type) {
        this.fieldName = fieldName;
        this.type = type;
    }
}
