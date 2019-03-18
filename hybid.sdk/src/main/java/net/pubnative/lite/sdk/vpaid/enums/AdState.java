package net.pubnative.lite.sdk.vpaid.enums;

/**
 * Enum with all possible ad states.
 */
public class AdState {
    /**
     * Initial state of ad right after creation.
     * Can be also after onHide() notification or destroy().
     */
    public static final int NONE = 200;

    /**
     * Ad currently in "loading" process.
     * Can be between trigger load() and onLoadSuccess(), onLoadFail() notifications or destroy().
     * While Ad in this state all other calling `load` methods will be ignored
     */
    public static final int LOADING = 201;

    /**
     * Ad currently displays on screen.
     * Can be between trigger show() and onHide() notification or destroy()
     */
    public static final int SHOWING = 202;

    private AdState() {
    }
}