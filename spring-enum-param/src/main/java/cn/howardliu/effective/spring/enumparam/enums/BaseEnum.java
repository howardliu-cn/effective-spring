package cn.howardliu.effective.spring.enumparam.enums;

/**
 * HowardLiu <howardliu1988@163.com>
 * Created on 2021/8/22 16:02
 */
public interface BaseEnum {

    static Integer adapter(String codeStr) {
        try {
            return Integer.valueOf(codeStr);
        } catch (Exception e) {
            return null;
        }
    }
}
