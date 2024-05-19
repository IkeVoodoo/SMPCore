package me.ikevoodoo.smpcore.config2;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.Comments;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentType;
import dev.dejvokep.boostedyaml.route.Route;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.ikevoodoo.smpcore.config2.annotations.Config;
import me.ikevoodoo.smpcore.config2.annotations.Load;
import me.ikevoodoo.smpcore.config2.annotations.Save;
import me.ikevoodoo.smpcore.config2.annotations.comments.BlankCommentLine;
import me.ikevoodoo.smpcore.config2.annotations.comments.Comment;
import me.ikevoodoo.smpcore.config2.annotations.data.CollectionType;
import me.ikevoodoo.smpcore.config2.annotations.data.Getter;
import me.ikevoodoo.smpcore.config2.annotations.data.Setter;
import me.ikevoodoo.smpcore.reflection.ReflectionHelper;
import me.ikevoodoo.smpcore.utils.Pair;
import me.ikevoodoo.smpcore.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Configuration implements InvocationHandler {

    private static final Map<Class<? extends Annotation>, ConfigAnnotationProcessor<?>> ANNOTATION_PROCESSORS = new HashMap<>();

    static {
        ANNOTATION_PROCESSORS.put(Load.class, (annotation, doc, route, args, def) -> doc.reload());
        ANNOTATION_PROCESSORS.put(Save.class, (annotation, doc, route, args, def) -> doc.save());
        ANNOTATION_PROCESSORS.put(Getter.class, (annotation, doc, route, args, def) -> {
            var res = doc.get(route);
            if (res == null) return def;

            return res;
        });
        ANNOTATION_PROCESSORS.put(Setter.class, (annotation, document, route, params, defaultParam) -> {
            document.set(route, params.length == 0 ? defaultParam : params[0]);
            return null;
        });
    }

    private final YamlDocument document;
    private final Map<Method, SettingCache> cache = Collections.synchronizedMap(new HashMap<>());
    private final String name;

    @SuppressWarnings("unchecked")
    public static <T> Pair<Configuration, T> createConfiguration(Class<T> tClass, File file) throws IOException {
        var save = !file.exists();

        var config = new Configuration(file);
        var proxy = Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, config);

        try {
            config.setDefaults(proxy, tClass);

            if (save) {
                config.save();
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return new Pair<>(config, (T) proxy);
    }

    public static String getNameForClass(Class<?> clazz) {
        var annotation = clazz.getAnnotation(Config.class);
        if (annotation == null) {
            return StringUtils.lowercaseFirst(clazz.getSimpleName());
        }

        var name = annotation.value();

        if (name.isBlank()) {
            return StringUtils.lowercaseFirst(clazz.getSimpleName());
        }

        return name;
    }

    Configuration(File file) throws IOException {
        this.document = YamlDocument.create(file,
                GeneralSettings.builder()
                        .setUseDefaults(false)
                        .setKeyFormat(GeneralSettings.KeyFormat.OBJECT)
                        .build(),
                LoaderSettings.builder()
                        .setAutoUpdate(true)
                        .build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.DEFAULT
        );

        this.name = StringUtils.stripExtension(file.getName());
    }

    Configuration(YamlDocument document, String name) {
        this.document = document;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void reload() throws IOException {
        this.document.reload();
    }

    public void save() throws IOException {
        this.document.save();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!method.isAnnotationPresent(Getter.class) && !method.isAnnotationPresent(Setter.class)) {
            return InvocationHandler.invokeDefault(proxy, method, args);
        }

        var annotations = method.getAnnotations();
        var cached = this.cache.get(method);
        if (cached == null) {
            cached = new SettingCache(this.getRouteFor(method), null, null);
            this.cache.put(method, cached);
        }

        var returnType = method.getReturnType();
        if (returnType.isAnnotationPresent(Config.class)) {
            if (cached.getConfig() == null) {
                cached.setConfig(new Configuration(this.document, Configuration.getNameForClass(returnType)));
                cached.setProxy(Proxy.newProxyInstance(proxy.getClass().getClassLoader(), new Class[] {
                        returnType
                }, cached.getConfig()));
            }

            return cached.getProxy();
        }

        Object defaultValue = null;

        try {
            defaultValue = InvocationHandler.invokeDefault(proxy, method, args);
        } catch (Exception ignored) {
            // Ignore
        }

        Object result = null;
        for (var annotation : annotations) {

            ConfigAnnotationProcessor<?> processor = null;

            for (var entry : ANNOTATION_PROCESSORS.entrySet()) {
                if (entry.getKey().isAssignableFrom(annotation.getClass())) {
                    processor = entry.getValue();
                    break;
                }
            }

            if (processor == null) continue;

            var res = processor.process(annotation, this.document, cached.getRoute(), args, defaultValue);
            if (res != null) {
                result = res;
            }
        }


        if (result instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                return collection;
            }

            var collectionType = method.getAnnotation(CollectionType.class);
            if (collectionType == null) {
                return collection;
            }

            for (var element : collection) {
                if (element != null && element.getClass() != collectionType.value()) {
                    throw new IllegalArgumentException("Expected a '%s' in collection '%s', got value '%s' of type '%s'"
                            .formatted(collectionType.value().getSimpleName(), cached.getRoute().join('.'), element, element.getClass().getSimpleName()));
                }
            }

            return collection;
        }

        if (method.getReturnType() == Optional.class) {
            if (result instanceof Optional<?>) {
                return result;
            }

            return Optional.ofNullable(result);
        }

        return result;
    }

    private void setDefaults(Object proxy, Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (var method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Getter.class)) {
                var comments = new ArrayList<Optional<String>>();

                for (var annotation : method.getAnnotations()) {
                    if (annotation instanceof Comment comment) {
                        comments.add(Optional.of(comment.value()));
                    }

                    if (annotation instanceof me.ikevoodoo.smpcore.config2.annotations.comments.Comments commentsAnnotation) {
                        for (var entry : commentsAnnotation.value()) {
                            comments.add(Optional.of(entry.value()));
                        }
                    }

                    if (annotation instanceof BlankCommentLine) {
                        comments.add(Optional.empty());
                    }
                }

                var lines = comments.stream().map(optionalLine ->
                        optionalLine.map(line -> new CommentLine(Optional.empty(), Optional.empty(), line, CommentType.BLOCK)).orElse(Comments.BLANK_LINE)).toList();

                var cached = new SettingCache(this.getRouteFor(method), null, null);
                this.cache.put(method, cached);

                this.document.getOptionalBlock(cached.getRoute()).ifPresent(block -> {
                    block.removeComments();
                    Comments.add(block, Comments.NodeType.KEY, Comments.Position.BEFORE, lines);
                });

                var returnType = method.getReturnType();
                if (returnType.isAnnotationPresent(Config.class)) {
                    if (cached.getConfig() == null) {
                        cached.setConfig(new Configuration(this.document, Configuration.getNameForClass(returnType)));
                        cached.setProxy(Proxy.newProxyInstance(proxy.getClass().getClassLoader(), new Class[] {
                                returnType
                        }, cached.getConfig()));
                    }

                    this.setDefaults(cached.getProxy(), returnType);
                    continue;
                }

                this.document.set(cached.getRoute(), method.invoke(proxy));
            }
        }

    }

    private Route getRouteFor(Method method) throws NoSuchMethodException, IllegalAccessException {
        var classes = ReflectionHelper.getClassData(method.getDeclaringClass()).getClassTree();
        var paths = new ArrayList<String>();

        for (var clazz : classes) {
            var cfg = clazz.getAnnotation(Config.class);
            if (cfg == null || cfg.hidden()) continue;

            paths.add(Configuration.getNameForClass(clazz));
        }

        var getter = method.getAnnotation(Getter.class);
        if (getter != null) {
            paths.add(getter.target().isBlank()
                    ? StringUtils.lowercaseFirst(method.getName().replaceFirst("get", ""))
                    : getter.target());

            return this.createRoute(paths);
        }

        var setter = method.getAnnotation(Setter.class);
        if (setter != null) {
            paths.add(setter.target().isBlank()
                    ? StringUtils.lowercaseFirst(method.getName().replaceFirst("set", ""))
                    : setter.target());

            return this.createRoute(paths);
        }

        paths.add(StringUtils.lowercaseFirst(method.getName()));

        return this.createRoute(paths);
    }

    private Route createRoute(List<String> paths) {
        return Route.from(paths.toArray());
    }

}
