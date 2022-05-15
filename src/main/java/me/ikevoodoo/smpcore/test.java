package me.ikevoodoo.smpcore;

public class test {

    public static void main(String[] args) throws ClassNotFoundException {

        System.out.println(test.class.getName());
        // get class by class name
        ClassLoader classLoader = test.class.getClassLoader();
        Class<?> clazz = classLoader.loadClass(test.class.getName());
        System.out.println(clazz.getName());
        System.out.println(clazz == test.class);
    }

}
