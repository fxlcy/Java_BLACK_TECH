package cn.fxlcy.library;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fxlcy
 * on 2017/2/16.
 *
 * @author fxlcy
 * @version 1.0
 */
public class ProxyClassLoader extends ClassLoader {
    private Map<String, byte[]> mClassBytes;

    public ProxyClassLoader(ClassLoader parent, Map<String, byte[]> classBytes) {
        super(parent);

        if (classBytes == null) {
            mClassBytes = new HashMap<>();
        } else {
            mClassBytes = classBytes;
        }
    }

    public void addClassBytes(String key, byte[] bytes) {
        mClassBytes.put(key, bytes);
    }

    public void addClassBytesAll(Map<String, byte[]> classBytes) {
        mClassBytes.putAll(classBytes);
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = mClassBytes.remove(name);
        if (bytes != null) {
            return defineClass(name, bytes, 0, bytes.length);
        }

        return super.findClass(name);
    }
}
