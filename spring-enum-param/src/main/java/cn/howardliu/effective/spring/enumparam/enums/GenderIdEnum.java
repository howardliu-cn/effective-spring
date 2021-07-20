package cn.howardliu.effective.spring.enumparam.enums;

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
}
