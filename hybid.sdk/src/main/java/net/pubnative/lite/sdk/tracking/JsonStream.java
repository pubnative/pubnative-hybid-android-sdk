package net.pubnative.lite.sdk.tracking;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class JsonStream extends JsonWriter {
    public interface Streamable {
        void toStream(JsonStream stream) throws IOException;
    }

    private final Writer out;

    public JsonStream(Writer out) {
        super(out);
        setSerializeNulls(false);
        this.out = out;
    }

    // Allow chaining name().value()
    public JsonStream name(String name) throws IOException {
        super.name(name);
        return this;
    }

    public void value(Streamable streamable) throws IOException {
        if (streamable == null) {
            nullValue();
            return;
        }
        streamable.toStream(this);
    }

    public void value(File file) throws IOException {
        super.flush();
        beforeValue(false); // add comma if in array

        // Copy the file contents onto the stream
        FileReader input = null;
        try {
            input = new FileReader(file);
            IOUtils.copy(input, out);
        } finally {
            IOUtils.closeQuietly(input);
        }

        out.flush();
    }
}
