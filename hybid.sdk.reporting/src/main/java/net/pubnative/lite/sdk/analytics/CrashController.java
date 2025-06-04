// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.analytics;

import java.util.Arrays;

public class CrashController {

    public CrashController() {
    }

    public ReportingEvent formatException(Exception exception) {
        ReportingEvent event = new ReportingEvent();
        event.setEventType(Reporting.EventType.ERROR);
        event.setPlatform(Reporting.Platform.ANDROID);
        if (exception != null) {
            if (exception.getMessage() != null)
                event.setErrorMessage(exception.getMessage());
            StackTraceElement[] stackTrace = exception.getStackTrace();
            if (stackTrace != null)
                event.setCustomString("Stacktrace", Arrays.toString(stackTrace));
            if (exception.getLocalizedMessage() != null)
                event.setCustomString("LocalizedMessage", exception.getLocalizedMessage());
        }
        return event;
    }

    public ReportingEvent formatException(Throwable exception) {
        ReportingEvent event = new ReportingEvent();
        event.setEventType(Reporting.EventType.ERROR);
        event.setPlatform(Reporting.Platform.ANDROID);
        if (exception != null) {
            if (exception.getMessage() != null)
                event.setErrorMessage(exception.getMessage());
            if (exception.getStackTrace() != null)
                event.setCustomString("Stacktrace", Arrays.toString(exception.getStackTrace()));
            if (exception.getLocalizedMessage() != null)
                event.setCustomString("LocalizedMessage", exception.getLocalizedMessage());
        }
        return event;
    }
}
