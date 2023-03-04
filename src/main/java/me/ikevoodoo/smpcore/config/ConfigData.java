package me.ikevoodoo.smpcore.config;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.config.annotations.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ConfigData {

    private final String name;
    private final File file;
    private final String type;
    private final HashMap<String, Object> defaults;
    private final Class<?> clazz;

    private FileConfiguration config;


    public ConfigData(SMPPlugin plugin, String name, String type, Class<?> clazz) {
        this.name = name;
        this.defaults = new HashMap<>();
        if(type.isBlank() && !name.contains("."))
            type = "yml";
        this.file = new File(plugin.getDataFolder(), name.endsWith("." + type) ? name : name + "." + type);
        this.type = type;
        this.clazz = clazz;
        try {
            setDefaults(clazz);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(!this.file.exists()) {
            if (!this.file.getParentFile().mkdirs()) {
                plugin.getLogger().warning("Unable to create file parent folder: " + this.file.getParentFile().getAbsolutePath());
            }
            try {
                if(!this.file.createNewFile()) {
                    plugin.getLogger().warning("Unable to create file: " + this.file.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.reload();
    }

    public String getName() {
        return this.name;
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public <T> T get(String path, Class<T> type) {
        return type.cast(config.get(path, defaults.get(path)));
    }

    public Object getDefault(String path) {
        return defaults.get(path);
    }

    public void reload() {
        loadConfig();
        load(clazz, config);

        this.set(clazz, null, this.config); // Try to update.
        this.save();
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        this.config = switch (type) {
            case "yml", "yaml" -> YamlConfiguration.loadConfiguration(this.file);
            default -> throw new IllegalArgumentException("Unknown config type: " + type);
        };
    }

    private void setDefaults(Class<?> clazz) throws IllegalAccessException {
        for(Field field : clazz.getDeclaredFields()) {
            setFieldDefault(field.getName(), field);
        }
        for(Class<?> nested : clazz.getDeclaredClasses()) {
            setNestedDefaults(nested, clazz.getSimpleName().toLowerCase(Locale.ROOT));
        }
    }

    private void setNestedDefaults(Class<?> clazz, String path) throws IllegalAccessException {
        for(Field field : clazz.getDeclaredFields()) {
            setFieldDefault(path + "." + field.getName(), field);
        }
    }

    private void set(Class<?> clazz, Object instance, ConfigurationSection section) {
        for (var field : clazz.getFields()) {
            try {
                var fieldType = field.getType();
                var fieldName = field.getName();

                if (fieldType.isAnnotationPresent(Config.class)) {
                    set(fieldType, instance, section.createSection(fieldName));
                    continue;
                }

                var value = this.getValue(field, instance);
                if (value == null) {
                    // The value is null, bruh
                    continue;
                }

                var valueClass = value.getClass();
                if (List.class.isAssignableFrom(valueClass)) {
                    var listType = this.getListType(field); // such as java.lang.String
                    if (listType == null) {
                        // Unable to fetch the list type, log this!
                        continue;
                    }

                    var listName = listType.getName();
                    var list = (List<?>) value;

                    if (!listType.isAnnotationPresent(ConfigType.class)) {
                        section.set(fieldName, list);
                        continue;
                    }

                    ConfigurationSection elementSection = section.createSection(fieldName);
                    elementSection.set("doNotTouch", listType.getName());
                    for (int i = 0; i < list.size(); i++) {
                        var sec = elementSection.createSection(listName + "_" + i);
                        var element = list.get(i);

                        for (var f : listType.getDeclaredFields()) {
                            boolean accessible = f.canAccess(element);
                            f.setAccessible(true);

                            if (sec.contains(f.getName())) continue;
                            sec.set(f.getName(), f.get(element));
                            f.setAccessible(accessible);
                        }
                    }

                    continue;
                }

                if (section.contains(fieldName)) continue;

                if(field.isEnumConstant()) {
                    section.set(fieldName, value.toString());
                    continue;
                }

                section.set(fieldName, value);
            } catch (IllegalAccessException e) {
                // Call log printer
            } catch (ClassNotFoundException e) {
                // Unable to get the list type, log this!
                e.printStackTrace();
            }
        }

        for(Class<?> nested : clazz.getDeclaredClasses()) {
            var sectionName = nested.getSimpleName();
            if (sectionName.length() > 1) {
                sectionName = Character.toLowerCase(sectionName.charAt(0)) + sectionName.substring(1);
            }

            final var existingSection = section.getConfigurationSection(sectionName);
            if (existingSection != null) {
                set(nested, instance, existingSection);
                continue;
            }

            set(nested, instance, section.createSection(sectionName));
        }
    }

    private void load(Class<?> clazz, ConfigurationSection section) {
        if(section == null)
            return;

        for(var field : clazz.getFields()) {
            try {
                if(field.getType().isAnnotationPresent(Config.class)) {
                    load(field.getType(), section.getConfigurationSection(field.getName()));
                    continue;
                }

                if(section.isConfigurationSection(field.getName())) {
                    if(List.class.isAssignableFrom(field.getType())) {
                        ConfigurationSection listSection = section.getConfigurationSection(field.getName());
                        if (listSection == null) continue;
                        String storedType = listSection.getString("doNotTouch");
                        if (storedType == null) continue;

                        ClassLoader loader = getClass().getClassLoader();
                        Class<?> elementType = loader.loadClass(storedType);
                        List<Object> list = new ArrayList<>();
                        for(String key : listSection.getKeys(false)) {
                            ConfigurationSection sec = listSection.getConfigurationSection(key);
                            if(sec == null) continue;
                            var element = instantiateClass(elementType);
                            if(element == null)
                                continue;
                            for(Field f : elementType.getDeclaredFields()) {
                                boolean accessible = f.canAccess(element);
                                f.setAccessible(true);
                                f.set(element, sec.get(f.getName()));
                                f.setAccessible(accessible);
                            }
                            list.add(element);
                        }
                        field.set(null, list);
                    }
                    continue;
                }

                Object val = section.get(field.getName(), defaults.get(field.getName()));
                if(val == null)
                    continue;

                if(field.getType().isEnum()) {
                    for (Object o : field.getType().getEnumConstants()) {
                        if(o.toString().equals(val.toString())) {
                            field.set(null, o);
                            break;
                        }
                    }
                    continue;
                }

                if(val instanceof String str) {
                    field.set(null, ChatColor.translateAlternateColorCodes('&', str));
                    continue;
                }
                field.set(null, val);
            } catch (IllegalAccessException | ClassNotFoundException ignored) {
                // Unused
            }
        }
        for(Class<?> nested : clazz.getDeclaredClasses()) {
            load(nested, section.getConfigurationSection(nested.getSimpleName().toLowerCase(Locale.ROOT)));
        }
    }

    private Object instantiateClass(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if(constructor.getParameterCount() == 0) {
                try {
                    return constructor.newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    private void setFieldDefault(String path, Field field) throws IllegalAccessException {
        checkType(path, field, DoubleDefault.class, double.class, Double.class);
        checkType(path, field, IntDefault.class, int.class, Integer.class);
        checkType(path, field, StringDefault.class, String.class);
        checkType(path, field, BooleanDefault.class, boolean.class, Boolean.class);
        checkType(path, field, LongDefault.class, long.class, Long.class);
    }

    private void checkType(String path, Field field, Class<? extends Annotation> annotation, Class<?>... types) throws IllegalAccessException {
        if(field.isAnnotationPresent(annotation)) {
            if(Arrays.stream(types).noneMatch(field.getType()::isAssignableFrom))
                throw new IllegalArgumentException("Field " + path + " is not a " + types[0].getSimpleName());

            try {
                Field value = annotation.getDeclaredField("value");
                if (value.getType() == types[0])
                    defaults.put(path, value.get(null));
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                defaults.put(path, field.get(null));
            }
        }
    }

    private Class<?> getListType(Field field) throws ClassNotFoundException {
        var listType = field.getAnnotation(ListType.class);
        if (listType == null) return null;

        var className = listType.value();
        if (className.isEmpty() || className.isBlank()) {
            throw new IllegalStateException("ListType cannot be blank!");
        }

        return Class.forName(className);
    }

    private Object getValue(Field field, Object instance) throws IllegalAccessException {
        var fieldType = field.getType();

        if (isOfTypeOrBoxed(fieldType, double.class)) {
            return getFieldValue(field, instance, DoubleDefault.class);
        }

        if (isOfTypeOrBoxed(fieldType, int.class)) {
            return getFieldValue(field, instance, IntDefault.class);
        }

        if (isOfTypeOrBoxed(fieldType, String.class)) {
            return getFieldValue(field, instance, StringDefault.class);
        }

        if (isOfTypeOrBoxed(fieldType, boolean.class)) {
            return getFieldValue(field, instance, BooleanDefault.class);
        }

        if (isOfTypeOrBoxed(fieldType, long.class)) {
            return getFieldValue(field, instance, LongDefault.class);
        }

        return field.get(instance); // Return whatever value the field holds if it is not a primitive
    }

    private Object getFieldValue(Field field, Object instance, Class<? extends Annotation> annotation) throws IllegalAccessException {
        if (field.isAnnotationPresent(annotation)) {
            try {
                var valueField = annotation.getDeclaredField("value");

                return valueField.get(null);
            } catch (NoSuchFieldException ignored) {
                // Silently ignore exception if the annotation doesn't have a value field
            }
        }

        return field.get(instance);
    }

    private boolean isOfTypeOrBoxed(Class<?> clazz, Class<?> type) {
        return clazz == type || clazz == getBoxedTypeOf(type);
    }

    private Class<?> getBoxedTypeOf(Class<?> clazz) {
        if (clazz == double.class) return Double.class;
        if (clazz == int.class) return Integer.class;
        if (clazz == boolean.class) return Boolean.class;
        if (clazz == long.class) return Long.class;

        return null; // No boxed type is available
    }
}
