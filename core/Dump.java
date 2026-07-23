import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;

public class Dump {
    public static void main(String[] args) throws Exception {
        URL url = new java.io.File(args[0]).toURI().toURL();
        URLClassLoader cl = new URLClassLoader(new URL[]{url});
        Class<?> clazz = cl.loadClass("com.zigythebird.playeranimcore.loading.UniversalAnimLoader");
        for (Method m : clazz.getMethods()) {
            System.out.println(m);
        }
        System.out.println("---");
        Class<?> clazz2 = cl.loadClass("com.zigythebird.playeranimcore.loading.PlayerAnimatorLoader");
        for (Method m : clazz2.getMethods()) {
            System.out.println(m);
        }
    }
}
