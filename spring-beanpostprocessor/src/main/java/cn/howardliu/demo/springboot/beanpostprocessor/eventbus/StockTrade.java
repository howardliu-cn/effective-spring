package cn.howardliu.demo.springboot.beanpostprocessor.eventbus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * TODO 描述信息
 *
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/23 下午9:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockTrade {
    private String symbol;
    private int quantity;
    private double price;
    private Date tradeDate;
}
