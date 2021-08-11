package cn.howardliu.effective.spring.enumparam.enums;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-20
 */
public enum GenderIdCodeEnum implements IdCodeBaseEnum {
    MALE(1, "male"),
    FEMALE(2, "female");

    private final Integer id;
    private final String code;

    GenderIdCodeEnum(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @JsonCreator(mode = Mode.DELEGATING)
    public static GenderIdCodeEnum getItem(String code) {
        final Integer intCode = adapter(code);
        for (GenderIdCodeEnum item : values()) {
            if (Objects.equals(code, item.name())) {
                return item;
            }
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }

            if (item.getId().equals(intCode)) {
                return item;
            }
        }
        return null;
    }

    private static Integer adapter(String codeStr) {
        try {
            return Integer.valueOf(codeStr);
        } catch (Exception e) {
            return null;
        }
    }
}
