package cn.howardliu.demo.spring.seh.advice;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2022-03-24
 */
@Configuration
public class WebMvcConfiguration {
    @Bean
    public WebMvcRegistrations webMvcRegistrationsHandlerMapping() {
        return new WebMvcRegistrations() {
            @Override
            public ExceptionHandlerExceptionResolver getExceptionHandlerExceptionResolver() {
                return new RequestMethodExceptionHandlerExceptionResolver();
            }
        };
    }
}
