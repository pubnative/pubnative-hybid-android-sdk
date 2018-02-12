package net.pubnative.lite.sdk.tracking;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class PNLiteCrashTrackerException extends Throwable {

    /**
     * The name of the exception (used instead of the exception class)
     */
    private final String name;

    /**
     * Constructor
     *
     * @param name    The name of the exception (used instead of the exception class)
     * @param message The exception message
     * @param frames  The exception stack trace
     */
    public PNLiteCrashTrackerException(String name, String message, StackTraceElement[] frames) {
        super(message);

        super.setStackTrace(frames);
        this.name = name;
    }

    /**
     * @return The name of the exception (used instead of the exception class)
     */
    public String getName() {
        return name;
    }
}
