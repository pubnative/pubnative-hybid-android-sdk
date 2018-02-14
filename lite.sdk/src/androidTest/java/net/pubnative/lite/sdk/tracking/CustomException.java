package net.pubnative.lite.sdk.tracking;

import java.io.IOException;

/**
 * Created by erosgarciaponte on 13.02.18.
 */

public class CustomException extends Exception implements JsonStream.Streamable {

    CustomException(String message) {
        super(message);
    }

    @Override
    public void toStream(JsonStream writer) throws IOException {
        writer.beginObject();
        writer.name("errorClass").value("CustomizedException");
        writer.name("message").value(getLocalizedMessage());
        writer.name("stacktrace");
        writer.beginArray();

        writer.beginObject();
        writer.name("file").value("MyFile.java");
        writer.name("lineNumber").value(408);
        writer.name("offset").value(18);
        writer.name("method").value("MyFile.run");
        writer.endObject();

        writer.endArray();
        writer.endObject();
    }
}
