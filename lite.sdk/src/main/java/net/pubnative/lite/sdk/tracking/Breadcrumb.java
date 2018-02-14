package net.pubnative.lite.sdk.tracking;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public final class Breadcrumb implements JsonStream.Streamable {

    private static final int MAX_MESSAGE_LENGTH = 140;
    private static final String DEFAULT_NAME = "manual";
    private static final String MESSAGE_METAKEY = "message";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String NAME_KEY = "name";
    private static final String METADATA_KEY = "metaData";
    private static final String TYPE_KEY = "type";

    private final String timestamp;

    private final String name;

    private final BreadcrumbType type;

    private final Map<String, String> metadata;

    Breadcrumb(String message) {
        this(DEFAULT_NAME, BreadcrumbType.MANUAL, Collections.singletonMap(MESSAGE_METAKEY,
                message.substring(0, Math.min(message.length(), MAX_MESSAGE_LENGTH))));
    }

    Breadcrumb(String name,
               BreadcrumbType type,
               Map<String, String> metadata) {
        this.timestamp = DateUtils.toIso8601(new Date());
        this.type = type;
        this.metadata = metadata;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public BreadcrumbType getType() {
        return type;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public void toStream(JsonStream writer) throws IOException {
        writer.beginObject();
        writer.name(TIMESTAMP_KEY).value(this.timestamp);
        writer.name(NAME_KEY).value(this.name);
        writer.name(TYPE_KEY).value(this.type.toString());
        writer.name(METADATA_KEY);
        writer.beginObject();

        // sort metadata alphabetically
        List<String> keys = new ArrayList<>(metadata.keySet());
        Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);

        for (String key : keys) {
            writer.name(key).value(metadata.get(key));
        }

        writer.endObject();
        writer.endObject();
    }

    int payloadSize() throws IOException {
        StringWriter writer = new StringWriter();
        JsonStream jsonStream = new JsonStream(writer);
        toStream(jsonStream);

        return writer.toString().length();
    }
}
