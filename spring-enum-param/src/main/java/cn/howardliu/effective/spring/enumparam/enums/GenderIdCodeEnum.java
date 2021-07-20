package cn.howardliu.effective.spring.enumparam.enums;

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
}
