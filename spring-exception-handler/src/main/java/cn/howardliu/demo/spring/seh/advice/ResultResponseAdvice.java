package cn.howardliu.demo.spring.seh.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.howardliu.demo.spring.seh.pojo.Response;

/**
 * created at 2020/8/12
 *
 * @author www.howardliu.cn
 * @since 1.0.0
 */
@RestControllerAdvice
public class ResultResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(final MethodParameter returnType,
            final Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.getGenericParameterType().equals(Response.class); // 1
    }

    @Override
    public Object beforeBodyWrite(final Object body, final MethodParameter returnType,
            final MediaType selectedContentType,
            final Class<? extends HttpMessageConverter<?>> selectedConverterType,
            final ServerHttpRequest request, final ServerHttpResponse response) {
        if (body == null || body instanceof Response) {
            return body;
        }
        final Response<Object> result = new Response<>();
        result.setCode(200);
        result.setDesc("查询成功");
        result.setData(body);
        if (returnType.getGenericParameterType().equals(String.class)) { // 2
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("将 Response 对象序列化为 json 字符串时发生异常", e);
            }
        }
        return result;
    }
}
