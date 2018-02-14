package net.pubnative.lite.sdk.tracking;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URLConnection;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

class IOUtils {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;

    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final Exception ioe) {
            // ignore
        }
    }

    public static void close(final URLConnection conn) {
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
        }
    }

    public static int copy(final Reader input,
                           final Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int read;
        while (EOF != (read = input.read(buffer))) {
            output.write(buffer, 0, read);
            count += read;
        }

        if (count > Integer.MAX_VALUE) {
            return -1;
        }

        return (int) count;
    }
}
