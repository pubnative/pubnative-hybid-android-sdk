package net.pubnative.lite.sdk.tracking;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Random;

/**
 * Created by erosgarciaponte on 08.02.18.
 */

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultExceptionHandler;

    private static final String TAG = "UNHANDLED_EXCEPTION";

    public DefaultExceptionHandler(Thread.UncaughtExceptionHandler pDefaultExceptionHandler) {
        defaultExceptionHandler = pDefaultExceptionHandler;
    }

    public void uncaughtException(Thread t, Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        try {
            // Random number to avoid duplicate files
            Random generator = new Random();
            int random = generator.nextInt(99999);
            // Embed version in stacktrace filename
            String filename = ExceptionConfig.APP_VERSION + "-" + Integer.toString(random);
            Log.d(TAG, "Writing unhandled exception to: " + ExceptionConfig.FILES_PATH + "/" + filename + ".stacktrace");
            // Write the stacktrace to disk
            BufferedWriter bos = new BufferedWriter(new FileWriter(ExceptionConfig.FILES_PATH + "/" + filename + ".stacktrace"));
            bos.write(ExceptionConfig.ANDROID_VERSION + "\n");
            bos.write(ExceptionConfig.PHONE_MODEL + "\n");
            bos.write(result.toString());
            bos.flush();
            // Close up everything
            bos.close();
        } catch (Exception ebos) {
            // Nothing much we can do about this - the game is over
            ebos.printStackTrace();
        }
        Log.d(TAG, result.toString());
        //call original handler
        defaultExceptionHandler.uncaughtException(t, e);
    }
}
