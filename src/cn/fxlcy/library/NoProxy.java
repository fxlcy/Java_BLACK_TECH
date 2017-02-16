package cn.fxlcy.library;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by fxlcy
 * on 2017/2/16.
 *
 * @author fxlcy
 * @version 1.0
 * 
 * 不被代理
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoProxy {
}
