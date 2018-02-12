package net.pubnative.lite.sdk.tracking;

import android.content.Context;

import java.io.File;
import java.util.Comparator;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

class SessionStore extends FileStore<Session> {

    static final Comparator<File> SESSION_COMPARATOR = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs == null && rhs == null) {
                return 0;
            }
            if (lhs == null) {
                return 1;
            }
            if (rhs == null) {
                return -1;
            }
            String lhsName = lhs.getName();
            String rhsName = rhs.getName();
            return lhsName.compareTo(rhsName);
        }
    };

    SessionStore(Configuration config, Context appContext) {
        super(config, appContext, "/pnlite-sessions/",
                128, SESSION_COMPARATOR);
    }

    @Override
    String getFilename(Session session) {
        return String.format(Locale.US, "%s%s%d.json",
                storeDirectory, UUID.randomUUID().toString(), System.currentTimeMillis());
    }
}
