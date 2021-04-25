package cn.howardliu.demo.springboot.beanpostprocessor.eventbus.annotation;

import cn.howardliu.demo.springboot.beanpostprocessor.eventbus.GlobalEventBus;

import java.lang.annotation.*;

/**
 * TODO 描述信息
 *
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/23 下午9:30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Subscriber {
    String value() default GlobalEventBus.GLOBAL_EVENT_BUS_EXPRESSION;
}
