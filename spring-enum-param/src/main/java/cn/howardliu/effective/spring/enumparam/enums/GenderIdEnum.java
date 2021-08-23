package cn.howardliu.effective.spring.enumparam.enums;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-20
 */
public enum GenderIdEnum implements IdBaseEnum {
    MALE(1),
    FEMALE(2);

    private final Integer id;

    GenderIdEnum(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }


    @JsonCreator(mode = Mode.DELEGATING)
    public static GenderIdEnum create(Object code) {
        final String stringCode = code.toString();
        final Integer intCode = BaseEnum.adapter(stringCode);
        for (GenderIdEnum item : values()) {
            if (Objects.equals(stringCode, item.name())) {
                return item;
            }
            if (Objects.equals(item.getId(), intCode)) {
                return item;
            }
        }
        return null;
    }
}
