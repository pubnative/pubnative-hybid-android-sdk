package net.pubnative.lite.sdk.tracking;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public interface BeforeRecordBreadcrumb {
    boolean shouldRecord(Breadcrumb breadcrumb);
}