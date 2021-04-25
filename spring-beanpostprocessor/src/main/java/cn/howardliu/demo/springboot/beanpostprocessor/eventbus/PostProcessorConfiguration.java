package cn.howardliu.demo.springboot.beanpostprocessor.eventbus;

import cn.howardliu.demo.springboot.beanpostprocessor.eventbus.processor.GuavaEventBusBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO 描述信息
 *
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/23 下午9:37
 */
@Configuration
public class PostProcessorConfiguration {

    @Bean
    public GlobalEventBus eventBus() {
        return GlobalEventBus.getInstance();
    }

    @Bean
    public GuavaEventBusBeanPostProcessor eventBusBeanPostProcessor() {
        return new GuavaEventBusBeanPostProcessor();
    }

    @Bean
    public StockTradePublisher stockTradePublisher() {
        return new StockTradePublisher();
    }
}
