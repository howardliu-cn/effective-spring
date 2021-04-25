package cn.howardliu.demo.spring.beanpostprocessor;

import cn.howardliu.demo.spring.beanpostprocessor.annotation.IdGeneratorClient;
import cn.howardliu.demo.spring.beanpostprocessor.generator.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class SpringBeanPostProcessorApplicationTests {
    @IdGeneratorClient
    private IdGenerator defaultIdGenerator;

    @Test
    void contextLoads() {
        Assert.notNull(defaultIdGenerator, "注入失败");
        System.out.println(defaultIdGenerator.groupName());
        System.out.println(defaultIdGenerator.nextId());
    }

}
