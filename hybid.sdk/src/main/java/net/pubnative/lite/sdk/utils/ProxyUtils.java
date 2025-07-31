// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import java.lang.reflect.Proxy;

public class ProxyUtils {

    public static Object createProxy(ClassLoader classLoader, Class<?> listenerInterface, ProxyMethodHandler handler) {
        return Proxy.newProxyInstance(
                classLoader,
                new Class<?>[]{listenerInterface},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    switch (methodName) {
                        case "equals":
                            return proxy == args[0];
                        case "hashCode":
                            return System.identityHashCode(proxy);
                        case "toString":
                            return "Proxy for " + listenerInterface.getName();
                        default:
                            return handler.handleMethod(proxy, method, args);
                    }
                }
        );
    }

    @FunctionalInterface
    public interface ProxyMethodHandler {
        Object handleMethod(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable;
    }
}
