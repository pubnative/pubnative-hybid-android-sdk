package net.pubnative.tarantula.sdk.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;


public class TarantulaAdvertisingIdClient {

    private static final String TAG = TarantulaAdvertisingIdClient.class.getSimpleName();

    public interface Listener {
        void onPNAdvertisingIdFinish(String advertisingId);
    }

    protected Listener mListener;
    protected Handler mHadler;

    public void request(Context context, Listener listener) {

        mListener = listener;
        mHadler = new Handler(Looper.getMainLooper());
        getAdvertisingId(context);
    }

    protected void getAdvertisingId(final Context context) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                AdInfo adInfo = null;
                try {
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    AdvertisingConnection connection = new AdvertisingConnection();
                    try {
                        if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
                            AdvertisingInterface adInterface = new AdvertisingInterface(connection.getBinder());
                            adInfo = new AdInfo(adInterface.getId(), adInterface.isLimitAdTrackingEnabled(true));
                        }
                    } catch (Exception exception) {
                        Log.e(TAG, "getAdvertisingIdInfo - Error: " + exception);
                    } finally {
                        context.unbindService(connection);
                    }
                } catch (Exception exception) {
                    Log.e(TAG, "getAdvertisingIdInfo - Error: " + exception);
                }

                String advertisingId = null;
                if(adInfo != null) {
                    if (adInfo.isLimitAdTrackingEnabled()) {
                        Log.i(TAG, "Error: cannot get advertising id, limit ad tracking is enabled");
                    } else {
                        advertisingId = adInfo.getId();
                    }
                }

                TarantulaAdvertisingIdClient.this.invokeOnFinish(advertisingId);
            }
        }).start();
    }

    protected void invokeOnFinish(final String advertisingId) {
        mHadler.post(new Runnable() {
            @Override
            public void run() {
                if(mListener != null) {
                    mListener.onPNAdvertisingIdFinish(advertisingId);
                }
            }
        });
    }

    /**
     * Ad Info data class with the results
     */
    public class AdInfo {

        private final String mAdvertisingId;
        private final boolean mLimitAdTrackingEnabled;

        AdInfo(String advertisingId, boolean limitAdTrackingEnabled) {

            mAdvertisingId = advertisingId;
            mLimitAdTrackingEnabled = limitAdTrackingEnabled;
        }

        public String getId() {
            return mAdvertisingId;
        }

        public boolean isLimitAdTrackingEnabled() {

            return mLimitAdTrackingEnabled;
        }
    }

    //==============================================================================================
    // Inner classes
    //==============================================================================================

    /**
     * Advertising Service Connection
     */
    protected class AdvertisingConnection implements ServiceConnection {

        boolean retrieved = false;
        private final LinkedBlockingQueue<IBinder> queue = new LinkedBlockingQueue<IBinder>(1);

        public void onServiceConnected(ComponentName name, IBinder service) {

            try {
                this.queue.put(service);
            } catch (InterruptedException localInterruptedException) {
                Log.e(TAG, "Error: can't connect to AdvertisingId service", localInterruptedException);
            }
        }

        public void onServiceDisconnected(ComponentName name) {}

        public IBinder getBinder() throws InterruptedException {

            if (this.retrieved) { throw new IllegalStateException(); }
            this.retrieved = true;
            return this.queue.take();
        }
    }

    /**
     * Advertising IInterface to get the ID
     */
    protected class AdvertisingInterface implements IInterface {

        private IBinder binder;

        public AdvertisingInterface(IBinder pBinder) {

            binder = pBinder;
        }

        public IBinder asBinder() {

            return binder;
        }

        public String getId() throws RemoteException {

            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            String id = null;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                binder.transact(1, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } catch (Exception ex) {
                Log.e(TAG, "Error: Can't read AdvertisingId from the service", ex);
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        public boolean isLimitAdTrackingEnabled(boolean paramBoolean) throws RemoteException {

            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            boolean limitAdTracking = false;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                data.writeInt(paramBoolean ? 1 : 0);
                binder.transact(2, data, reply, 0);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } catch (Exception ex) {
                Log.e(TAG, "Error: Can't get is limit Ad tracking enabled", ex);
            } finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }
    }
}