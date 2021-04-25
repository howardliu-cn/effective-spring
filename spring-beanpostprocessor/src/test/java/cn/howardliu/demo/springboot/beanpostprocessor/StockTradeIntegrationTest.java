package cn.howardliu.demo.springboot.beanpostprocessor;

import cn.howardliu.demo.springboot.beanpostprocessor.eventbus.*;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO 描述信息
 *
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/23 下午9:38
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PostProcessorConfiguration.class)
public class StockTradeIntegrationTest {
    @Autowired
    StockTradePublisher stockTradePublisher;

    @Test
    public void givenValidConfig_whenTradePublished_thenTradeReceived() {
        final StockTrade stockTrade = new StockTrade("AMZN", 100, 2483.52d, new Date());
        final AtomicBoolean assertionsPassed = new AtomicBoolean(false);
        final StockTradeListener listener = trade -> assertionsPassed.set(this.verifyExact(stockTrade, trade));
        this.stockTradePublisher.addStockTradeListener(listener);
        try {
            GlobalEventBus.post(stockTrade);
            Awaitility.await()
                    .atMost(Duration.ofSeconds(2L))
                    .untilAsserted(() -> Assertions.assertThat(assertionsPassed.get()).isTrue());
        } finally {
            this.stockTradePublisher.removeStockTradeListener(listener);
        }
    }

    boolean verifyExact(StockTrade stockTrade, StockTrade trade) {
        return Objects.equals(stockTrade.getSymbol(), trade.getSymbol())
                && Objects.equals(stockTrade.getTradeDate(), trade.getTradeDate())
                && stockTrade.getQuantity() == trade.getQuantity()
                && stockTrade.getPrice() == trade.getPrice();
    }
}
