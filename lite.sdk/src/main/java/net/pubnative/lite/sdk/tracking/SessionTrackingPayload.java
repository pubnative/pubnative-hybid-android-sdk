package net.pubnative.lite.sdk.tracking;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class SessionTrackingPayload implements JsonStream.Streamable {

    private final Notifier notifier;
    private final Session session;
    private final DeviceDataSummary deviceDataSummary = new DeviceDataSummary();
    private final AppData appData;
    private final List<File> files;

    SessionTrackingPayload(List<File> files, AppData appData) {
        this.appData = appData;
        this.notifier = Notifier.getInstance();
        this.session = null;
        this.files = files;
    }

    SessionTrackingPayload(Session session, AppData appDataSummary) {
        this.appData = appDataSummary;
        this.notifier = Notifier.getInstance();
        this.session = session;
        this.files = null;
    }

    @Override
    public void toStream(JsonStream writer) throws IOException {
        writer.beginObject();
        writer.name("notifier").value(notifier);
        writer.name("app").value(appData);
        writer.name("device").value(deviceDataSummary);

        writer.name("sessions").beginArray();

        if (session == null) {
            for (File file : files) {
                writer.value(file);
            }
        } else {
            writer.value(session);
        }

        writer.endArray();
        writer.endObject();
    }
}
