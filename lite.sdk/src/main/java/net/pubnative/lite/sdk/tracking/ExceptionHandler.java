package net.pubnative.lite.sdk.tracking;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String STRICT_MODE_TAB = "StrictMode";
    private static final String STRICT_MODE_KEY = "Violation";

    private final Thread.UncaughtExceptionHandler originalHandler;
    private final StrictModeHandler strictModeHandler = new StrictModeHandler();
    final Map<Client, Boolean> clientMap = new WeakHashMap<>();

    static void enable(Client client) {
        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();

        // Find or create the Bugsnag ExceptionHandler
        ExceptionHandler bugsnagHandler;
        if (currentHandler instanceof ExceptionHandler) {
            bugsnagHandler = (ExceptionHandler) currentHandler;
        } else {
            bugsnagHandler = new ExceptionHandler(currentHandler);
            Thread.setDefaultUncaughtExceptionHandler(bugsnagHandler);
        }

        // Subscribe this client to uncaught exceptions
        bugsnagHandler.clientMap.put(client, true);
    }

    static void disable(Client client) {
        // Find the Bugsnag ExceptionHandler
        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHandler instanceof ExceptionHandler) {
            // Unsubscribe this client from uncaught exceptions
            ExceptionHandler bugsnagHandler = (ExceptionHandler) currentHandler;
            bugsnagHandler.clientMap.remove(client);

            // Remove the Bugsnag ExceptionHandler if no clients are subscribed
            if (bugsnagHandler.clientMap.isEmpty()) {
                Thread.setDefaultUncaughtExceptionHandler(bugsnagHandler.originalHandler);
            }
        }
    }

    ExceptionHandler(Thread.UncaughtExceptionHandler originalHandler) {
        this.originalHandler = originalHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        boolean strictModeThrowable = strictModeHandler.isStrictModeThrowable(throwable);

        // Notify any subscribed clients of the uncaught exception
        for (Client client : clientMap.keySet()) {
            MetaData metaData = new MetaData();
            String violationDesc = null;

            if (strictModeThrowable) { // add strictmode policy violation to metadata
                violationDesc = strictModeHandler.getViolationDescription(throwable.getMessage());
                metaData = new MetaData();
                metaData.addToTab(STRICT_MODE_TAB, STRICT_MODE_KEY, violationDesc);
            }

            String severityReason = strictModeThrowable
                    ? HandledState.REASON_STRICT_MODE : HandledState.REASON_UNHANDLED_EXCEPTION;
            client.cacheAndNotify(throwable, Severity.ERROR,
                    metaData, severityReason, violationDesc);
        }

        // Pass exception on to original exception handler
        if (originalHandler != null) {
            originalHandler.uncaughtException(thread, throwable);
        } else {
            System.err.printf("Exception in thread \"%s\" ", thread.getName());
            throwable.printStackTrace(System.err);
        }
    }
}
