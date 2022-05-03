package net.pubnative.lite.sdk.vpaid;

public final class VpaidConstants {

    public static final String FILE_FOLDER = "PNVpaidAds";

    public static final long CACHED_VIDEO_LIFE_TIME = (long) 1000 * 60 * 60 * 32; //32 hours
    static final int DEFAULT_EXPIRED_TIME = 1000 * 60 * 10; //10 minutes
    static final long FETCH_TIMEOUT = (long) 1000 * 60 * 3; //3 minutes
    static final long PREPARE_PLAYER_TIMEOUT = (long) 1000 * 15; //10 sec

    private VpaidConstants() {
    }
}