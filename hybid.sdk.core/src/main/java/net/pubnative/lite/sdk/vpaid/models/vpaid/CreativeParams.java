package net.pubnative.lite.sdk.vpaid.models.vpaid;

public class CreativeParams {

    private final int width;
    private final int height;
    private final String viewMode;
    private final int desiredBitrate;
    private String creativeData;
    private String environmentVars;

    public CreativeParams(int width, int height, String viewMode, int desiredBitrate) {
        this.width = width;
        this.height = height;
        this.viewMode = "'" + viewMode + "'";
        this.desiredBitrate = desiredBitrate;
    }

    public void setAdParameters(String adParameters) {
        this.creativeData = adParameters;
    }

    public void setEnvironmentVars(String environmentVars) {
        this.environmentVars = environmentVars;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getViewMode() {
        return viewMode;
    }

    public int getDesiredBitrate() {
        return desiredBitrate;
    }

    public String getCreativeData() {
        return creativeData;
    }

    public String getEnvironmentVars() {
        return environmentVars;
    }
}
