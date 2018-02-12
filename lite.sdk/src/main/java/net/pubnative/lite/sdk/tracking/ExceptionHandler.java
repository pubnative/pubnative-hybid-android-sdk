package net.pubnative.lite.sdk.tracking;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by erosgarciaponte on 08.02.18.
 */

public class ExceptionHandler {
    public static String TAG = ExceptionHandler.class.getSimpleName();

    private static String[] stackTraceFileList = null;

    /**
     * Register handler for unhandled exceptions.
     * @param context
     */
    public static boolean register(Context context) {
        Log.i(TAG, "Registering default exceptions handler");
        // Get information about the Package
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi;
            // Version
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            ExceptionConfig.APP_VERSION = pi.versionName;
            // Package name
            ExceptionConfig.APP_PACKAGE = pi.packageName;
            // Files dir for storing the stack traces
            ExceptionConfig.FILES_PATH = context.getFilesDir().getAbsolutePath();
            // Device model
            ExceptionConfig.PHONE_MODEL = android.os.Build.MODEL;
            // Android version
            ExceptionConfig.ANDROID_VERSION = android.os.Build.VERSION.RELEASE;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "TRACE_VERSION: " + ExceptionConfig.TraceVersion);
        Log.d(TAG, "APP_VERSION: " + ExceptionConfig.APP_VERSION);
        Log.d(TAG, "APP_PACKAGE: " + ExceptionConfig.APP_PACKAGE);
        Log.d(TAG, "FILES_PATH: " + ExceptionConfig.FILES_PATH);
        Log.d(TAG, "URL: " + ExceptionConfig.URL);

        boolean stackTracesFound = false;
        // We'll return true if any stack traces were found
        if ( searchForStackTraces().length > 0 ) {
            stackTracesFound = true;
        }

        new Thread() {
            @Override
            public void run() {
                // First of all transmit any stack traces that may be lying around
                submitStackTraces();
                UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
                if (currentHandler != null) {
                    Log.d(TAG, "current handler class="+currentHandler.getClass().getName());
                }
                // don't register again if already registered
                if (!(currentHandler instanceof DefaultExceptionHandler)) {
                    // Register default exceptions handler
                    Thread.setDefaultUncaughtExceptionHandler(
                            new DefaultExceptionHandler(currentHandler));
                }
            }
        }.start();

        return stackTracesFound;
    }

    /**
     * Register handler for unhandled exceptions.
     * @param context
     * @param url
     */
    public static void register(Context context, String url) {
        Log.i(TAG, "Registering default exceptions handler: " + url);
        // Use custom URL
        ExceptionConfig.URL = url;
        // Call the default register method
        register(context);
    }


    /**
     * Search for stack trace files.
     * @return
     */
    private static String[] searchForStackTraces() {
        if ( stackTraceFileList != null ) {
            return stackTraceFileList;
        }
        File dir = new File(ExceptionConfig.FILES_PATH + "/");
        // Try to create the files folder if it doesn't exist
        dir.mkdir();
        // Filter for ".stacktrace" files
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".stacktrace");
            }
        };
        return (stackTraceFileList = dir.list(filter));
    }

    /**
     * Look into the files folder to see if there are any "*.stacktrace" files.
     * If any are present, submit them to the trace server.
     */
    public static void submitStackTraces() {
        HttpURLConnection urlConnection = null;
        try {
            Log.d(TAG, "Looking for exceptions in: " + ExceptionConfig.FILES_PATH);
            String[] list = searchForStackTraces();
            if ( list != null && list.length > 0 ) {
                Log.d(TAG, "Found "+list.length+" stacktrace(s)");
                for (int i=0; i < list.length; i++) {
                    String filePath = ExceptionConfig.FILES_PATH+"/"+list[i];
                    // Extract the version from the filename: "packagename-version-...."
                    String version = list[i].split("-")[0];
                    Log.d(TAG, "Stacktrace in file '"+filePath+"' belongs to version " + version);
                    // Read contents of stacktrace
                    StringBuilder contents = new StringBuilder();
                    BufferedReader input =  new BufferedReader(new FileReader(filePath));
                    String line = null;
                    String androidVersion = null;
                    String phoneModel = null;
                    while (( line = input.readLine()) != null){
                        if (androidVersion == null) {
                            androidVersion = line;
                            continue;
                        }
                        else if (phoneModel == null) {
                            phoneModel = line;
                            continue;
                        }
                        contents.append(line);
                        contents.append(System.getProperty("line.separator"));
                    }
                    input.close();
                    String stacktrace;
                    stacktrace = contents.toString();
                    Log.d(TAG, "Transmitting stack trace: " + stacktrace);

                    URL url = new URL(ExceptionConfig.URL);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setReadTimeout(8000);
                    urlConnection.setConnectTimeout(8000);

                    //urlConnection.setRequestProperty("Content-Type", "application/json");
                    //urlConnection.setRequestProperty("Bugsnag-Api-Key", "API_KEY");
                    //urlConnection.setRequestProperty("Bugsnag-Payload-Version", "4");
                    urlConnection.setRequestMethod("POST");

                    //JSONObject postData = buildPostData();
                    StringBuilder postData = new StringBuilder();
                    postData.append("package_name").append("=").append(ExceptionConfig.APP_PACKAGE).append("&");
                    postData.append("package_version").append("=").append(version).append("&");
                    postData.append("phone_model").append("=").append(phoneModel).append("&");
                    postData.append("android_version").append("=").append(androidVersion).append("&");
                    postData.append("stacktrace").append("=").append(stacktrace);

                    urlConnection.connect();

                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                    writer.close();

                    int statusCode = urlConnection.getResponseCode();

                    if (statusCode == 200) {
                        InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        String response = convertInputStreamToString(inputStream);
                    } else {
                        Log.e(TAG, "Could not report crash");
                    }
                }
            }
        } catch( Exception e ) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                String[] list = searchForStackTraces();
                for ( int i = 0; i < list.length; i ++ ) {
                    File file = new File(ExceptionConfig.FILES_PATH+"/"+list[i]);
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String convertInputStreamToString(InputStream stream) {
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder response = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

        } catch (IOException exception) {
            Log.e(TAG, exception.getMessage());
        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage());
        }

        return response.toString();
    }

    private static JSONObject buildPostData() {
        JSONObject postData = new JSONObject();

        try {
            postData.put("apiKey", "API_KEY");
            postData.put("notifier", buildNotifierInfo());
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }

        return postData;
    }

    private static JSONObject buildNotifierInfo() throws JSONException {
        JSONObject notifier = new JSONObject();
            notifier.put("name", "PN Lite Bugsnag Notifier");
            notifier.put("version", ExceptionConfig.TraceVersion);
        return notifier;
    }

    private static JSONArray buildExceptionEvent() throws JSONException {
        JSONArray events = new JSONArray();

        return events;
    }
}
