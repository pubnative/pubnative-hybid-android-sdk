package net.pubnative.lite.sdk.utils.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
    public static class MethodBuilder {
        private final Object mInstance;
        private final String mMethodName;
        private Class<?> mClass;

        private final List<Class<?>> mParameterClasses;
        private final List<Object> mParameters;
        private boolean mIsAccessible;
        private boolean mIsStatic;

        public MethodBuilder(final Object instance, final String methodName) {
            mInstance = instance;
            mMethodName = methodName;

            mParameterClasses = new ArrayList<>();
            mParameters = new ArrayList<>();

            mClass = (instance != null) ? instance.getClass() : null;
        }

        public <T> MethodBuilder addParam(final Class<T> clazz,
                                          final T parameter) {

            mParameterClasses.add(clazz);
            mParameters.add(parameter);

            return this;
        }

        public MethodBuilder setStatic(final Class<?> clazz) {

            mIsStatic = true;
            mClass = clazz;

            return this;
        }

        public MethodBuilder setStatic(final String className)
                throws ClassNotFoundException {

            mIsStatic = true;
            mClass = Class.forName(className);

            return this;
        }

        public Object execute() throws Exception {
            final Class<?>[] classArray = new Class<?>[mParameterClasses.size()];
            final Class<?>[] parameterTypes = mParameterClasses.toArray(classArray);

            final Method method = getDeclaredMethodWithTraversal(mClass, mMethodName, parameterTypes);

            if (mIsAccessible) {
                method.setAccessible(true);
            }

            final Object[] parameters = mParameters.toArray();

            if (mIsStatic) {
                return method.invoke(null, parameters);
            } else {
                return method.invoke(mInstance, parameters);
            }
        }
    }

    public static Method getDeclaredMethodWithTraversal(final Class<?> clazz,
                                                        final String methodName, final Class<?>... parameterTypes)
            throws NoSuchMethodException {

        Class<?> currentClass = clazz;

        while (currentClass != null) {
            try {
                return currentClass.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                currentClass = currentClass.getSuperclass();
            }
        }

        throw new NoSuchMethodException();
    }
}
