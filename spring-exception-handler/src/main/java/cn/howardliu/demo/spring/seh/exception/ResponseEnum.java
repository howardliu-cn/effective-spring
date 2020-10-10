package cn.howardliu.demo.spring.seh.exception;

/**
 * <br>created at 2020/8/24
 *
 * @author liuxh
 * @since 1.0.0
 */
public interface ResponseEnum {
    Integer getCode();

    String getMessage();

    default String getLocaleMessage() {
        return getLocaleMessage(null);
    }

    String getLocaleMessage(Object[] args);
}
