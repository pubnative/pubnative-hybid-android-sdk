// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.utils;

import android.content.Context;
import net.pubnative.lite.sdk.vpaid.VpaidConstants;
import net.pubnative.lite.sdk.vpaid.helpers.FileLockManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@RunWith(RobolectricTestRunner.class)
public class FileUtilsTest {

    @Mock
    private Context mockContext;
    @Mock
    private File mockParentDir;
    @Mock
    private File mockFile1;
    @Mock
    private File mockFile2;
    @Mock
    private File mockFileExpired;
    @Mock
    private File mockFileEmpty;

    private AutoCloseable closeable;
    private long currentTime;

    @Before
    public void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        Field field = FileUtils.class.getDeclaredField("cachedParentDir");
        field.setAccessible(true);
        field.set(null, null);

        when(mockContext.getExternalFilesDir(VpaidConstants.FILE_FOLDER)).thenReturn(mockParentDir);
        currentTime = System.currentTimeMillis();
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
        clearAllLocks();
    }

    private void clearAllLocks() throws Exception {
        Field field = FileLockManager.class.getDeclaredField("mFileReferences");
        field.setAccessible(true);
        ((Map<?, ?>) field.get(FileLockManager.getInstance())).clear();
    }

    @Test
    public void testObtainHashName() {
        String url = "https://example.com/video.mp4";
        String expectedHash = "1154027261";
        String actualHash = FileUtils.obtainHashName(url);
        assertEquals(expectedHash, actualHash);
    }

    @Test
    public void getParentDir_whenCacheIsNull_returnsDirFromContext() {
        File result = FileUtils.getParentDir(mockContext);
        assertEquals(mockParentDir, result);
    }

    @Test
    public void getParentDir_whenCacheIsNotNull_returnsCachedDir() {
        // First call to set the cache
        FileUtils.getParentDir(mockContext);

        // Second call should return cached value, context should not be used again here
        File result = FileUtils.getParentDir(null); // Pass null to ensure cache is used
        assertEquals(mockParentDir, result);
    }

    @Test
    public void getParentDir_whenContextIsNullAndCacheIsNull_returnsNull() {
        when(mockContext.getExternalFilesDir(VpaidConstants.FILE_FOLDER)).thenReturn(null); // Ensure context returns null for this specific test case if called
        File result = FileUtils.getParentDir(null);
        assertNull(result);
    }

    @Test
    public void getParentDir_whenExternalFilesDirReturnsNull_returnsNull() {
        when(mockContext.getExternalFilesDir(VpaidConstants.FILE_FOLDER)).thenReturn(null);
        File result = FileUtils.getParentDir(mockContext);
        assertNull(result);
    }

    @Test
    public void clearCache_whenParentDirIsNull_doesNothing() {
        when(mockContext.getExternalFilesDir(VpaidConstants.FILE_FOLDER)).thenReturn(null);
        FileUtils.clearCache(mockContext);
        // No exceptions and no further interactions expected
    }

    @Test
    public void clearCache_whenParentDirHasNoFiles_doesNothing() {
        when(mockParentDir.listFiles()).thenReturn(new File[]{});
        FileUtils.clearCache(mockContext);
        verify(mockParentDir, times(1)).listFiles();
    }

    @Test
    public void clearCache_whenParentDirHasFiles_deletesFiles() {
        when(mockFile1.isDirectory()).thenReturn(false);
        when(mockFile2.isDirectory()).thenReturn(false);
        File[] files = {mockFile1, mockFile2};
        when(mockParentDir.listFiles()).thenReturn(files);

        FileUtils.clearCache(mockContext);

        verify(mockFile1, times(1)).delete();
        verify(mockFile2, times(1)).delete();
    }

    @Test
    public void clearCache_whenParentDirHasDirectory_doesNotDeleteDirectory() {
        when(mockFile1.isDirectory()).thenReturn(true);
        File[] files = {mockFile1};
        when(mockParentDir.listFiles()).thenReturn(files);

        FileUtils.clearCache(mockContext);

        verify(mockFile1, never()).delete();
    }

    @Test
    public void deleteExpiredFiles_whenParentDirIsNull_doesNothing() {
        when(mockContext.getExternalFilesDir(VpaidConstants.FILE_FOLDER)).thenReturn(null);
        FileUtils.deleteExpiredFiles(mockContext);
        // No exceptions expected
    }

    @Test
    public void deleteExpiredFiles_whenParentDirHasNoFiles_doesNothing() {
        when(mockParentDir.listFiles()).thenReturn(new File[]{});
        FileUtils.deleteExpiredFiles(mockContext);
        verify(mockParentDir, times(1)).listFiles();
    }

    @Test
    public void deleteExpiredFiles_deletesExpiredFile() {
        when(mockFileExpired.isDirectory()).thenReturn(false);
        when(mockFileExpired.lastModified()).thenReturn(currentTime - VpaidConstants.CACHED_VIDEO_LIFE_TIME - 1000); // Older than expiry
        when(mockFileExpired.length()).thenReturn(1024L); // Non-empty

        File[] files = {mockFileExpired};
        when(mockParentDir.listFiles()).thenReturn(files);

        FileUtils.deleteExpiredFiles(mockContext);

        verify(mockFileExpired, times(1)).delete();
    }

    @Test
    public void deleteExpiredFiles_deletesEmptyFile() {
        when(mockFileEmpty.isDirectory()).thenReturn(false);
        when(mockFileEmpty.lastModified()).thenReturn(currentTime - 1000); // Not expired by time
        when(mockFileEmpty.length()).thenReturn(0L); // Empty file

        File[] files = {mockFileEmpty};
        when(mockParentDir.listFiles()).thenReturn(files);

        FileUtils.deleteExpiredFiles(mockContext);

        verify(mockFileEmpty, times(1)).delete();
    }

    @Test
    public void deleteExpiredFiles_doesNotDeleteValidFile() {
        when(mockFile1.isDirectory()).thenReturn(false);
        when(mockFile1.lastModified()).thenReturn(currentTime - 1000); // Not expired
        when(mockFile1.length()).thenReturn(1024L); // Non-empty

        File[] files = {mockFile1};
        when(mockParentDir.listFiles()).thenReturn(files);

        FileUtils.deleteExpiredFiles(mockContext);

        verify(mockFile1, never()).delete();
    }

    @Test
    public void deleteExpiredFiles_doesNotDeleteDirectory() {
        when(mockFile1.isDirectory()).thenReturn(true);
        File[] files = {mockFile1};
        when(mockParentDir.listFiles()).thenReturn(files);

        FileUtils.deleteExpiredFiles(mockContext);

        verify(mockFile1, never()).delete();
    }

    @Test
    public void deleteExpiredFiles_lockedExpiredFile_forceReleasedAndDeleted() {
        when(mockFileExpired.isDirectory()).thenReturn(false);
        when(mockFileExpired.lastModified()).thenReturn(currentTime - VpaidConstants.CACHED_VIDEO_LIFE_TIME - 1000); // Expired
        when(mockFileExpired.length()).thenReturn(1024L); // Non-empty
        when(mockParentDir.listFiles()).thenReturn(new File[]{mockFileExpired});
        when(mockFileExpired.getAbsolutePath()).thenReturn("/mock/expired_locked_file");

        FileLockManager lockManager = FileLockManager.getInstance();
        lockManager.acquire(mockFileExpired.getAbsolutePath());
        FileUtils.deleteExpiredFiles(mockContext);
        // Should force release and delete
        verify(mockFileExpired, times(1)).delete();
        assertFalse(lockManager.isLocked("/mock/expired_locked_file"));
    }

    @Test
    public void deleteExpiredFiles_lockedNonExpiredFile_notDeleted() {
        when(mockFile1.isDirectory()).thenReturn(false);
        when(mockFile1.lastModified()).thenReturn(currentTime - 1000); // Not expired
        when(mockFile1.length()).thenReturn(1024L); // Non-empty
        when(mockParentDir.listFiles()).thenReturn(new File[]{mockFile1});

        when(mockFile1.getAbsolutePath()).thenReturn("/mock/locked_valid_file");
        FileLockManager lockManager = FileLockManager.getInstance();
        lockManager.acquire(mockFile1.getAbsolutePath());
        FileUtils.deleteExpiredFiles(mockContext);
        // Should not delete
        verify(mockFile1, never()).delete();
        assertTrue(lockManager.isLocked("/mock/locked_valid_file"));
        lockManager.release("/mock/locked_valid_file");
    }

    @Test
    public void deleteExpiredFiles_emptyLockedFile_notDeleted() {
        when(mockFileEmpty.isDirectory()).thenReturn(false);
        when(mockFileEmpty.lastModified()).thenReturn(currentTime - 1000); // Not expired
        when(mockFileEmpty.length()).thenReturn(0L); // Empty file
        when(mockParentDir.listFiles()).thenReturn(new File[]{mockFileEmpty});

        when(mockFileEmpty.getAbsolutePath()).thenReturn("/mock/empty_locked_file");
        FileLockManager lockManager = FileLockManager.getInstance();
        lockManager.acquire(mockFileEmpty.getAbsolutePath());
        FileUtils.deleteExpiredFiles(mockContext);
        // Should not delete
        verify(mockFileEmpty, never()).delete();
        assertTrue(lockManager.isLocked("/mock/empty_locked_file"));
        lockManager.release("/mock/empty_locked_file");
    }

    @Test
    public void deleteExpiredFiles_emptyUnlockedFile_deleted() {
        when(mockFileEmpty.isDirectory()).thenReturn(false);
        when(mockFileEmpty.lastModified()).thenReturn(currentTime - 1000); // Not expired
        when(mockFileEmpty.length()).thenReturn(0L); // Empty file
        when(mockParentDir.listFiles()).thenReturn(new File[]{mockFileEmpty});

        when(mockFileEmpty.getAbsolutePath()).thenReturn("/mock/empty_unlocked_file");
        FileUtils.deleteExpiredFiles(mockContext);
        // Should delete
        verify(mockFileEmpty, times(1)).delete();
    }

    @Test
    public void deleteExpiredFiles_unlockedExpiredFile_deleted() {
        when(mockFileExpired.isDirectory()).thenReturn(false);
        when(mockFileExpired.lastModified()).thenReturn(currentTime - VpaidConstants.CACHED_VIDEO_LIFE_TIME - 1000); // Expired
        when(mockFileExpired.length()).thenReturn(1024L); // Non-empty
        when(mockParentDir.listFiles()).thenReturn(new File[]{mockFileExpired});

        when(mockFileExpired.getAbsolutePath()).thenReturn("/mock/expired_unlocked_file");
        FileUtils.deleteExpiredFiles(mockContext);
        // Should delete
        verify(mockFileExpired, times(1)).delete();
    }
}
