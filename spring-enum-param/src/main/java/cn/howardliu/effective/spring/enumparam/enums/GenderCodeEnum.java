package cn.howardliu.effective.spring.enumparam.enums;

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
}
