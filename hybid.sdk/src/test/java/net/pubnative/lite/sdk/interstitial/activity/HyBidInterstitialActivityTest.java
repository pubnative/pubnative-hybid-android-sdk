// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Insets;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.view.WindowInsetsCompat;

import net.pubnative.lite.sdk.interstitial.viewModel.InterstitialViewModel;
import net.pubnative.lite.sdk.vpaid.HyBidActivityInteractor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class HyBidInterstitialActivityTest {
    protected HyBidActivityInteractor mInteractor;
    private TestInterstitialActivity activity;

    private HyBidInterstitialActivity subject;
    private long broadcastIdentifier;
    private ActivityController<TestInterstitialActivity> activityController;

    @Mock
    private InterstitialViewModel mockViewModel;


    private static class TestInterstitialActivity extends HyBidInterstitialActivity {
        public void setMockViewModel(InterstitialViewModel viewModel) {
            this.mViewModel = viewModel;
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
        }

        @Override
        public void finish() {
            // Override to prevent actual finish() behavior in tests
        }

        @Override
        public boolean isSuperBackPressedCalled() {
            return super.isSuperBackPressedCalled();
        }
    }

    @Before
    public void setup() {
        broadcastIdentifier = 2222;
        MockitoAnnotations.openMocks(this);
        activityController = Robolectric.buildActivity(TestInterstitialActivity.class);
        activity = activityController.get();
    }

    @Test
    public void onCreate_shouldCreateView() {
        Context context = Robolectric.buildActivity(Activity.class).create().get();
        Intent intent = new Intent(context, TestInterstitialActivity.class);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_BROADCAST_ID, broadcastIdentifier);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID, "2");

        subject = Robolectric.buildActivity(TestInterstitialActivity.class, intent)
                .create().get();
        View adView = getContentView(subject);

        Assert.assertNotNull(adView);
    }

    @Test
    public void onDestroy_shouldCleanUpContentView() {
        Context context = Robolectric.buildActivity(Activity.class).create().get();
        Intent intent = new Intent(context, TestInterstitialActivity.class);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_BROADCAST_ID, broadcastIdentifier);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID, "2");

        subject = Robolectric.buildActivity(TestInterstitialActivity.class, intent)
                .create().destroy().get();

        Assert.assertEquals(0, getContentView(subject).getChildCount());
    }

    @Test
    public void getBroadcastIdentifier_shouldReturnBroadcastIdFromIntent() {
        Context context = Robolectric.buildActivity(Activity.class).create().get();
        Intent intent = new Intent(context, TestInterstitialActivity.class);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_BROADCAST_ID, broadcastIdentifier);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID, "2");

        subject = Robolectric.buildActivity(TestInterstitialActivity.class, intent)
                .create().get();
//        Assert.assertEquals(2222L, subject.getBroadcastSender().getBroadcastId());
    }

    @Test
    public void applyWindowInsets_shouldSetBottomPadding() {
        Context context = Robolectric.buildActivity(Activity.class).create().get();
        Intent intent = new Intent(context, TestInterstitialActivity.class);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_BROADCAST_ID, broadcastIdentifier);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID, "2");

        subject = Robolectric.buildActivity(TestInterstitialActivity.class, intent)
                .create().get();

        FrameLayout container = getContentView(subject);

        int bottomInset = 42;
        WindowInsetsCompat insetsCompat = new WindowInsetsCompat.Builder()
                .setInsets(WindowInsetsCompat.Type.systemBars(),
                        androidx.core.graphics.Insets.wrap(Insets.of(0, 0, 0, bottomInset)))
                .build();
        android.view.WindowInsets insets = new android.view.WindowInsets(insetsCompat.toWindowInsets());
        container.dispatchApplyWindowInsets(insets);

        Assert.assertEquals(bottomInset, container.getPaddingBottom());
        Assert.assertEquals(0, container.getPaddingTop());
        Assert.assertEquals(0, container.getPaddingLeft());
        Assert.assertEquals(0, container.getPaddingRight());
    }

    @Test
    public void onBackPressed_whenViewModelIsNull_shouldNotCallSuperOnBackPressed() {
        activityController.create().start().resume().visible();
        activity.setMockViewModel(null);
        activity.onBackPressed();
        assertFalse(activity.isSuperBackPressedCalled());
        assertFalse(activity.isFinishing());
    }

    @Test
    public void onBackPressed_whenAdIsSkippable_shouldCallSuperOnBackPressed() {
        activityController.create();
        try {
            Field viewModelField = HyBidInterstitialActivity.class.getDeclaredField("mViewModel");
            viewModelField.setAccessible(true);
            InterstitialViewModel viewModel = (InterstitialViewModel) viewModelField.get(activity);
            Field isSkippableField = InterstitialViewModel.class.getDeclaredField("mIsSkippable");
            isSkippableField.setAccessible(true);
            isSkippableField.set(viewModel, true);
            activity.onBackPressed();
            assertTrue(activity.isSuperBackPressedCalled());
            isSkippableField.set(viewModel, false);
            viewModelField.setAccessible(false);
            isSkippableField.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException("Test failed due to reflection error", e);
        }
    }

    @Test
    public void onBackPressed_whenAdIsNotSkippable_shouldNotCallSuperOnBackPressed() {
        // Given
        activityController.create();
        try {
            // Use reflection to access and modify mViewModel
            Field viewModelField = HyBidInterstitialActivity.class.getDeclaredField("mViewModel");
            viewModelField.setAccessible(true);
            InterstitialViewModel viewModel = (InterstitialViewModel) viewModelField.get(activity);
            Field isSkippableField = InterstitialViewModel.class.getDeclaredField("mIsSkippable");
            isSkippableField.setAccessible(true);
            isSkippableField.set(viewModel, false);
            activity.onBackPressed();
            assertFalse(activity.isSuperBackPressedCalled());
            viewModelField.setAccessible(false);
            isSkippableField.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException("Test failed due to reflection error", e);
        }
    }

    @Test
    public void testAddWatermarkView() {
        Context context = Robolectric.buildActivity(Activity.class).create().get();
        Intent intent = new Intent(context, TestInterstitialActivity.class);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_BROADCAST_ID, broadcastIdentifier);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID, "2");

        HyBidInterstitialActivity subject = Robolectric.buildActivity(TestInterstitialActivity.class, intent)
                .create().get();

        ((TestInterstitialActivity) subject).setMockViewModel(mockViewModel);

        View mockWatermarkView = new View(subject);

        int childCountBefore = getContentView(subject).getChildCount();
        subject.addWatermarkView(mockWatermarkView);
        int childCountAfter = getContentView(subject).getChildCount();

        assertEquals(childCountBefore + 1, childCountAfter);
        verify(mockViewModel).addFriendlyObstruction(mockWatermarkView);
    }

    @Test
    public void addWatermarkView_calledTwice_doesNotDuplicateRegistration() {
        Intent intent = new Intent();
        intent.putExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID, "test_zone");
        intent.putExtra(HyBidInterstitialActivity.EXTRA_BROADCAST_ID, 1L);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_SKIP_OFFSET, 5);

        HyBidInterstitialActivity subject = Robolectric.buildActivity(TestInterstitialActivity.class, intent)
                .create().get();

        ((TestInterstitialActivity) subject).setMockViewModel(mockViewModel);

        View mockWatermarkView = new View(subject);

        // First call
        subject.addWatermarkView(mockWatermarkView);
        // Second call (should be ignored due to guard)
        subject.addWatermarkView(mockWatermarkView);

        // Verify addFriendlyObstruction called only once
        verify(mockViewModel, times(1)).addFriendlyObstruction(mockWatermarkView);
    }

    protected FrameLayout getContentView(HyBidInterstitialActivity subject) {
        return subject.getCloseableContainer();
    }
}
