package net.pubnative.lite.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.WebView;

import net.pubnative.lite.sdk.models.request.BrandVersion;
import net.pubnative.lite.sdk.models.request.UserAgent;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgentProvider {
    private static final String TAG = UserAgentProvider.class.getSimpleName();
    private static final String PREFERENCES_USER_AGENT = "net.pubnative.lite.useragent";
    private static final String KEY_USER_AGENT_LAST_VERSION = "hybid_user_agent_last_version";
    private static final String KEY_USER_AGENT = "hybid_user_agent";

    private String mUserAgent;
    private UserAgent mStructuredUserAgent;

    public void initialise(Context context) {
        fetchUserAgent(context);
    }

    public String getUserAgent() {
        return mUserAgent;
    }

    public UserAgent getStructuredUserAgent() {
        return mStructuredUserAgent;
    }

    public void fetchUserAgent(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_USER_AGENT, Context.MODE_PRIVATE);
        String userAgent = preferences.getString(KEY_USER_AGENT, "");
        int userAgentVersion = preferences.getInt(KEY_USER_AGENT_LAST_VERSION, -1);

        if (!TextUtils.isEmpty(userAgent) && isValidUserAgent(userAgentVersion)) {
            mUserAgent = userAgent;
            fetchStructuredUserAgent(mUserAgent);
        } else {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                try {
                    mUserAgent = new WebView(context).getSettings().getUserAgentString();
                    fetchStructuredUserAgent(mUserAgent);
                    if (!TextUtils.isEmpty(mUserAgent)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(KEY_USER_AGENT, mUserAgent);
                        editor.putInt(KEY_USER_AGENT_LAST_VERSION, Build.VERSION.SDK_INT);
                        editor.apply();
                    }
                } catch (RuntimeException runtimeException) {
                    fetchStructuredUserAgent(null);
                    Logger.e(TAG, runtimeException.getMessage());
                    HyBid.reportException(runtimeException);
                }
            });
        }
    }

    //Generate structures user agent from the user agent string
    public void fetchStructuredUserAgent(String userAgent) {
        if (mStructuredUserAgent == null) {
            BrandVersion brandVersion = new BrandVersion();
            brandVersion.setBrand("Android");
            List<String> version = new ArrayList<>();
            version.add(String.valueOf(Build.VERSION.RELEASE));
            brandVersion.setVersion(version);

            mStructuredUserAgent = new UserAgent();
            mStructuredUserAgent.setSource(0); //Unknown since there's no option in the standard for self generated
            mStructuredUserAgent.setMobile(1); //Mobile
            Architecture arch = getArchitecture();

            if (!TextUtils.isEmpty(arch.getName())) {
                mStructuredUserAgent.setArchitecture(arch.getName());
                mStructuredUserAgent.setBitness(arch.getBitness());
            }
            mStructuredUserAgent.setModel(Build.MODEL);
            mStructuredUserAgent.setPlatform(brandVersion);

            mStructuredUserAgent.setBrowsers(extractBrowserInfo(userAgent));
        }
    }

    private Architecture getArchitecture() {
        String abi;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] abis = Build.SUPPORTED_ABIS;
            if (abis != null && abis.length > 0) {
                abi = abis[0];
            } else {
                abi = Build.CPU_ABI;
            }
        } else {
            abi = Build.CPU_ABI;
        }

        String name = abi;
        if (abi.contains(Architecture.X86)) {
            name = Architecture.X86;
        } else if (abi.contains(Architecture.ARM)) {
            name = Architecture.ARM;
        } else if (abi.contains(Architecture.MIPS)) {
            name = Architecture.MIPS;
        }

        return new Architecture(name, getBitness(abi));
    }

    private String getBitness(String abi) {
        if (TextUtils.isEmpty(abi)) {
            return Architecture.BITNESS_64;
        }

        return abi.contains(Architecture.BITNESS_64) ? Architecture.BITNESS_64 : Architecture.BITNESS_32;
    }

    private List<BrandVersion> extractBrowserInfo(String userAgent) {
        BrandVersion unknownBrand = new BrandVersion();
        unknownBrand.setBrand("Unknown");
        unknownBrand.setVersion(Collections.singletonList("Unknown"));

        if (TextUtils.isEmpty(userAgent)) {
            return Collections.singletonList(unknownBrand);
        }

        List<BrandVersion> browsers = new ArrayList<>();

        // Define patterns for common browsers
        Pattern chromePattern = Pattern.compile("Chrome\\/([\\d.]+)");
        Pattern chromiumPattern = Pattern.compile("Chromium\\/([\\d.]+)");
        Pattern firefoxPattern = Pattern.compile("Firefox\\/([\\d.]+)");
        Pattern safariPattern = Pattern.compile("Mobile Safari\\/([\\d.]+)");
        Pattern webKitPattern = Pattern.compile("AppleWebKit\\/([\\d.]+)");
        Pattern edgePattern = Pattern.compile("Edg\\/([\\d.]+)");

        Matcher matcher;

        // Check for Chrome
        matcher = chromePattern.matcher(userAgent);
        if (matcher.find()) {
            browsers.add(parseBrowser("Chrome", matcher));
        }

        // Check for Chromium
        matcher = chromiumPattern.matcher(userAgent);
        if (matcher.find()) {
            browsers.add(parseBrowser("Chromium", matcher));
        }

        // Check for Firefox
        matcher = firefoxPattern.matcher(userAgent);
        if (matcher.find()) {
            browsers.add(parseBrowser("Firefox", matcher));
        }

        // Check for Safari
        matcher = safariPattern.matcher(userAgent);
        if (matcher.find()) {
            browsers.add(parseBrowser("Mobile Safari", matcher));
        }

        // Check for WebKit
        matcher = webKitPattern.matcher(userAgent);
        if (matcher.find()) {
            browsers.add(parseBrowser("AppleWebKit", matcher));
        }

        // Check for Edge (Chromium-based)
        matcher = edgePattern.matcher(userAgent);
        if (matcher.find()) {
            browsers.add(parseBrowser("Edge", matcher));

        }

        // If none of the common browsers are detected, set default values
        if (browsers.isEmpty()) {
            browsers.add(unknownBrand);
        }

        return browsers;
    }

    private BrandVersion parseBrowser(String brand, Matcher matcher) {
        BrandVersion brandVersion = new BrandVersion();
        brandVersion.setBrand(brand);
        String version = matcher.group(1);
        if (TextUtils.isEmpty(version)) {
            brandVersion.setVersion(Collections.singletonList("Unknown"));
        } else {
            String[] versionParts = version.split("\\.");
            if (versionParts.length > 0) {
                brandVersion.setVersion(Arrays.asList(versionParts));
            } else {
                brandVersion.setVersion(Collections.singletonList("Unknown"));
            }
        }
        return brandVersion;
    }

    private static final class Architecture {
        private static final String X86 = "x86";
        private static final String ARM = "arm";
        private static final String MIPS = "mips";
        private static final String BITNESS_32 = "32";
        private static final String BITNESS_64 = "64";

        private final String name;
        private final String bitness;

        public Architecture(String name, String bitness) {
            this.name = name;
            this.bitness = bitness;
        }

        public String getName() {
            return name;
        }

        public String getBitness() {
            return bitness;
        }
    }

    private boolean isValidUserAgent(int version) {
        if (version == -1) {
            return false;
        }

        return version == Build.VERSION.SDK_INT;
    }
}
