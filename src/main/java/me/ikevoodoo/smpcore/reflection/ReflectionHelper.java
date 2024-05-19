package me.ikevoodoo.smpcore.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

public class ReflectionHelper {

    private static final HashMap<String, ClassData> CLASS_DATAS = new HashMap<>();

    private ReflectionHelper() {

    }

    public static class ClassData {
        private final Class<?> clazz;
        private final HashMap<String, MethodHandle> handleHashMap = new HashMap<>();
        private final HashMap<String, MethodHandle> staticHandleHashMap = new HashMap<>();
        private boolean initialized;

        ClassData(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public Object invokeStatic(String signature, Object... args) throws Throwable {
            this.initHandleMaps();

            var handleOptional = getMethodHandle(signature, true);
            if (handleOptional.isEmpty()) {
                return null;
            }

            return handleOptional.get().invokeWithArguments(args);
        }

        public Object invoke(String signature, Object... args) throws Throwable {
            this.initHandleMaps();

            var handleOptional = getMethodHandle(signature, false);
            if (handleOptional.isEmpty()) {
                return null;
            }

            return handleOptional.get().invokeWithArguments(args);
        }

        public String findFirstSignature(String name) {
            this.initHandleMaps();

            for (var signature : this.handleHashMap.keySet()) {
                if (signature.startsWith(name)) {
                    return signature;
                }
            }

            return name;
        }

        public String findFirstStaticSignature(String name) {
            this.initHandleMaps();

            for (String s : this.staticHandleHashMap.keySet())
                if (s.startsWith(name))
                    return s;
            return name;
        }

        public Class<?>[] getClassTree() {
            var list = new LinkedList<Class<?>>();
            Class<?> parent = this.clazz;

            list.addFirst(parent);

            while ((parent = parent.getEnclosingClass()) != null) {
                list.addFirst(parent);
            }

            return list.toArray(Class[]::new);
        }

        private Optional<MethodHandle> getMethodHandle(String signature, boolean isStatic) {
            return Optional.ofNullable(isStatic ? this.staticHandleHashMap.get(signature) : this.handleHashMap.get(signature));
        }

        private void initHandleMaps() {
            if(this.initialized) return;
            this.initialized = true;

            try {
                this.extractHandles();
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
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

    public static ClassData getClassData(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException {
        ClassData data = CLASS_DATAS.get(clazz.getName());
        if (data != null) {
            return data;
        }

        data = new ClassData(clazz);
        CLASS_DATAS.put(clazz.getName(), data);
        return data;
    }
}