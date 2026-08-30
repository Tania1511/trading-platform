package com.tradingplatform.matchingengine.book;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class OrderBook {

    private String symbol;

    private final PriorityQueue<RestingOrder> buyOrders;
    private final PriorityQueue<RestingOrder> sellOrders;

    private final AtomicLong sequenceGenerator = new AtomicLong(0);

    public OrderBook(String symbol){
        this.symbol=symbol;
        this.buyOrders = new PriorityQueue<>(Comparator.comparing(RestingOrder::getPrice,Comparator.reverseOrder()).thenComparing(RestingOrder::getSequence));
        this.sellOrders = new PriorityQueue<>(Comparator.comparing(RestingOrder::getPrice).thenComparing(RestingOrder::getSequence));
    }

    public List<Trade> submitOrder(UUID orderId, String clientOrderId, String symbol, RestingOrder.Side side, BigDecimal price, BigDecimal quantity, Instant placedAt){
        long sequence = sequenceGenerator.incrementAndGet();
        RestingOrder incoming = new RestingOrder(orderId,clientOrderId,symbol,side,quantity,price,placedAt,sequence);
        List<Trade> trades = new ArrayList<>();

        PriorityQueue<RestingOrder> oppositeSide = (side== RestingOrder.Side.BUY) ? sellOrders : buyOrders;

        while(!incoming.isFullyFilled() && !oppositeSide.isEmpty() && priceCross(incoming,oppositeSide.peek())){
           RestingOrder resting = oppositeSide.peek();

           BigDecimal matchingQuantity = incoming.getRemainingQuantity().min(resting.getRemainingQuantity());

           Trade trade = buildTrade(incoming,resting,matchingQuantity);
           trades.add(trade);

           incoming.reduceRemainingQuantityBy(matchingQuantity);
           resting.reduceRemainingQuantityBy(matchingQuantity);

           if(resting.isFullyFilled()){
               oppositeSide.poll();
           }


        }
        if(!incoming.isFullyFilled()){
            PriorityQueue<RestingOrder> ownSide = (side== RestingOrder.Side.BUY) ? buyOrders : sellOrders;
            ownSide.add(incoming);
        }
        return trades;
    }

    private boolean priceCross(RestingOrder incoming, RestingOrder resting) {
        if(incoming.getSide() == RestingOrder.Side.BUY)
            return incoming.getPrice().compareTo(resting.getPrice()) >=0;
        else
            return incoming.getPrice().compareTo(resting.getPrice()) <=0;
    }

    private Trade buildTrade(RestingOrder incoming, RestingOrder resting, BigDecimal matchingQuantity) {
        RestingOrder buyer = (incoming.getSide() == RestingOrder.Side.BUY) ? incoming : resting;
        RestingOrder seller = (incoming.getSide() == RestingOrder.Side.BUY) ? resting : incoming;
        return new Trade(
                UUID.randomUUID(),symbol,
                buyer.getOrderId(),seller.getOrderId(),
                buyer.getClientOrderId(), seller.getClientOrderId(),
                matchingQuantity,resting.getPrice(),
                Instant.now());
    }

    public int buySideDepth(){
        return buyOrders.size();
    }

    public int sellSideDepth(){
        return sellOrders.size();
    }

    public String getSymbol(){
        return symbol;
    }

}
