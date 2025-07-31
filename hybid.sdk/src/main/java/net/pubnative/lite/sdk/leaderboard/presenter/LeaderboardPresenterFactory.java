// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.leaderboard.presenter;

import android.content.Context;

import net.pubnative.lite.sdk.banner.presenter.BannerPresenterFactory;
import net.pubnative.lite.sdk.models.IntegrationType;

/**
 * @deprecated This presenter factory is only kept for backwards compatibility
 * <p> Use {@link BannerPresenterFactory} instead.</p>
 */
@Deprecated
public class LeaderboardPresenterFactory extends BannerPresenterFactory {
    public LeaderboardPresenterFactory(Context context) {
        super(context, IntegrationType.STANDALONE);
    }
}
