package net.mcbbs.client.main.client.cmd;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Map;

public class PackageManager {
    public static Map<String, PluginDesigner> packages;

    public static void loadPackageFile(URL url, String classPath) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        URLClassLoader pluginLoader = new URLClassLoader(new URL[]{url});
        Class<?> cl = pluginLoader.loadClass(classPath);
        if (Arrays.binarySearch(cl.getInterfaces(), PluginInterface.class) == -1)
            throw new IOException("遇到了一个无法装载为插件的 jar 文件，请检查位于 " + url.toString() + " 的类 " + classPath + " 是否符合插件的编写要求！");
        Method m = cl.getMethod("initializer");
        Object o = cl.getConstructor().newInstance();
        PluginDesigner ret = (PluginDesigner) m.invoke(o);
        packages.put(ret.pkg, ret);
    }

    public static void loadPackage(PluginDesigner p) {
        packages.put(p.pkg, p);
    }
}