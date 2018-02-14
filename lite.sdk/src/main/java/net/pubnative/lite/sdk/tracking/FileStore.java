package net.pubnative.lite.sdk.tracking;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

abstract class FileStore<T extends JsonStream.Streamable> {

    protected final Configuration config;
    final String storeDirectory;
    private final int maxStoreCount;
    private final Comparator<File> comparator;

    FileStore(Configuration config, Context appContext, String folder,
              int maxStoreCount, Comparator<File> comparator) {
        this.config = config;
        this.maxStoreCount = maxStoreCount;
        this.comparator = comparator;

        String path;
        try {
            path = appContext.getCacheDir().getAbsolutePath() + folder;

            File outFile = new File(path);
            outFile.mkdirs();
            if (!outFile.exists()) {
                Logger.warn("Could not prepare file storage directory");
                path = null;
            }
        } catch (Exception exception) {
            Logger.warn("Could not prepare file storage directory", exception);
            path = null;
        }
        this.storeDirectory = path;
    }

    String write(T streamable) {
        if (storeDirectory == null) {
            return null;
        }

        // Limit number of saved errors to prevent disk space issues
        File exceptionDir = new File(storeDirectory);
        if (exceptionDir.isDirectory()) {
            File[] files = exceptionDir.listFiles();
            if (files != null && files.length >= maxStoreCount) {
                // Sort files then delete the first one (oldest timestamp)
                Arrays.sort(files, comparator);
                Logger.warn(String.format("Discarding oldest error as stored "
                        + "error limit reached (%s)", files[0].getPath()));
                if (!files[0].delete()) {
                    files[0].deleteOnExit();
                }
            }
        }

        String filename = getFilename(streamable);

        Writer out = null;
        try {
            out = new FileWriter(filename);

            JsonStream stream = new JsonStream(out);
            stream.value(streamable);
            stream.close();

            Logger.info(String.format("Saved unsent payload to disk (%s) ", filename));
            return filename;
        } catch (Exception exception) {
            Logger.warn(String.format("Couldn't save unsent payload to disk (%s) ",
                    filename), exception);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return null;
    }

    abstract String getFilename(T streamable);

    List<File> findStoredFiles() {
        List<File> files = new ArrayList<>();

        if (storeDirectory != null) {
            File dir = new File(storeDirectory);

            if (dir.exists() && dir.isDirectory()) {
                File[] values = dir.listFiles();

                if (values != null) {
                    files.addAll(Arrays.asList(values));
                }
            }
        }
        return files;
    }
}
