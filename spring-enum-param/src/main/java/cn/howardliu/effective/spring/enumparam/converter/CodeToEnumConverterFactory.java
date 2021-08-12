package cn.howardliu.effective.spring.enumparam.converter;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.google.common.collect.Maps;

import cn.howardliu.effective.spring.enumparam.enums.CodeBaseEnum;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-10
 */
public class CodeToEnumConverterFactory implements ConverterFactory<String, CodeBaseEnum> {
    @SuppressWarnings("rawtypes")
    private static final Map<Class, Converter> CONVERTERS = Maps.newHashMap();

    @Override
    public <T extends CodeBaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        //noinspection unchecked
        return CONVERTERS.computeIfAbsent(targetType, key -> new CodeToEnumConverter<>(targetType));
    }
}
