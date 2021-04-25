package cn.howardliu.demo.springboot.beanpostprocessor.eventbus;

import cn.howardliu.demo.springboot.beanpostprocessor.eventbus.annotation.Subscriber;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO 描述信息
 *
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/23 下午9:36
 */
@SuppressWarnings("UnstableApiUsage")
@Subscriber
public class StockTradePublisher {
    final Set<StockTradeListener> stockTradeListeners = new HashSet<>();

    public void addStockTradeListener(StockTradeListener listener) {
        synchronized (this.stockTradeListeners) {
            this.stockTradeListeners.add(listener);
        }
    }

    public void removeStockTradeListener(StockTradeListener listener) {
        synchronized (this.stockTradeListeners) {
            this.stockTradeListeners.remove(listener);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    void handleNewStockTradeEvent(StockTrade trade) {
        // publish to DB, send to PubNub, ...
        Set<StockTradeListener> listeners;
        synchronized (this.stockTradeListeners) {
            listeners = new HashSet<>(this.stockTradeListeners);
        }
        listeners.forEach(li -> li.stockTradePublished(trade));
    }
}
