package cn.howardliu.demo.springboot.beanpostprocessor.idgenerator;

import cn.howardliu.demo.springboot.beanpostprocessor.idgenerator.processor.IdGeneratorBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/22 下午5:35
 */
@Configuration
public class IdGeneratorClientConfiguration {

    @Bean
    public IdGeneratorBeanPostProcessor idGeneratorBeanPostProcessor() {
        return new IdGeneratorBeanPostProcessor();
    }
}
