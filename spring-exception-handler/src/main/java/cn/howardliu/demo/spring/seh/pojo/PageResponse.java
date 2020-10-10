package cn.howardliu.demo.spring.seh.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;

/**
 * <br>created at 2020/8/25
 *
 * @author liuxh
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageResponse<T> extends Response<PageData<T>> {
    public static void main(String[] args) throws JsonProcessingException {
        PageResponse<String> response = new PageResponse<>();
        response.setCode(200);
        PageData<String> pageData = new PageData<>();
        pageData.setRecords(Arrays.asList("1", "2"));
        pageData.setPageNo(1);
        pageData.setPageSize(10);
        pageData.setTotals(2);
        response.setData(pageData);
        ObjectMapper om = new ObjectMapper();
        System.out.println(om.writeValueAsString(response));
    }
}
