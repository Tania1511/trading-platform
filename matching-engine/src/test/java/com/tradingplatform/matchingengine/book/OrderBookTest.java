package com.tradingplatform.matchingengine.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


//@ExtendWith(MockitoExtension.class)
public class OrderBookTest {

    private OrderBook orderBook;

    @BeforeEach
    void setup(){
        orderBook = new OrderBook("AAPL");
    }

    @Test
    void buyOrderWithNoRestingSellOrders_restsInBook_producesNoTrades(){
        List<Trade> trades = orderBook.submitOrder(UUID.randomUUID(),"buy-1","AAPL",RestingOrder.Side.BUY,new BigDecimal("150.00"),new BigDecimal("10"), Instant.now());

        assertThat(trades).isEmpty();
        assertThat(orderBook.buySideDepth()).isEqualTo(1);
        assertThat(orderBook.sellSideDepth()).isEqualTo(0);
    }

    @Test
    void matchingBuyAndSellAtSamePrice_producesExactlyOneTrade_FullyFullingBoth(){
        orderBook.submitOrder(UUID.randomUUID(),"sell-1","AAPL", RestingOrder.Side.SELL,new BigDecimal("150.00"),new BigDecimal("10"), Instant.now());
        List<Trade> trades = orderBook.submitOrder(UUID.randomUUID(),"buy-1","AAPL",RestingOrder.Side.BUY,new BigDecimal("150.00"),new BigDecimal("10"), Instant.now());

        assertThat(trades).hasSize(1);
        Trade trade = trades.get(0);
        assertThat(trade.price()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(trade.quantity()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(trade.buyClientId()).isEqualTo("buy-1");
        assertThat(trade.sellClientId()).isEqualTo("sell-1");
        assertThat(orderBook.buySideDepth()).isEqualTo(0);
        assertThat(orderBook.sellSideDepth()).isEqualTo(0);
    }

    @Test
    void tradeExecutesAtRestingOrderPrice_notIncomingOrderPrice(){
        orderBook.submitOrder(UUID.randomUUID(),"sell-1","AAPL", RestingOrder.Side.SELL,new BigDecimal("149.00"),new BigDecimal("10"), Instant.now());
        List<Trade> trades = orderBook.submitOrder(UUID.randomUUID(),"buy-1","AAPL",RestingOrder.Side.BUY,new BigDecimal("151.00"),new BigDecimal("10"), Instant.now());

        assertThat(trades).hasSize(1);
        assertThat(trades.get(0).price()).isEqualByComparingTo(new BigDecimal("149.00"));
    }

    @Test
    void partialFill_leavesRemainderRestingInBook(){
        orderBook.submitOrder(UUID.randomUUID(),"sell-1","AAPL", RestingOrder.Side.SELL,new BigDecimal("150.00"),new BigDecimal("100"), Instant.now());
        List<Trade> trades = orderBook.submitOrder(UUID.randomUUID(),"buy-1","AAPL",RestingOrder.Side.BUY,new BigDecimal("150.00"),new BigDecimal("30"), Instant.now());

        assertThat(trades).hasSize(1);
        assertThat(trades.get(0).quantity()).isEqualByComparingTo(new BigDecimal("30"));
        assertThat(orderBook.buySideDepth()).isEqualTo(0);
        assertThat(orderBook.sellSideDepth()).isEqualTo(1);
    }

    @Test
    void priceTimePriority_BestPRiceMatchesFirstRegardlessOfArrivalOrder(){
        orderBook.submitOrder(UUID.randomUUID(),"sell-expensive","AAPL", RestingOrder.Side.SELL,new BigDecimal("152.00"),new BigDecimal("10"), Instant.now());
        orderBook.submitOrder(UUID.randomUUID(),"sell-cheap","AAPL", RestingOrder.Side.SELL,new BigDecimal("150.00"),new BigDecimal("10"), Instant.now());

        List<Trade> trades = orderBook.submitOrder(UUID.randomUUID(),"buy-1","AAPL",RestingOrder.Side.BUY,new BigDecimal("152.00"),new BigDecimal("10"), Instant.now());

        assertThat(trades).hasSize(1);
        Trade trade = trades.get(0);
        assertThat(trade.price()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(trade.sellClientId()).isEqualTo("sell-cheap");
        assertThat(orderBook.sellSideDepth()).isEqualTo(1);
    }

    @Test
    void timePriority_earlierMatchedOrderFirstAtEqualPrice(){
        orderBook.submitOrder(UUID.randomUUID(),"sell-first","AAPL", RestingOrder.Side.SELL,new BigDecimal("150.00"),new BigDecimal("10"), Instant.now());
        orderBook.submitOrder(UUID.randomUUID(),"sell-second","AAPL", RestingOrder.Side.SELL,new BigDecimal("150.00"),new BigDecimal("10"), Instant.now());

        List<Trade> trades = orderBook.submitOrder(UUID.randomUUID(),"buy-1","AAPL",RestingOrder.Side.BUY,new BigDecimal("150.00"),new BigDecimal("10"), Instant.now());
        assertThat(trades).hasSize(1);
        assertThat(trades.get(0).sellClientId()).isEqualTo("sell-first");
        assertThat(orderBook.sellSideDepth()).isEqualTo(1);
    }

    @Test
    void largeIncomingOrder_sweepsThroughMultipleRestingOrders_producesMultipleTrades(){
        orderBook.submitOrder(UUID.randomUUID(),"sell-1","AAPL", RestingOrder.Side.SELL,new BigDecimal("150.00"),new BigDecimal("10"), Instant.now());
        orderBook.submitOrder(UUID.randomUUID(),"sell-2","AAPL", RestingOrder.Side.SELL,new BigDecimal("151.00"),new BigDecimal("10"), Instant.now());
        orderBook.submitOrder(UUID.randomUUID(),"sell-3","AAPL", RestingOrder.Side.SELL,new BigDecimal("152.00"),new BigDecimal("10"), Instant.now());

        List<Trade> trades = orderBook.submitOrder(UUID.randomUUID(),"buy-1","AAPL",RestingOrder.Side.BUY,new BigDecimal("152.00"),new BigDecimal("25"), Instant.now());

        assertThat(trades).hasSize(3);
        assertThat(orderBook.sellSideDepth()).isEqualTo(1);
        assertThat(trades.get(0).price()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(trades.get(1).price()).isEqualByComparingTo(new BigDecimal("151.00"));
        assertThat(trades.get(2).price()).isEqualByComparingTo(new BigDecimal("152.00"));
        assertThat(trades.get(2).quantity()).isEqualByComparingTo(new BigDecimal("5"));
        assertThat(orderBook.sellSideDepth()).isEqualTo(1);
    }

    @Test
    void nonCrossingPrices_bothOrdersRestSeparately_noTrade(){
        orderBook.submitOrder(UUID.randomUUID(),"buy-1","AAPL",RestingOrder.Side.BUY,new BigDecimal("149.00"),new BigDecimal("10"), Instant.now());

        List<Trade> trades = orderBook.submitOrder(UUID.randomUUID(),"sell-1","AAPL", RestingOrder.Side.SELL,new BigDecimal("151.00"),new BigDecimal("10"), Instant.now());

        assertThat(trades).isEmpty();
        assertThat(orderBook.buySideDepth()).isEqualTo(1);
        assertThat(orderBook.sellSideDepth()).isEqualTo(1);
    }

}
