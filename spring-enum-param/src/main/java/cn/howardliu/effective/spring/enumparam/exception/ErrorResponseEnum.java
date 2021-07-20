package cn.howardliu.effective.spring.enumparam.exception;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-17
 */
public enum ErrorResponseEnum implements ResponseEnum {
    PARAMS_ENUM_NOT_MATCH(400, "exception.enum.not.match"),
    ;

    private final Integer code;
    private final String message;

    ErrorResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
