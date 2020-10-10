package cn.howardliu.demo.spring.seh.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * <br>created at 2020/8/24
 *
 * @author liuxh
 * @since 1.0.0
 */
public enum CommonResponseEnum implements ResponseEnum {
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "Bad Request"),
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Not Found"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "Method Not Allowed"),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE.value(), "Not Acceptable"),
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT.value(), "Request Timeout"),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Unsupported Media Type"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service Unavailable"),
    ILLEGAL_ARGUMENT(4000, "Illegal Argument"),
    DATA_NOT_FOUND(4004, "Data Not Found"),
    USER_NOT_FOUND(4104, "User Not Found"),
    MENU_NOT_FOUND(4204, "Menu Not Found"),
    INTERNAL_ERROR(9999, "Server Error"),
    ;

    private final Integer code;
    private final String message;
    private MessageSource messageSource;

    CommonResponseEnum(final Integer code, final String message) {
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

    @Override
    public String getLocaleMessage(Object[] args) {
        return messageSource.getMessage("response.error." + code, args, message, LocaleContextHolder.getLocale());
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Component
    public static class ReportTypeServiceInjector {
        private final MessageSource messageSource;

        public ReportTypeServiceInjector(final MessageSource messageSource) {
            this.messageSource = messageSource;
        }

        @PostConstruct
        public void postConstruct() {
            for (final CommonResponseEnum anEnum : CommonResponseEnum.values()) {
                anEnum.setMessageSource(messageSource);
            }
        }
    }
}
