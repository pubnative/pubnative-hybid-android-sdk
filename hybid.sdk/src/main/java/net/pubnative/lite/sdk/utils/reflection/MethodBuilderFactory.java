package net.pubnative.lite.sdk.utils.reflection;

public class MethodBuilderFactory {
    protected static MethodBuilderFactory instance = new MethodBuilderFactory();

    @Deprecated // for testing
    public static void setInstance(MethodBuilderFactory factory) {
        instance = factory;
    }

    public static ReflectionUtils.MethodBuilder create(Object object, String methodName) {
        return instance.internalCreate(object, methodName);
    }

    protected ReflectionUtils.MethodBuilder internalCreate(Object object, String methodName) {
        return new ReflectionUtils.MethodBuilder(object, methodName);
    }
}
