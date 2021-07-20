package cn.howardliu.effective.spring.enumparam.converter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Maps;

import cn.howardliu.effective.spring.enumparam.enums.IdCodeBaseEnum;
import cn.howardliu.effective.spring.enumparam.exception.CodeBaseException;
import cn.howardliu.effective.spring.enumparam.exception.ErrorResponseEnum;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-10
 */
public class IdCodeToEnumConverter<T extends IdCodeBaseEnum> implements Converter<String, T> {
    private final Map<String, T> idEnumMap = Maps.newHashMap();
    private final Map<String, T> codeEnumMap = Maps.newHashMap();

    public IdCodeToEnumConverter(Class<T> enumType) {
        Arrays.stream(enumType.getEnumConstants())
                .forEach(x -> {
                    idEnumMap.put(x.getId().toString(), x);
                    codeEnumMap.put(x.getCode(), x);
                });
    }

    @Override
    public T convert(String source) {
        return Optional.of(source)
                .map(codeEnumMap::get)
                .orElseGet(() -> Optional.of(source)
                        .map(idEnumMap::get)
                        .orElseThrow(() -> new CodeBaseException(ErrorResponseEnum.PARAMS_ENUM_NOT_MATCH)));
    }
}
