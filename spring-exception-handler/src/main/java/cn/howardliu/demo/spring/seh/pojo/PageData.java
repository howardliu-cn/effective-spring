package cn.howardliu.demo.spring.seh.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>created at 2020/8/25
 *
 * @author liuxh
 * @since 1.0.0
 */
@Data
public class PageData<T> {
    private int pageSize = 10;
    private int pageNo = 0;
    private int totals = 0;
    private List<T> records = new ArrayList<>();
}
