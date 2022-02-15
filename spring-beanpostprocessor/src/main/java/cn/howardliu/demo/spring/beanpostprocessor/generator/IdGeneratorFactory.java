package cn.howardliu.demo.spring.beanpostprocessor.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ID生成器工厂类
 *
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * @version 1.0
 * @since 2021/4/22 下午5:18
 */
public enum IdGeneratorFactory {
    INSTANCE;

    private static final Map<String, IdGenerator> ID_GENERATOR_MAP = new ConcurrentHashMap<>(new HashMap<>());

    public synchronized IdGenerator create(final String groupName) {
        return ID_GENERATOR_MAP.computeIfAbsent(groupName, key -> new DefaultIdGenerator(groupName));
    }
}
