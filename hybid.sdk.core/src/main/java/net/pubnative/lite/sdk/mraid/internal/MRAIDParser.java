// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.mraid.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class MRAIDParser {
    private static final String TAG = MRAIDParser.class.getSimpleName();

    public Map<String, String> parseCommandUrl(String commandUrl) {
        // The command is a URL string that looks like this:
        //
        // mraid://command?param1=val1&param2=val2&...
        //
        // We need to parse out the command and create a map containing it and
        // its the parameters and their associated values.

        MRAIDLog.d(TAG, "parseCommandUrl " + commandUrl);

        // Remove mraid:// prefix.
        String s = commandUrl.substring(8);

        String command;
        Map<String, String> params = new HashMap<>();

        // Check for parameters, parse them if found
        int idx = s.indexOf('?');
        if (idx != -1) {
            command = s.substring(0, idx);
            String paramStr = s.substring(idx + 1);
            String[] paramArray = paramStr.split("&");
            for (String param : paramArray) {
                idx = param.indexOf('=');
                String key = param.substring(0, idx);
                String val = param.substring(idx + 1);
                params.put(key, val);
            }
        } else {
            command = s;
        }

        // Check for valid command.
        if (!isValidCommand(command)) {
            MRAIDLog.w("command " + command + " is unknown");
            return null;
        }

        // Check for valid parameters for the given command.
        if (!checkParamsForCommand(command, params)) {
            MRAIDLog.w("command URL " + commandUrl + " is missing parameters");
            return null;
        }

        Map<String, String> commandMap = new HashMap<>();
        commandMap.put("command", command);
        commandMap.putAll(params);
        return commandMap;
    }

    private boolean isValidCommand(String command) {
        final String[] commands = {
                "close",
                "createCalendarEvent",
                "expand",
                "open",
                "playVideo",
                "resize",
                "setOrientationProperties",
                "setResizeProperties",
                "storePicture",
                "useCustomClose"
        };
        return (Arrays.asList(commands).contains(command));
    }

    private boolean checkParamsForCommand(String command, Map<String, String> params) {
        switch (command) {
            case "createCalendarEvent":
                return params.containsKey("eventJSON");
            case "open":
            case "playVideo":
            case "storePicture":
                return params.containsKey("url");
            case "setOrientationProperties":
                return params.containsKey("allowOrientationChange") &&
                        params.containsKey("forceOrientation");
            case "setResizeProperties":
                return params.containsKey("width") &&
                        params.containsKey("height") &&
                        params.containsKey("offsetX") &&
                        params.containsKey("offsetY") &&
                        params.containsKey("customClosePosition") &&
                        params.containsKey("allowOffscreen");
            case "useCustomClose":
                return params.containsKey("useCustomClose");
        }
        return true;
    }
}
