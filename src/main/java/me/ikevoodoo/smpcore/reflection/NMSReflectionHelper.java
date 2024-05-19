package me.ikevoodoo.smpcore.reflection;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class NMSReflectionHelper {

    private NMSReflectionHelper() {

    }

    public static String getNMSVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }



    public static Object getCraftWorld(World world) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        return getCraftWorldData().getClazz().cast(world);
    }

    public static ReflectionHelper.ClassData getCraftWorldData() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        return ReflectionHelper.getClassData(getCraftWorldClass());
    }

    public static Class<?> getCraftWorldClass() throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + getNMSVersion() + ".CraftWorld");
    }

}
