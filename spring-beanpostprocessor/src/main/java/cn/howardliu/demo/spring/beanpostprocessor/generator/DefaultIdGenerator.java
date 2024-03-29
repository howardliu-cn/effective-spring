package cn.howardliu.demo.spring.beanpostprocessor.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 默认ID生成器
 *
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * @version 1.0
 * @since 2021/4/21 下午5:42
 */
class DefaultIdGenerator implements IdGenerator {
    private static final Map<String, AtomicLong> ID_CACHE = new ConcurrentHashMap<>(new HashMap<>());
    private final String groupName;

    DefaultIdGenerator(final String groupName) {
        this.groupName = groupName;
        synchronized (ID_CACHE) {
            ID_CACHE.computeIfAbsent(groupName, key -> new AtomicLong(1));
        }
    }

    @Override
    public String groupName() {
        return this.groupName;
    }

    @Override
    public long nextId() {
        return ID_CACHE.get(this.groupName).getAndIncrement();
    }
}
