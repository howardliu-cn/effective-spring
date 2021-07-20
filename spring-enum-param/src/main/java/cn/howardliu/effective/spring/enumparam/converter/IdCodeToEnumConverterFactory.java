package cn.howardliu.effective.spring.enumparam.converter;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.google.common.collect.Maps;

import cn.howardliu.effective.spring.enumparam.enums.IdCodeBaseEnum;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-10
 */
public class IdCodeToEnumConverterFactory implements ConverterFactory<String, IdCodeBaseEnum> {
    @SuppressWarnings("rawtypes")
    private static final Map<Class, Converter> CONVERTERS = Maps.newHashMap();

    @Override
    public <T extends IdCodeBaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        //noinspection unchecked
        Converter<String, T> converter = CONVERTERS.get(targetType);
        if (converter == null) {
            converter = new IdCodeToEnumConverter<>(targetType);
            CONVERTERS.put(targetType, converter);
        }
        return converter;
    }
}
