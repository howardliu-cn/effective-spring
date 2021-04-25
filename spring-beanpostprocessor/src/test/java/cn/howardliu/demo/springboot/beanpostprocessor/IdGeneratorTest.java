package cn.howardliu.demo.springboot.beanpostprocessor;

import cn.howardliu.demo.springboot.beanpostprocessor.idgenerator.annotation.IdGeneratorClient;
import cn.howardliu.demo.springboot.beanpostprocessor.idgenerator.generator.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class IdGeneratorTest {
    @IdGeneratorClient
    private IdGenerator defaultIdGenerator;

    @Test
    void contextLoads() {
        Assert.notNull(defaultIdGenerator, "注入失败");
        System.out.println(defaultIdGenerator.groupName());
        System.out.println(defaultIdGenerator.nextId());
    }
}
