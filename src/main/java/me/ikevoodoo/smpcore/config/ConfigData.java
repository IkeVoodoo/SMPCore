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
                throw new IllegalStateException("Unable to create file parent folder: " + this.file.getParentFile().getAbsolutePath());
            }
            try {
                if(!this.file.createNewFile()) {
                    throw new IllegalStateException("Unable to create file: " + this.file.getAbsolutePath());
                }
                loadConfig();
                set(clazz, null, config);
                save();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reload();
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
            case "yml", "yaml" -> YamlConfiguration.loadConfiguration(file);
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

    private void set(Class<?> clazz, Object o, ConfigurationSection section) {
        for(Field field : clazz.getFields()) {
            try {
                if(field.getType().isAnnotationPresent(Config.class)) {
                    set(field.getType(), o, section.createSection(field.getName()));
                    continue;
                }
                Object value = field.get(o);
                if(value == null)
                    continue;

                // check if class is a list (implements/extends java.util.List)
                if(List.class.isAssignableFrom(field.getType())) {
                    List<?> list = (List<?>) value;
                    if(!list.isEmpty()) {
                        Class<?> listType = list.get(0).getClass();
                        if(!listType.isAnnotationPresent(ConfigType.class)) {
                            section.set(field.getName(), list);
                            continue;
                        }
                        String elementName = listType.getSimpleName().toLowerCase(Locale.ROOT);
                        ConfigurationSection elementSection = section.createSection(field.getName());
                        elementSection.set("doNotTouch", listType.getName());
                        for (int i = 0; i < list.size(); i++) {
                            ConfigurationSection sec = elementSection.createSection(elementName + "_" + i);
                            Object element = list.get(i);
                            for (Field f : listType.getDeclaredFields()) {
                                boolean accessible = f.canAccess(element);
                                f.setAccessible(true);
                                sec.set(f.getName(), f.get(element));
                                f.setAccessible(accessible);
                            }
                        }
                        continue;
                    }
                }

                if(field.isEnumConstant()) {
                    section.set(field.getName(), value.toString());
                    continue;
                }

                section.set(field.getName(), value);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        for(Class<?> nested : clazz.getDeclaredClasses()) {
            set(nested, o, section.createSection(nested.getSimpleName().toLowerCase(Locale.ROOT)));
        }
    }

    private void load(Class<?> clazz, ConfigurationSection section) {
        if(section == null)
            return;
        for(Field field : clazz.getFields()) {
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
                        ClassLoader loader = getClass().getClassLoader();
                        Class<?> elementType = loader.loadClass(storedType);
                        List<Object> list = new ArrayList<>();
                        for(String key : listSection.getKeys(false)) {
                            ConfigurationSection sec = listSection.getConfigurationSection(key);
                            if(sec == null) continue;
                            Object element = get(elementType);
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

    private Object get(Class<?> clazz) {
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
}
