package me.ikevoodoo.smpcore.utils;

public class ClassUtils {

    private ClassUtils() {

    }

    public static boolean is(Object o, Class<?> clazz) {
        return o != null && clazz.isAssignableFrom(o.getClass());
    }

}
