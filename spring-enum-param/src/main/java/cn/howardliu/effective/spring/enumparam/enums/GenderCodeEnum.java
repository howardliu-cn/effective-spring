package cn.howardliu.effective.spring.enumparam.enums;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-20
 */
public enum GenderCodeEnum implements CodeBaseEnum {
    MALE("male"),
    FEMALE("female");

    private final String code;

    GenderCodeEnum(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @JsonCreator(mode = Mode.DELEGATING)
    public static GenderCodeEnum create(Object code) {
        final String stringCode = code.toString();
        for (GenderCodeEnum item : values()) {
            if (Objects.equals(stringCode, item.name())) {
                return item;
            }
            if (Objects.equals(item.getCode(), stringCode)) {
                return item;
            }
        }
        return null;
    }
}
