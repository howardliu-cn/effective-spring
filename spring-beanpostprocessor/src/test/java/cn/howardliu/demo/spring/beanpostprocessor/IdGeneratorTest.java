package cn.howardliu.demo.springboot.beanpostprocessor;

import cn.howardliu.demo.springboot.beanpostprocessor.annotation.IdGeneratorClient;
import cn.howardliu.demo.springboot.beanpostprocessor.generator.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class IdGeneratorTest {
    @IdGeneratorClient
    private IdGenerator defaultIdGenerator;

    @Test
    public void contextLoads() {
        Assert.notNull(defaultIdGenerator, "注入失败");
        System.out.println(defaultIdGenerator.groupName());
        System.out.println(defaultIdGenerator.nextId());
    }
}
