package com.tradingplatform.matchingengine.book;

import com.tradingplatform.matchingengine.event.OrderPlacedEvent;
import com.tradingplatform.matchingengine.event.TradeEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderBookManager {

    private static final Logger log = LoggerFactory.getLogger(OrderBookManager.class);

    private final Map<String, OrderBook> orderBookMap = new ConcurrentHashMap<>();

    private final TradeEventPublisher tradeEventPublisher;

    public OrderBookManager(TradeEventPublisher tradeEventPublisher) {
        this.tradeEventPublisher = tradeEventPublisher;
    }

    public void handle(OrderPlacedEvent event){

        OrderBook book = orderBookMap.computeIfAbsent(event.symbol(),k->new OrderBook(event.symbol()));

        RestingOrder.Side side = RestingOrder.Side.valueOf(event.side());

        List<Trade> trades = book.submitOrder(event.orderId(),event.clientOrderId(), event.symbol(), side,event.price(),event.quantity(), event.occurredAt());

        if(trades.isEmpty()){
            log.info("Order clientId={} rested in {} book with no immediate match",
                    event.clientOrderId(),event.symbol());
        }

        for (Trade trade : trades){
            log.info("Match: symbol={} price={} quantity={} buy={} sell={}",
                    trade.symbol(), trade.price(),trade.quantity(),trade.buyClientId(),trade.sellClientId());
            tradeEventPublisher.publishEvent(trade);
        }
    }
}
