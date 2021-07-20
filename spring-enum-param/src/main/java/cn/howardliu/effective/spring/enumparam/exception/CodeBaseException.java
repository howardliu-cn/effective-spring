package cn.howardliu.effective.spring.enumparam.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CodeBaseException extends RuntimeException {
    private final ResponseEnum anEnum;
    private final transient Object[] args;
    private final String message;
    private final Throwable cause;

    public CodeBaseException(final ResponseEnum anEnum) {
        this(anEnum, null, anEnum.getMessage(), null);
    }

    public CodeBaseException(final ResponseEnum anEnum, final String message) {
        this(anEnum, null, message, null);
    }

    public CodeBaseException(final ResponseEnum anEnum, final Object[] args, final String message) {
        this(anEnum, args, message, null);
    }

    public CodeBaseException(final ResponseEnum anEnum, final Object[] args, final String message,
            final Throwable cause) {
        this.anEnum = anEnum;
        this.args = args;
        this.message = message;
        this.cause = cause;
    }
}
