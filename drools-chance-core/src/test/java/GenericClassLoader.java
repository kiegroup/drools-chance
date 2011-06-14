

@Deprecated
public class GenericClassLoader extends ClassLoader {

	public Class loadClass(String className, byte[] b) {
		return  defineClass(className, b, 0, b.length);
	}

}
