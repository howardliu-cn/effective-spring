package cn.howardliu.demo.springboot.beanpostprocessor.idgenerator.generator;

/**
 * ID生成器
 *
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/21 下午5:42
 */
public interface IdGenerator {
    String groupName();

    long nextId();
}
