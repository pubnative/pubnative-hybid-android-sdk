// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.bidstream;

public final class BidstreamConstants {
    public final static class PlacementPosition {
        public final static int UNKNOWN = 0;
        public final static int FULLSCREEN = 7;
    }

    public final static class ExpandableDirections {
        public final static int FULLSCREEN = 5;
        public final static int RESIZE_MINIMIZE = 6;
    }

    public final static class VideoPlacement {
        public final static int INTERSTITIAL = 5;
    }

    public final static class VideoPlacementSubtype {
        public final static int INTERSTITIAL = 3;
        public final static int STANDALONE = 4;
    }

    public final static class VideoPlaybackMethod {
        public final static int PAGE_LOAD_SOUND_ON = 1;
        public final static int PAGE_LOAD_SOUND_OFF = 2;
        public final static int ENTER_VIEWPORT_SOUND_ON = 5;
        public final static int ENTER_VIEWPORT_SOUND_OFF = 6;
    }
}
