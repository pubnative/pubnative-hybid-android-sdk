package net.pubnative.lite.sdk.tracking;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class PNLiteCrashTrackerException extends Throwable {

    private final String name;

    public PNLiteCrashTrackerException(String name, String message, StackTraceElement[] frames) {
        super(message);

        super.setStackTrace(frames);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
