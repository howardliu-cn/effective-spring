package cn.howardliu.effective.spring.enumparam.deserializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import cn.howardliu.effective.spring.enumparam.enums.BaseEnum;
import cn.howardliu.effective.spring.enumparam.exception.CodeBaseException;
import cn.howardliu.effective.spring.enumparam.exception.ErrorResponseEnum;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021/8/22 15:58
 */
public class IdCodeToEnumDeserializer extends JsonDeserializer<BaseEnum> {
    @Override
    public BaseEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        final String param = jsonParser.getText();
        final JsonStreamContext parsingContext = jsonParser.getParsingContext();
        final String currentName = parsingContext.getCurrentName();
        final Object currentValue = parsingContext.getCurrentValue();
        try {
            final Field declaredField = currentValue.getClass().getDeclaredField(currentName);
            final Class<?> targetType = declaredField.getType();
            final Method createMethod = targetType.getDeclaredMethod("create", Object.class);
            return (BaseEnum) createMethod.invoke(null, param);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
            throw new CodeBaseException(ErrorResponseEnum.PARAMS_ENUM_NOT_MATCH, new Object[] {param}, "", e);
        }
    }
}
