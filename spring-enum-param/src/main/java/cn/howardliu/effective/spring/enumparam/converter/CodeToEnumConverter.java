package cn.howardliu.effective.spring.enumparam.converter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Maps;

import cn.howardliu.effective.spring.enumparam.enums.CodeBaseEnum;
import cn.howardliu.effective.spring.enumparam.exception.CodeBaseException;
import cn.howardliu.effective.spring.enumparam.exception.ErrorResponseEnum;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-10
 */
public class CodeToEnumConverter<T extends CodeBaseEnum> implements Converter<String, T> {
    private final Map<String, T> codeEnumMap = Maps.newHashMap();

    public CodeToEnumConverter(Class<T> enumType) {
        Arrays.stream(enumType.getEnumConstants())
                .forEach(x -> codeEnumMap.put(x.getCode(), x));
    }

    @Override
    public T convert(String source) {
        return Optional.of(source)
                .map(codeEnumMap::get)
                .orElseThrow(() -> new CodeBaseException(ErrorResponseEnum.PARAMS_ENUM_NOT_MATCH));
    }
}
