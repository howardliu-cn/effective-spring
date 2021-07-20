package cn.howardliu.effective.spring.enumparam.converter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Maps;

import cn.howardliu.effective.spring.enumparam.enums.IdBaseEnum;
import cn.howardliu.effective.spring.enumparam.exception.CodeBaseException;
import cn.howardliu.effective.spring.enumparam.exception.ErrorResponseEnum;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-10
 */
public class IdToEnumConverter<T extends IdBaseEnum> implements Converter<String, T> {
    private final Map<String, T> idEnumMap = Maps.newHashMap();

    public IdToEnumConverter(Class<T> enumType) {
        Arrays.stream(enumType.getEnumConstants())
                .forEach(x -> idEnumMap.put(x.getId().toString(), x));
    }

    @Override
    public T convert(String source) {
        return Optional.of(source)
                .map(idEnumMap::get)
                .orElseThrow(() -> new CodeBaseException(ErrorResponseEnum.PARAMS_ENUM_NOT_MATCH));
    }
}
