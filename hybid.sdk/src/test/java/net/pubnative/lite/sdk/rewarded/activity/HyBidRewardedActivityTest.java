package net.pubnative.lite.sdk.rewarded.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Insets;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.view.WindowInsetsCompat;

import com.verve.atom.sdk.database.DatabaseManager;

import net.pubnative.lite.sdk.rewarded.viewModel.MraidRewardedViewModel;
import net.pubnative.lite.sdk.rewarded.viewModel.RewardedViewModel;
import net.pubnative.lite.sdk.vpaid.HyBidActivityInteractor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class HyBidRewardedActivityTest {
    private HyBidRewardedActivity subject;
    private long broadcastIdentifier;
    @Mock
    private RewardedViewModel mockViewModel;

    @Mock
    private HyBidActivityInteractor mockInteractor;

    private TestHyBidRewardedActivity activity;
    private ActivityController<TestHyBidRewardedActivity> activityController;

    public static class TestHyBidRewardedActivity extends HyBidRewardedActivity {

        public void setMockViewModel(RewardedViewModel viewModel) {
            this.mViewModel = viewModel;
        }

        public void setMockInteractor(HyBidActivityInteractor interactor) {
            this.mInteractor = interactor;
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
        activityController = Robolectric.buildActivity(TestHyBidRewardedActivity.class);
        activity = activityController.get();
        activity.setMockViewModel(mockViewModel);
        activity.setMockInteractor(mockInteractor);

    }

    @Test
    public void applyWindowInsets_shouldSetBottomPadding() {
        activityController.create().start().resume().visible();
        Context context = Robolectric.buildActivity(Activity.class).create().get();
        Intent intent = new Intent(context, TestHyBidRewardedActivity.class);
        intent.putExtra(HyBidRewardedActivity.EXTRA_BROADCAST_ID, broadcastIdentifier);
        intent.putExtra(HyBidRewardedActivity.EXTRA_ZONE_ID, "2");

        subject = Robolectric.buildActivity(TestHyBidRewardedActivity.class, intent)
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
            Field viewModelField = HyBidRewardedActivity.class.getDeclaredField("mViewModel");
            viewModelField.setAccessible(true);
            RewardedViewModel viewModel = (RewardedViewModel) viewModelField.get(activity);
            Field isSkippableField = RewardedViewModel.class.getDeclaredField("mIsSkippable");
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
            Field viewModelField = HyBidRewardedActivity.class.getDeclaredField("mViewModel");
            viewModelField.setAccessible(true);
            RewardedViewModel viewModel = (RewardedViewModel) viewModelField.get(activity);
            Field isSkippableField = RewardedViewModel.class.getDeclaredField("mIsSkippable");
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
        activityController.create();
        activity.setMockViewModel(mockViewModel);
        activityController.start().resume().visible();

        View mockWatermarkView = new View(activity);

        int childCountBefore = activity.getCloseableContainer().getChildCount();
        activity.addWatermarkView(mockWatermarkView);
        int childCountAfter = activity.getCloseableContainer().getChildCount();

        assertEquals(childCountBefore + 1, childCountAfter);
        verify(mockViewModel).addFriendlyObstruction(mockWatermarkView);
    }

    @Test
    public void addWatermarkView_calledTwice_doesNotDuplicateRegistration() {
        Intent intent = new Intent();
        intent.putExtra(HyBidRewardedActivity.EXTRA_ZONE_ID, "test_zone");
        intent.putExtra(HyBidRewardedActivity.EXTRA_BROADCAST_ID, 1L);
        intent.putExtra(HyBidRewardedActivity.EXTRA_SKIP_OFFSET, 5);

        HyBidRewardedActivity activity = Robolectric.buildActivity(TestHyBidRewardedActivity.class, intent)
                .create().get();

        ((TestHyBidRewardedActivity) activity).setMockViewModel(mockViewModel);

        View mockWatermarkView = new View(activity);

        // First call
        activity.addWatermarkView(mockWatermarkView);
        // Second call (should be ignored due to guard)
        activity.addWatermarkView(mockWatermarkView);

        // Verify addFriendlyObstruction called only once
        verify(mockViewModel, times(1)).addFriendlyObstruction(mockWatermarkView);
    }

    protected FrameLayout getContentView(HyBidRewardedActivity subject) {
        return subject.getCloseableContainer();
    }
}
