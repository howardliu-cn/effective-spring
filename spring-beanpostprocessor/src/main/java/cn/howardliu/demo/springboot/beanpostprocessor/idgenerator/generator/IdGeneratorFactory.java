package cn.howardliu.demo.springboot.beanpostprocessor.idgenerator.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ID生成器工厂类
 *
 * @author liuxinghao
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
