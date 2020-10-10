package cn.howardliu.demo.spring.seh.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <br>created at 2020/8/25
 *
 * @author liuxh
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Response<T> extends BaseResponse {
    private T data;
}
