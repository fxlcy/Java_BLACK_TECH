package cn.fxlcy.library;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

/**
 * Created by fxlcy
 * on 2017/2/16.
 *
 * @author fxlcy
 * @version 1.0
 */

final class Template {
    public final static String PLACEHOLDER_CLASS_NAME = "className";
    public final static String PLACEHOLDER_BASE_CLASS_NAME = "baseClassName";
    public final static String PLACEHOLDER_PARAMS = "params1";
    public final static String PLACEHOLDER_PARAMS_INNER = "params2";
    public final static String PLACEHOLDER_SET_METHODS = "setMethods";

    private final static String sTemplate;

    private String mJavaText;

    static {
        StringBuilder str = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(Template.class.getResourceAsStream("/cn/fxlcy/library/Template.txt"));
        char[] buffer = new char[1024];
        int len;
        try {
            while ((len = reader.read(buffer)) > 0) {
                str.append(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sTemplate = str.toString();
    }


    public Template() {
        mJavaText = sTemplate;
    }


    public void setClassName(Class<?> clazz) {
        replacePlaceHolder(PLACEHOLDER_BASE_CLASS_NAME, clazz.getName().replace("$", "."));
        replacePlaceHolder(PLACEHOLDER_CLASS_NAME, clazz.getSimpleName() + "Proxy");
    }


    public void setParams(Object... params) {
        if (params == null || params.length == 0) {
            replacePlaceHolder(PLACEHOLDER_PARAMS, "");
            replacePlaceHolder(PLACEHOLDER_PARAMS_INNER,"");
            return;
        }

        StringBuilder sb = new StringBuilder(",");
        StringBuilder sb1 = new StringBuilder();
        
        int i = 0;
        for (Object o : params) {
            sb.append(o.getClass().getName()).append(" ").append("arg").append(i).append(",");
            sb1.append("arg").append(i).append(",");
            i++;
        }

        sb1.deleteCharAt(sb1.length() - 1);
        sb.deleteCharAt(sb.length() - 1);

        replacePlaceHolder(PLACEHOLDER_PARAMS, sb.toString());
        replacePlaceHolder(PLACEHOLDER_PARAMS_INNER,sb1.toString());
    }


    public void setMethods(Method... methods) {
        StringBuilder sb = new StringBuilder();
        for (Method method : methods) {
            String name = method.getName();

            sb.append("\r\npublic ").append(method.getReturnType().getName())
                    .append(" ").append(name).append("(");


            char[] chars = name.substring(3).toCharArray();
            String getMethodName = "get" + new String(chars);
            chars[0] = Character.toLowerCase(chars[0]);
            String fieldName = new String(chars);

            Class<?>[] classes = method.getParameterTypes();

            sb.append(classes[0].getName()).append(" ").append("arg");

            sb.append("){Object obj = ").append(getMethodName).append("();super.").append(name).append("(arg);mListener.onChanged(");
            sb.append("\"").append(fieldName).append("\"").append(",obj,arg);}");
        }

        replacePlaceHolder(PLACEHOLDER_SET_METHODS, sb.toString());
    }

    
    public String getJavaText(){
    	return mJavaText;
    }
    

    private void replacePlaceHolder(String placeHolder, String text) {
        String old = "%" + placeHolder + "%";
        mJavaText = mJavaText.replaceAll(old, text);
    }
}
