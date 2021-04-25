package cn.howardliu.demo.springboot.beanpostprocessor.eventbus;

/**
 * TODO 描述信息
 *
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/23 下午9:36
 */
@FunctionalInterface
public interface StockTradeListener {
    void stockTradePublished(StockTrade trade);
}
