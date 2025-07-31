// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.mraid.utils;

import java.util.ArrayList;
import java.util.List;

public class MraidCloseAdRepo {

    private static MraidCloseAdRepo instance;

    private boolean isAdSticky = false;

    private final List<ICloseAdObserver> observerList;

    private MraidCloseAdRepo() {
        observerList = new ArrayList<>();
    }

    public static MraidCloseAdRepo getInstance() {
        if (instance == null)
            instance = new MraidCloseAdRepo();
        return instance;
    }

    public void registerExpandedAdCloseObserver(ICloseAdObserver iCloseAdObserver) {
        if (isAdSticky) {
            if (!observerList.contains(iCloseAdObserver))
                observerList.add(iCloseAdObserver);
        }
    }

    public void unregisterExpandedAdCloseObserver(ICloseAdObserver iCloseAdObserver) {
        if (isAdSticky) {
            observerList.remove(iCloseAdObserver);
        }
    }

    public void notifyTabChanged(){
        notifyObservers();
    }

    public void notifyObservers() {
        if (isAdSticky) {
            for (ICloseAdObserver observer : observerList) {
                if (observer != null)
                    observer.onCloseExpandedAd();
            }
            if (!observerList.isEmpty()) {
                observerList.clear();
                isAdSticky = false;
            }
        }
    }

    public void setIsAdSticky(boolean sticky) {
        isAdSticky = sticky;
    }

    public boolean isStickyAd() {
        return isAdSticky;
    }

    public interface ICloseAdObserver {
        void onCloseExpandedAd();
    }
}
