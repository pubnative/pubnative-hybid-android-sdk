package net.pubnative.lite.sdk.models;

public enum RemoteConfig {

    AUDIO_STATE("audiostate", String.class),
    END_CARD_ENABLED("endcardenabled", Boolean.class),
    CUSTOM_END_CARD_ENABLED("custom_endcard_enabled", Boolean.class),
    CUSTOM_END_CARD_DISPLAY("custom_endcard_display", String.class),
    END_CARD_CLOSE_DELAY("endcard_close_delay", Integer.class),
    HTML_SKIP_OFFSET("html_skip_offset", Integer.class),
    REWARDED_HTML_SKIP_OFFSET("rewarded_html_skip_offset", Integer.class),
    REWARDED_VIDEO_SKIP_OFFSET("rewarded_video_skip_offset", Integer.class),
    VIDEO_SKIP_OFFSET("video_skip_offset", Integer.class),
    CLOSE_INTER_AFTER_FINISH("close_inter_after_finished", Boolean.class),
    CLOSE_REWARD_AFTER_FINISH("close_reward_after_finished", Boolean.class),
    IMP_TRACKING_METHOD("imp_tracking", String.class),
    IMP_TRACKING_VISIBLE_TIME("min_visible_time", Integer.class),
    IMP_TRACKING_VISIBLE_PERCENT("min_visible_percent", Double.class),
    CONTENT_INFO_URL("content_info_url", String.class),
    CONTENT_INFO_ICON_URL("content_info_icon_url", String.class),
    CONTENT_INFO_ICON_CLICK_ACTION("content_info_icon_click_action", String.class),
    CONTENT_INFO_HORIZONTAL_POSITION("content_info_horizontal_position", String.class),
    CONTENT_INFO_VERTICAL_POSITION("content_info_vertical_position", String.class),
    CONTENT_INFO_DISPLAY("content_info_display", String.class),
    CONTENT_INFO_TEXT("content_info_text", String.class),
    FULL_SCREEN_CLICKABILITY("fullscreen_clickability", Boolean.class),
    MRAID_EXPAND("mraid_expand", Boolean.class),
    NATIVE_CLOSE_BUTTON_DELAY("close_button_delay", Integer.class),
    CUSTOM_CTA_ENABLED("custom_cta_enabled", Boolean.class),
    CUSTOM_CTA_TYPE("custom_cta_type", String.class),
    CUSTOM_CTA_DELAY("custom_cta_delay", Integer.class),
    NAVIGATION_MODE("navigation_mode", String.class),
    LANDING_PAGE("landing_page", Boolean.class),
    TOPICS_API_ENABLED("topics_api_enabled", Boolean.class),
    ATOM_ENABLED("atom_enabled", Boolean.class),

    PC_END_CARD_ENABLED("pc_endcardenabled", Boolean.class),
    PC_HTML_SKIP_OFFSET("pc_html_skip_offset", Integer.class),
    PC_VIDEO_SKIP_OFFSET("pc_video_skip_offset", Integer.class),
    PC_REWARDED_HTML_SKIP_OFFSET("pc_rewarded_html_skip_offset", Integer.class),
    PC_REWARDED_VIDEO_SKIP_OFFSET("pc_rewarded_video_skip_offset", Integer.class),
    PC_END_CARD_CLOSE_DELAY("pc_endcard_close_delay", Integer.class),
    PC_REDUCED_ICON_SIZES("pc_reduced_icon_sizes", Boolean.class),

    BC_VIDEO_SKIP_OFFSET("bc_video_skip_offset", Integer.class),
    BC_REWARDED_VIDEO_SKIP_OFFSET("bc_rewarded_video_skip_offset", Integer.class),
    BC_END_CARD_CLOSE_DELAY("bc_endcard_close_delay", Integer.class),
    BC_HIDE_CONTROLS("bc_hide_controls", Boolean.class);

    public final String fieldName;
    public final Class<?> type;

    RemoteConfig(String fieldName, Class<?> type) {
        this.fieldName = fieldName;
        this.type = type;
    }
}
