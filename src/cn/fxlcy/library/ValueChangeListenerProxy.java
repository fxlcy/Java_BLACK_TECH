package cn.fxlcy.library;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Created by fxlcy
 * on 2017/2/16.
 *
 * @author fxlcy
 * @version 1.0
 * 
 * 通过JavaCompiler动态编译java文件实现Bean类执行set**操作时的监听
 */
public class ValueChangeListenerProxy {
	private final static Map<Class, Class> sClassMaps = new HashMap<>();
	private final static Map<Class,Constructor>sConstructorMaps = new HashMap<>();
	

	
	
	public static <T> T newInstance(Class<T> clazz, OnValueChangeListener listener, Object... args) {
		Class mapClazz = sClassMaps.get(clazz);
		if(mapClazz == null){

			Template t = new Template();
			t.setClassName(clazz);
			t.setParams(args);

			Method[] methods = clazz.getDeclaredMethods();
			ArrayList<Method> methodArrayList = new ArrayList<>();
			for (Method method : methods) {
				if (method.getName().startsWith("set") && !method.isAnnotationPresent(NoProxy.class)) {
					methodArrayList.add(method);
				}
			}
			
			methods = new Method[methodArrayList.size()];

			t.setMethods(methodArrayList.toArray(methods));

			String javaText = t.getJavaText();

			ClassLoader classLoader = null;

			JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager stdManager = jc.getStandardFileManager(null, null, null);
			try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
				JavaFileObject javaFileObject = manager.makeStringSource(
						clazz.getSimpleName() + "Proxy.java", javaText);
				CompilationTask task = jc.getTask(null, manager, null, null, null, Arrays.asList(javaFileObject));
				if (task.call()) {
					classLoader = 
							new ProxyClassLoader(ValueChangeListenerProxy.class.getClassLoader(),manager.getClassBytes());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(classLoader != null){
				try {
					mapClazz = (Class<T>) classLoader.loadClass("cn.fxlcy.library.proxy." + clazz.getSimpleName() + "Proxy");
					sClassMaps.put(clazz, mapClazz);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		if(mapClazz == null){
			return null;
		}

		Constructor<T> con = sConstructorMaps.get(mapClazz);

		if(con == null){
			if(args == null || args.length == 0){
				try {
					con = mapClazz.getConstructor(OnValueChangeListener.class);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}else{
				int len = args.length;
				Class<?>[] classes = new Class<?>[len + 1];
				classes[0] = OnValueChangeListener.class;

				for(int i =0;i<len;i++){
					classes[i + 1] = args[i].getClass();
				}

				try {
					con = mapClazz.getConstructor(classes);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
			
			sConstructorMaps.put(mapClazz, con);
		}
		

		if(con != null){
			try{
				if(args == null || args.length == 0){
					return con.newInstance(listener);
				}else{
					Object[] objs = new Object[args.length + 1];
					objs[0] = listener;
					for(int i = 0;i < args.length;i++){
						objs[i + 1] = args[i];
					}

					return con.newInstance(objs);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}



		return null;

	}
}
