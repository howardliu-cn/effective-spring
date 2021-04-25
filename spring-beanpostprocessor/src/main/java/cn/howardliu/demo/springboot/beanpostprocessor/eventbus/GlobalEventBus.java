package cn.howardliu.demo.springboot.beanpostprocessor.eventbus;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

import java.util.concurrent.Executors;

/**
 * TODO 描述信息
 *
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/23 下午7:24
 */
@SuppressWarnings("UnstableApiUsage")
public class GlobalEventBus {
    public static final String GLOBAL_EVENT_BUS_EXPRESSION = "T(cn.howardliu.demo.springboot.beanpostprocessor.eventbus.GlobalEventBus).getEventBus()";

    private static final String IDENTIFIER = "global-event-bus";
    private static final GlobalEventBus GLOBAL_EVENT_BUS = new GlobalEventBus();
    private final EventBus eventBus = new AsyncEventBus(IDENTIFIER, Executors.newCachedThreadPool());

    private GlobalEventBus() {
    }

    public static GlobalEventBus getInstance() {
        return GlobalEventBus.GLOBAL_EVENT_BUS;
    }

    public static EventBus getEventBus() {
        return GlobalEventBus.GLOBAL_EVENT_BUS.eventBus;
    }

    public static void subscribe(Object obj) {
        getEventBus().register(obj);
    }

    public static void unsubscribe(Object obj) {
        getEventBus().unregister(obj);
    }

    public static void post(Object event) {
        getEventBus().post(event);
    }
}
