package net.pubnative.lite.sdk.tracking;

import java.io.File;

/**
 * Created by erosgarciaponte on 13.02.18.
 */

final class FileUtils {

    private FileUtils() {
    }

    static void clearFilesInDir(File storageDir) {
        if (!storageDir.isDirectory()) {
            throw new IllegalArgumentException();
        }
        for (File file : storageDir.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }
}
