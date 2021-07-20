package cn.howardliu.effective.spring.enumparam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.howardliu.effective.spring.enumparam.converter.CodeToEnumConverterFactory;
import cn.howardliu.effective.spring.enumparam.converter.IdCodeToEnumConverterFactory;
import cn.howardliu.effective.spring.enumparam.converter.IdToEnumConverterFactory;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-10
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new IdCodeToEnumConverterFactory());
        registry.addConverterFactory(new CodeToEnumConverterFactory());
        registry.addConverterFactory(new IdToEnumConverterFactory());
    }
}
