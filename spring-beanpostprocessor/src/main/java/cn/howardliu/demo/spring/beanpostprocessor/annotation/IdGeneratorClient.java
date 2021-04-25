package cn.howardliu.demo.spring.beanpostprocessor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ID生成器客户端
 *
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/21 下午5:36
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface IdGeneratorClient {
    /**
     * ID生成器名称
     *
     * @return
     */
    String value() default "DEFAULT";
}
