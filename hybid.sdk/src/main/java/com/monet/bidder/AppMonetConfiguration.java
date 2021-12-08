package com.monet.bidder;

public class AppMonetConfiguration {
    final String applicationId;
    final boolean disableBannerListener;

    private AppMonetConfiguration(Builder builder) {
        this.applicationId = builder.applicationId;
        this.disableBannerListener = builder.disableBannerListener;
    }

    public static class Builder {
        private String applicationId;
        private boolean disableBannerListener = false;

        /**
         * This method sets the applicationId generated by AppMonet dashboard.
         *
         * @param applicationId The id generated by AppMonet dashboard.
         * @return {@link Builder}
         */
        public Builder applicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        /**
         * This method set a flag that tells the sdk if the com.mopub.mobileads.CustomEventBanner
         * are going to be handled by the sdk or manually by the developer. By default the sdk
         * tries to handle these events. Pass true if you want to handle these events on your own and
         * call our sdk manually for each event.
         *
         * @param disableBannerListener The boolean value  which disables or enables our internal
         *                              CustomEventBanner.
         * @return {@link Builder}
         */
        public Builder disableBannerListener(boolean disableBannerListener) {
            this.disableBannerListener = disableBannerListener;
            return this;
        }

        /**
         * This method builds the {@link AppMonetConfiguration} object with the passed values.
         *
         * @return {@link AppMonetConfiguration}
         */
        public AppMonetConfiguration build() {
            return new AppMonetConfiguration(this);
        }
    }
}
