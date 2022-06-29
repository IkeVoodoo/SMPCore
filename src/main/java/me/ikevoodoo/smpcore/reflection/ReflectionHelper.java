package me.ikevoodoo.smpcore.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Optional;

public class ReflectionHelper {

    private static final HashMap<String, ClassData> datas = new HashMap<>();

    private ReflectionHelper() {

    }

    public static ClassData get(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException {
        ClassData data = datas.get(clazz.getName());
        if (data != null)
            return data;

        data = new ClassData(clazz);
        datas.put(clazz.getName(), data);
        return data;
    }
}

class Test {
    public String test(int i, String s) {
        return s + i;
    }

    public static String testt() {
        return "helo";
    }
}

class ClassData {
    private final Class<?> clazz;
    private final HashMap<String, MethodHandle> handleHashMap = new HashMap<>();
    private final HashMap<String, MethodHandle> staticHandleHashMap = new HashMap<>();

    ClassData(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException {
        this.clazz = clazz;
        this.extractHandles();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object invokeStatic(String signature, Object... args) throws Throwable {
        Optional<MethodHandle> handleOptional = get(signature, true);
        if (handleOptional.isEmpty())
            return null;

        return handleOptional.get().invokeWithArguments(args);
    }

    public Object invoke(String signature, Object... args) throws Throwable {
        Optional<MethodHandle> handleOptional = get(signature, false);
        if (handleOptional.isEmpty())
            return null;

        return handleOptional.get().invokeWithArguments(args);
    }

    public String findFirst(String name) {
        for (String s : handleHashMap.keySet())
            if (s.startsWith(name))
                return s;
        return name;
    }

    public String findFirstStatic(String name) {
        for (String s : staticHandleHashMap.keySet())
            if (s.startsWith(name))
                return s;
        return name;
    }

    private Optional<MethodHandle> get(String signature, boolean statik) {
        return Optional.ofNullable(statik ? this.staticHandleHashMap.get(signature) : this.handleHashMap.get(signature));
    }

    private void extractHandles() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (Method m : this.clazz.getDeclaredMethods()) {
            MethodType type = MethodType.methodType(m.getReturnType(), m.getParameterTypes());
            String sig = toSig(m);
            try {
                try {
                    MethodHandle methodHandle = lookup.findVirtual(this.clazz, m.getName(), type);
                    this.handleHashMap.put(sig, methodHandle);
                } catch (IllegalAccessException e) {
                    MethodHandle methodHandle = lookup.findStatic(this.clazz, m.getName(), type);
                    this.staticHandleHashMap.put(sig, methodHandle);
                    throw e;
                }
            } catch (IllegalAccessException e) {
                if (e.getMessage().contains("no such method"))
                    continue;
                throw e;
            }

        }
    }

    private String toSig(Method m) {
        StringBuilder sb = new StringBuilder();
        sb.append(m.getName()).append("(");
        boolean appendColon = m.getParameterCount() > 1;
        for (Parameter param : m.getParameters()) {
            Class<?> type = param.getType();
            sb.append(type.getName());

            if (appendColon)
                sb.append(";");
        }
        sb.append(")").append(m.getReturnType().getName());
        return sb.toString();
    }
    private String toArray(Class<?> type) {
        if (!type.isArray())
            return type.getName();

        return "[" + toArray(type.arrayType());
    }
}