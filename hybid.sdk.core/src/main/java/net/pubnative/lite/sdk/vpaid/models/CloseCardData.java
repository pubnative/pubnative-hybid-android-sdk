package net.pubnative.lite.sdk.vpaid.models;

import android.graphics.Bitmap;

public class CloseCardData {

    private String title;
    private Bitmap icon;
    private String banner;
    private Bitmap bannerImage;
    private double rating = 0;
    private int votes = 0;

    public CloseCardData(){}

    public CloseCardData(String title, Bitmap icon, String banner, double rating, int votes) {
        this.title = title;
        this.icon = icon;
        this.banner = banner;
        this.rating = rating;
        this.votes = votes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Bitmap getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(Bitmap bannerImage) {
        this.bannerImage = bannerImage;
    }
}
