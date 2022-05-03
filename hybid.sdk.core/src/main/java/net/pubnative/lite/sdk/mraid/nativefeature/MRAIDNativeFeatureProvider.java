// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.mraid.nativefeature;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import net.pubnative.lite.sdk.mraid.internal.MRAIDLog;
import net.pubnative.lite.sdk.mraid.internal.MRAIDNativeFeatureManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class MRAIDNativeFeatureProvider {
    private static final String TAG = MRAIDNativeFeatureProvider.class.getSimpleName();

    private final Context context;
    private final MRAIDNativeFeatureManager nativeFeatureManager;

    public MRAIDNativeFeatureProvider(Context context, MRAIDNativeFeatureManager nativeFeatureManager) {
        this.context = context;
        this.nativeFeatureManager = nativeFeatureManager;
    }

    public final void callTel(String url) {
        if (nativeFeatureManager.isTelSupported()) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            context.startActivity(intent);
        }
    }

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String EVENT_LOCATION = "eventLocation";
    public static final String EVENT_COLOR = "eventColor";
    public static final String EVENT_COLOR_KEY = "eventColor_index";
    public static final String DISPLAY_COLOR = "displayColor";
    public static final String STATUS = "eventStatus";
    public static final String ACTION_HANDLE_CUSTOM_EVENT = "android.provider.calendar.action.HANDLE_CUSTOM_EVENT";
    public static final String EXTRA_CUSTOM_APP_URI = "customAppUri";
    public static final String EXTRA_EVENT_BEGIN_TIME = "beginTime";
    public static final String EXTRA_EVENT_END_TIME = "endTime";
    public static final String EXTRA_EVENT_ALL_DAY = "allDay";
    public static final String AUTHORITY = "com.android.calendar";

    public void createCalendarEvent(String eventJSON) {
        if (!nativeFeatureManager.isCalendarSupported()) {
            return;
        }
        try {
            // Need to fix some of the encoded string from JS
            eventJSON = eventJSON.replace("\\", "").replace("\"{", "{").replace("}\"", "}");
            JSONObject jsonObject = new JSONObject(eventJSON);

            String description = jsonObject.optString("description", "Untitled");
            String location = jsonObject.optString("location", "unknown");
            String summary = jsonObject.optString("summary");

            /*
             * NOTE: The Java SimpleDateFormat class will not work as is with the W3C spec for
             * calendar entries. The problem is that the W3C spec has time zones (UTC offsets)
             * containing a colon like this:
             *   "2014-12-21T12:34-05:00"
             * The SimpleDateFormat parser will choke on the colon. It wants something like this:
             *   "2014-12-21T12:34-0500"
             *
             * Also, the W3C spec indicates that seconds are optional, so we have to use two patterns
             * to be able to parse both this:
             *   "2014-12-21T12:34-0500"
             * and this:
             *   "2014-12-21T12:34:56-0500"
             */

            String[] patterns = {
                    "yyyy-MM-dd'T'HH:mmZ",
                    "yyyy-MM-dd'T'HH:mm:ssZ",
            };

            String[] dateStrings = new String[2];
            dateStrings[0] = jsonObject.getString("start");
            dateStrings[1] = jsonObject.optString("end");

            long startTime = 0;
            long endTime = 0;

            for (int i = 0; i < dateStrings.length; i++) {
                if (TextUtils.isEmpty(dateStrings[i])) {
                    continue;
                }
                // remove the colon in the timezone
                dateStrings[i] = dateStrings[i].replaceAll("([+-]\\d\\d):(\\d\\d)$", "$1$2");
                for (String pattern : patterns) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
                        Date date = sdf.parse(dateStrings[i]);
                        if (date != null) {
                            if (i == 0) {
                                startTime = date.getTime();
                            } else {
                                endTime = date.getTime();
                            }
                        }

                        break;
                    } catch (ParseException ignored) {
                    }
                }
            }

            Intent intent = new Intent(Intent.ACTION_INSERT).setType("vnd.android.cursor.item/event");
            intent.putExtra(TITLE, description);
            intent.putExtra(DESCRIPTION, summary);
            intent.putExtra(EVENT_LOCATION, location);

            if (startTime > 0) {
                intent.putExtra(EXTRA_EVENT_BEGIN_TIME, startTime);
            }

            if (endTime > 0) {
                intent.putExtra(EXTRA_EVENT_END_TIME, endTime);
            }

            context.startActivity(intent);
        } catch (JSONException e) {
            MRAIDLog.e(TAG, "Error parsing JSON: " + e.getLocalizedMessage());
        }
    }

    public void playVideo(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "video/*");
        context.startActivity(intent);
    }

    public void openBrowser(String url) {
        if (url.startsWith("market:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } else if (url.startsWith("http:") || url.startsWith("https:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }

    public void storePicture(final String url) {
        if (nativeFeatureManager.isStorePictureSupported()) {
            // Spawn a new thread to download and save the image
            new Thread(() -> {
                try {
                    storePictureInGallery(url);
                } catch (Exception e) {
                    MRAIDLog.e(TAG, e.getLocalizedMessage());
                }
            }).start();
        }
    }

    public void sendSms(String url) {
        if (nativeFeatureManager.isSmsSupported()) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            context.startActivity(intent);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void storePictureInGallery(String url) {
        // Setting up file to write the image to.
        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        String s = getAlbumDir() + "/img" + gmtDateFormat.format(new Date()) + ".png";
        MRAIDLog.i(TAG, "Saving image into: " + s);
        File f = new File(s);
        // Open InputStream to download the image.
        InputStream is;
        try {
            is = new URL(url).openStream();
            // Set up OutputStream to write data into image file.
            OutputStream os = new FileOutputStream(f);
            copyStream(is, os);
            MediaScannerConnection.scanFile(context,
                    new String[]{f.getAbsolutePath()}, null,
                    (path, uri) -> MRAIDLog.d("File saves successfully to " + path));
            MRAIDLog.i(TAG, "Saved image successfully");
        } catch (MalformedURLException e) {
            MRAIDLog.e(TAG, "Not able to save image due to invalid URL: " + e.getLocalizedMessage());
        } catch (IOException e) {
            MRAIDLog.e(TAG, "Unable to save image: " + e.getLocalizedMessage());
        }
    }

    private void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            MRAIDLog.i(TAG, "Error saving picture: " + ex.getLocalizedMessage());
        }
    }

    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Image");
            if (!storageDir.mkdirs() && !storageDir.exists()) {
                MRAIDLog.i(TAG, "Failed to create camera directory");
                return null;
            }
        } else {
            MRAIDLog.i(TAG, "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }
}
