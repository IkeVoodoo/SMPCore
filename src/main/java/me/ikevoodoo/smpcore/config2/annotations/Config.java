package me.ikevoodoo.smpcore.config2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    /*
    * The name to use as a config path.
    * */
    String value() default "";
    boolean hidden() default false;

}
