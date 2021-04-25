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
    @IdGeneratorClient("group1")
    private IdGenerator group1IdGenerator;

    @Test
    void contextLoads() {
        Assert.notNull(defaultIdGenerator, "注入失败");
        System.out.println(defaultIdGenerator.groupName() + " => " + defaultIdGenerator.nextId());

        Assert.notNull(group1IdGenerator, "注入失败");
        for (int i = 0; i < 5; i++) {
            System.out.println(defaultIdGenerator.groupName() + " => " + defaultIdGenerator.nextId());
            System.out.println(group1IdGenerator.groupName() + " => " + group1IdGenerator.nextId());
        }
    }

}
