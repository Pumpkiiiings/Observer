import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DumpReflect {
    public static void main(String[] args) throws Exception {
        File jarFile = new File(args[0]);
        URL url = jarFile.toURI().toURL();
        URLClassLoader cl = new URLClassLoader(new URL[]{url}, DumpReflect.class.getClassLoader());
        
        try (ZipFile zip = new ZipFile(jarFile)) {
            java.util.Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class") && name.contains("playeranimcore/loading/")) {
                    String className = name.replace('/', '.').replace(".class", "");
                    try {
                        Class<?> clazz = cl.loadClass(className);
                        System.out.println("Class: " + className);
                        for (Method m : clazz.getMethods()) {
                            System.out.println("  " + m);
                        }
                    } catch (Throwable t) { }
                }
            }
        }
    }
}
