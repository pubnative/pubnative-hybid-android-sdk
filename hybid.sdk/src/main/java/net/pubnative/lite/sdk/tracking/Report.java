package net.pubnative.lite.sdk.tracking;

import java.io.File;
import java.io.IOException;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class Report implements JsonStream.Streamable {
    private final File errorFile;
    private Error error;
    private Notifier notifier;
    private String apiKey;

    Report(String apiKey, File errorFile) {
        this.error = null;
        this.errorFile = errorFile;
        this.notifier = Notifier.getInstance();
        this.apiKey = apiKey;
    }

    Report(String apiKey, Error error) {
        this.error = error;
        this.errorFile = null;
        this.notifier = Notifier.getInstance();
        this.apiKey = apiKey;
    }

    @Override
    public void toStream(JsonStream writer) throws IOException {
        // Create a JSON stream and top-level object
        writer.beginObject();

        writer.name("apiKey").value(apiKey);
        writer.name("payloadVersion").value("4.0");

        // Write the notifier info
        writer.name("notifier").value(notifier);

        // Start events array
        writer.name("events").beginArray();

        // Write in-memory event
        if (error != null) {
            writer.value(error);
        }

        // Write on-disk event
        if (errorFile != null) {
            writer.value(errorFile);
        }

        // End events array
        writer.endArray();

        // End the main JSON object
        writer.endObject();
    }

    public Error getError() {
        return error;
    }
}
