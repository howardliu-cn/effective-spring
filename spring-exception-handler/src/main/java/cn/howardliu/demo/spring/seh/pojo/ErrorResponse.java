package cn.howardliu.demo.spring.seh.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * <br>created at 2020/8/25
 *
 * @author liuxh
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ErrorResponse extends BaseResponse {
    public ErrorResponse(final Integer code, final String desc) {
        super(code, desc);
    }

    public ErrorResponse(final Integer code, final String desc, final WebRequest request) {
        super(code, desc, extractRequestURI(request));
    }

    public ErrorResponse(final HttpStatus status, final Exception e) {
        super(status.value(), status.getReasonPhrase() + ": " + e.getMessage());
    }

    public ErrorResponse(final HttpStatus status, final Exception e, final WebRequest request) {
        super(status.value(), status.getReasonPhrase() + ": " + e.getMessage(), extractRequestURI(request));
    }

    private static String extractRequestURI(WebRequest request) {
        final String requestURI;
        if (request instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) request;
            requestURI = servletWebRequest.getRequest().getRequestURI();
        } else {
            requestURI = request.getDescription(false);
        }
        return requestURI;
    }
}
