package net.pubnative.lite.sdk.tracking;

import java.io.IOException;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

class Stacktrace implements JsonStream.Streamable {
        final Configuration config;
        final StackTraceElement[] stacktrace;

        Stacktrace(Configuration config, StackTraceElement[] stacktrace) {
            this.config = config;
            this.stacktrace = stacktrace;
        }

        @Override
        public void toStream(JsonStream writer) throws IOException {
            writer.beginArray();

            for (StackTraceElement el : stacktrace) {
                try {
                    writer.beginObject();
                    writer.name("method").value(el.getClassName() + "." + el.getMethodName());
                    writer.name("file").value(el.getFileName() == null ? "Unknown" : el.getFileName());
                    writer.name("lineNumber").value(el.getLineNumber());

                    if (config.inProject(el.getClassName())) {
                        writer.name("inProject").value(true);
                    }

                    writer.endObject();
                } catch (Exception lineEx) {
                    lineEx.printStackTrace(System.err);
                }
            }

            writer.endArray();
        }
}
