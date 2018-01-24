package net.pubnative.tarantula.sdk.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by erosgarciaponte on 24.01.18.
 */
@RunWith(RobolectricTestRunner.class)
public class TarantulaBitmapDownloaderTest {
    @Test
    public void download_withNullListener_pass() {
        TarantulaBitmapDownloader downloader = new TarantulaBitmapDownloader();
        downloader.download("valid_string", null);
    }

    @Test
    public void download_withEmptyUrl_callOnDownloadFailed() {
        String emptyUrl = "";
        TarantulaBitmapDownloader downloader = new TarantulaBitmapDownloader();
        TarantulaBitmapDownloader.DownloadListener listener = spy(TarantulaBitmapDownloader.DownloadListener.class);
        downloader.download(emptyUrl, listener);

        verify(listener).onDownloadFailed(eq(emptyUrl), any(Exception.class));
    }

    @Test
    public void download_withWrongUrl_callOnDownloadFailed() {
        String wrongUrl = "should_fail";
        TarantulaBitmapDownloader downloader = new TarantulaBitmapDownloader();
        TarantulaBitmapDownloader.DownloadListener listener = spy(TarantulaBitmapDownloader.DownloadListener.class);
        downloader.download(wrongUrl, listener);

        verify(listener).onDownloadFailed(eq(wrongUrl), any(Exception.class));
    }

    @Test
    public void download_withLocalFile_callOnDownloadFinish() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test_image_file.jpg");

        String url = Uri.fromFile(file).toString();
        TarantulaBitmapDownloader downloader = new TarantulaBitmapDownloader();
        TarantulaBitmapDownloader.DownloadListener listener = spy(TarantulaBitmapDownloader.DownloadListener.class);
        downloader.download(url, listener);

        CountDownLatch latch = new CountDownLatch(1);
        try {
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();

        verify(listener).onDownloadFinish(eq(url), any(Bitmap.class));
    }
}
