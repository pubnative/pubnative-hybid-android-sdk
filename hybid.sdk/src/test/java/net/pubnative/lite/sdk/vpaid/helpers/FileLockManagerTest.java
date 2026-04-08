// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.helpers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class FileLockManagerTest {

    private FileLockManager lockManager;

    @Before
    public void setUp() throws Exception {
        lockManager = FileLockManager.getInstance();
        clearAllLocks();
    }

    @After
    public void tearDown() throws Exception {
        clearAllLocks();
    }

    private void clearAllLocks() throws Exception {
        Field field = FileLockManager.class.getDeclaredField("mFileReferences");
        field.setAccessible(true);
        ((Map<?, ?>) field.get(lockManager)).clear();
    }

    @Test
    public void getInstance_returnsSameInstance() {
        FileLockManager instance1 = FileLockManager.getInstance();
        FileLockManager instance2 = FileLockManager.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertEquals(instance1, instance2);
    }

    @Test
    public void acquire_withValidPath_locksFile() {
        String filePath = "/path/to/file.mp4";

        lockManager.acquire(filePath);

        assertTrue(lockManager.isLocked(filePath));
    }

    @Test
    public void acquire_withNullPath_doesNothing() {
        lockManager.acquire(null);
        assertFalse(lockManager.isLocked(null));
    }

    @Test
    public void release_afterOneAcquire_unlocksFile() {
        String filePath = "/path/to/file.mp4";

        lockManager.acquire(filePath);
        lockManager.release(filePath);

        assertFalse(lockManager.isLocked(filePath));
    }

    @Test
    public void release_afterMultipleAcquires_decrementsReferenceCount() {
        String filePath = "/path/to/file.mp4";

        lockManager.acquire(filePath);
        lockManager.acquire(filePath);
        lockManager.acquire(filePath);

        lockManager.release(filePath);
        assertTrue(lockManager.isLocked(filePath));

        lockManager.release(filePath);
        assertTrue(lockManager.isLocked(filePath));

        lockManager.release(filePath);
        assertFalse(lockManager.isLocked(filePath));
    }

    @Test
    public void release_withNullPath_doesNothing() {
        lockManager.release(null);
    }
    @Test
    public void forceRelease_withLockedFile_unlocksImmediately() {
        String filePath = "/path/to/file.mp4";

        lockManager.acquire(filePath);
        lockManager.acquire(filePath);
        lockManager.acquire(filePath);

        lockManager.forceRelease(filePath);

        assertFalse(lockManager.isLocked(filePath));
    }

    @Test
    public void forceRelease_withNullPath_doesNothing() {
        lockManager.forceRelease(null);
    }

    @Test
    public void multipleConcurrentFiles_managedIndependently() {
        String file1 = "/path/to/file1.mp4";
        String file2 = "/path/to/file2.mp4";
        String file3 = "/path/to/file3.mp4";

        lockManager.acquire(file1);
        lockManager.acquire(file2);
        lockManager.acquire(file2);
        lockManager.acquire(file3);

        assertTrue(lockManager.isLocked(file1));
        assertTrue(lockManager.isLocked(file2));
        assertTrue(lockManager.isLocked(file3));

        lockManager.release(file1);
        assertFalse(lockManager.isLocked(file1));
        assertTrue(lockManager.isLocked(file2));
        assertTrue(lockManager.isLocked(file3));

        lockManager.release(file2);
        assertTrue(lockManager.isLocked(file2));

        lockManager.release(file2);
        assertFalse(lockManager.isLocked(file2));
        assertTrue(lockManager.isLocked(file3));

        lockManager.release(file3);
        assertFalse(lockManager.isLocked(file3));
    }

    @Test
    public void threadSafety_multipleAcquires_maintainsCorrectCount() throws Exception {
        String filePath = "/path/to/file.mp4";
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> lockManager.acquire(filePath));
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertTrue(lockManager.isLocked(filePath));

        for (int i = 0; i < threadCount; i++) {
            lockManager.release(filePath);
        }

        assertFalse(lockManager.isLocked(filePath));
    }
}
