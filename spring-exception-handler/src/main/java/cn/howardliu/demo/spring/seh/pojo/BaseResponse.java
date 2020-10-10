package cn.howardliu.demo.spring.seh.pojo;

import lombok.Data;

import java.util.Date;

/**
 * <br>created at 2020/8/24
 *
 * @author liuxh
 * @since 1.0.0
 */
@Data
public abstract class BaseResponse {
    private Integer code;
    private String desc;
    private Date timestamp = new Date();
    private String path;

    public BaseResponse() {
    }

    public BaseResponse(final Integer code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public BaseResponse(final Integer code, final String desc, final String path) {
        this.code = code;
        this.desc = desc;
        this.path = path;
    }
}
