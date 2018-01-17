package net.pubnative.tarantula.sdk.exception;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by erosgarciaponte on 17.01.18.
 */

public class TarantulaException extends Exception {
    public static final String      TAG                          = TarantulaException.class.getSimpleName();
    //==============================================================================================
    // Request Exceptions
    //==============================================================================================
    public static final TarantulaException REQUEST_NO_INTERNET          = new TarantulaException(1000, "Internet connection is not available");
    public static final TarantulaException REQUEST_PARAMETERS_INVALID   = new TarantulaException(1001, "Invalid execute parameters");
    public static final TarantulaException REQUEST_NO_FILL              = new TarantulaException(1002, "No fill");
    public static final TarantulaException REQUEST_CONFIG_INVALID       = new TarantulaException(1005, "Null or invalid config");
    public static final TarantulaException REQUEST_CONFIG_EMPTY         = new TarantulaException(1006, "Retrieved config contains null element");
    public static final TarantulaException REQUEST_LOADING              = new TarantulaException(1007, "Currently loading");
    public static final TarantulaException REQUEST_SHOWN                = new TarantulaException(1008, "Already shown");
    //==============================================================================================
    // Adapter Exceptions
    //==============================================================================================
    public static final TarantulaException ADAPTER_MISSING_DATA         = new TarantulaException(2000, "Null context or adapter data provided");
    public static final TarantulaException ADAPTER_ILLEGAL_ARGUMENTS    = new TarantulaException(2001, "Invalid data provided");
    public static final TarantulaException ADAPTER_TIMEOUT              = new TarantulaException(2002, "adapter timeout");
    public static final TarantulaException ADAPTER_NOT_FOUND            = new TarantulaException(2003, "adapter not found");
    public static final TarantulaException ADAPTER_TYPE_NOT_IMPLEMENTED = new TarantulaException(2004, "adapter doesn't implements this type");
    public static final TarantulaException ADAPTER_NO_FILL              = new TarantulaException(2005, "adapter did not fill the request");
    //==============================================================================================
    // Placement Exceptions
    //==============================================================================================
    public static final TarantulaException PLACEMENT_FREQUENCY_CAP      = new TarantulaException(3001, "Too many ads: frequency");
    public static final TarantulaException PLACEMENT_PACING_CAP         = new TarantulaException(3002, "Too many ads: pacing");
    public static final TarantulaException PLACEMENT_DISABLED           = new TarantulaException(3003, "Placement is disabled");
    public static final TarantulaException PLACEMENT_NOT_FOUND          = new TarantulaException(3004, "Placement not found");
    //==============================================================================================
    // Private fields
    //==============================================================================================
    protected int mErrorCode;
    protected Map mExtraMap;


    /**
     * Constructor
     *
     * @param errorCode Error code
     * @param message   Error message
     */
    public TarantulaException(int errorCode, String message) {

        super(message);
        mErrorCode = errorCode;
    }

    /**
     * Constructor
     *
     * @param errorCode Error code
     * @param exception Base exception
     */
    public TarantulaException(int errorCode, Exception exception) {

        super(exception);
        mErrorCode = errorCode;
    }

    public static TarantulaException extraException(Map extraMap) {
        TarantulaException extraException = new TarantulaException(0, "extra exception");
        extraException.mExtraMap = extraMap;
        return extraException;
    }

    /**
     * This will return this exception error code number
     *
     * @return valid int representing the error code
     */
    public int getErrorCode() {

        return mErrorCode;
    }

    @Override
    public boolean equals(Object o) {

        boolean result = false;
        if (o.getClass().isAssignableFrom(TarantulaException.class)) {

            TarantulaException exception = (TarantulaException) o;
            result = exception.getErrorCode() == mErrorCode;
        }
        return result;
    }

    @Override
    public String getMessage() {

        return String.valueOf("PNException (" + getErrorCode() + "): " + super.getMessage());
    }

    @Override
    public String toString() {

        String result;
        try {
            JSONObject json = new JSONObject();
            json.put("code", getErrorCode());
            json.put("message", super.getMessage());
            StackTraceElement[] stack = getStackTrace();
            if (stack != null && stack.length > 0) {
                StringBuilder stackTraceBuilder = new StringBuilder();
                for (StackTraceElement element : getStackTrace()) {
                    stackTraceBuilder.append(element.toString());
                    stackTraceBuilder.append('\n');
                }
                json.put("stackTrace", stackTraceBuilder.toString());
            }
            if (mExtraMap != null) {
                JSONObject extraDataObj = new JSONObject();
                for (Object key : mExtraMap.keySet()) {
                    extraDataObj.put(key.toString(), mExtraMap.get(key));
                }
                json.put("extraData", extraDataObj);
            }
            result = json.toString();
        } catch (JSONException e) {
            result = getMessage();
        }
        return result;
    }
}
