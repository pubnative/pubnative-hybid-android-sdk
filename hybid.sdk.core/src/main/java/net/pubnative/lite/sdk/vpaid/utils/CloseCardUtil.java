package net.pubnative.lite.sdk.vpaid.utils;

import android.graphics.Bitmap;

import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdData;
import net.pubnative.lite.sdk.utils.PNBitmapDownloader;
import net.pubnative.lite.sdk.vpaid.models.CloseCardData;

public class CloseCardUtil {

    public CloseCardUtil() {}

    public void fetchCloseCardData(Ad ad, CloseCardData closeCardData){

        // Get title
        AdData titleData = ad.getAsset(APIAsset.TITLE);
        if (titleData != null) {
            closeCardData.setTitle(titleData.getText());
        }

        // Get rating
        AdData ratingData = ad.getAsset(APIAsset.RATING);
        if (ratingData != null) {
            closeCardData.setRating(ratingData.getNumber());
        }

        // Get votes count
        AdData votesData = ad.getAsset(APIAsset.VOTES);
        if (votesData != null) {;
            closeCardData.setVotes(votesData.getNumber().intValue());
        }

        // Get Icon
        AdData iconData = ad.getAsset(APIAsset.ICON);
        if (iconData != null) {
            new PNBitmapDownloader().download(iconData.getURL(), new PNBitmapDownloader.DownloadListener() {
                @Override
                public void onDownloadFinish(String url, Bitmap bitmap) {
                    closeCardData.setIcon(bitmap);
                }
                @Override
                public void onDownloadFailed(String url, Exception exception) {

                }
            });
        }

        // Get Banner
        AdData bannerData = ad.getAsset(APIAsset.BANNER);
        if (bannerData != null) {
            closeCardData.setBanner(bannerData.getURL());
            new PNBitmapDownloader().download(bannerData.getURL(), new PNBitmapDownloader.DownloadListener() {
                @Override
                public void onDownloadFinish(String url, Bitmap bitmap) {
                    closeCardData.setBannerImage(bitmap);
                }
                @Override
                public void onDownloadFailed(String url, Exception exception) {

                }
            });
        }
    }
}
