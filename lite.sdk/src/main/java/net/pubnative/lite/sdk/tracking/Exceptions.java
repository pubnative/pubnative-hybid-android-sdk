package net.pubnative.lite.sdk.tracking;

import java.io.IOException;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

class Exceptions implements JsonStream.Streamable {
    private final Configuration config;
    private final Throwable exception;

    Exceptions(Configuration config, Throwable exception) {
        this.config = config;
        this.exception = exception;
    }

    @Override
    public void toStream(JsonStream writer) throws IOException {
        writer.beginArray();

        // Unwrap any "cause" exceptions
        Throwable currentEx = exception;
        while (currentEx != null) {
            if (currentEx instanceof JsonStream.Streamable) {
                ((JsonStream.Streamable) currentEx).toStream(writer);
            } else {
                String exceptionName = getExceptionName(currentEx);
                String localizedMessage = currentEx.getLocalizedMessage();
                StackTraceElement[] stackTrace = currentEx.getStackTrace();
                exceptionToStream(writer, exceptionName, localizedMessage, stackTrace);
            }
            currentEx = currentEx.getCause();
        }

        writer.endArray();
    }

    /**
     * Get the class name from the exception contained in this Error report.
     */
    private String getExceptionName(Throwable throwable) {
        if (throwable instanceof PNLiteCrashTrackerException) {
            return ((PNLiteCrashTrackerException) throwable).getName();
        } else {
            return throwable.getClass().getName();
        }
    }

    private void exceptionToStream(JsonStream writer,
                                   String name,
                                   String message,
                                   StackTraceElement[] frames) throws IOException {
        writer.beginObject();
        writer.name("errorClass").value(name);
        writer.name("message").value(message);
        writer.name("type").value(config.defaultExceptionType);

        Stacktrace stacktrace = new Stacktrace(config, frames);
        writer.name("stacktrace").value(stacktrace);
        writer.endObject();
    }
}
