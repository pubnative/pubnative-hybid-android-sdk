package net.pubnative.lite.sdk.analytics;

import java.util.Arrays;

public class CrashController {

    public CrashController() {
    }

    public synchronized ReportingEvent formatException(Exception exception) {
        ReportingEvent event = new ReportingEvent();
        event.setEventType(Reporting.EventType.ERROR);
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

    public ReportingEvent formatException(Throwable exception) {
        ReportingEvent event = new ReportingEvent();
        event.setEventType(Reporting.EventType.ERROR);
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
