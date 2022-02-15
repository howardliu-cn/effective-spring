package cn.howardliu.demo.spring.beanpostprocessor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.howardliu.demo.spring.beanpostprocessor.processor.IdGeneratorBeanPostProcessor;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
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
