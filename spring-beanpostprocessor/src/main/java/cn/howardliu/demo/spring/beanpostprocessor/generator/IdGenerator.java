package cn.howardliu.demo.spring.beanpostprocessor.generator;

/**
 * ID生成器
 *
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * @version 1.0
 * @since 2021/4/21 下午5:42
 */
public interface IdGenerator {
    String groupName();

    long nextId();
}
