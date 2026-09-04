package com.tradingplatform.positionservice.service;

import com.tradingplatform.positionservice.dto.PositionResponse;
import com.tradingplatform.positionservice.dto.TradeResponse;
import com.tradingplatform.positionservice.event.PositionUpdatedEventPublisher;
import com.tradingplatform.positionservice.event.TradeEvent;
import com.tradingplatform.positionservice.model.Position;
import com.tradingplatform.positionservice.model.ProcessedTradeEvent;
import com.tradingplatform.positionservice.model.Trade;
import com.tradingplatform.positionservice.repository.PositionRepository;
import com.tradingplatform.positionservice.repository.ProcessedTradeRepository;
import com.tradingplatform.positionservice.repository.TradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PositionService {

    private final PositionRepository positionRepository;
    private final ProcessedTradeRepository processedTradeRepository;
    private final TradeRepository tradeRepository;
    private final PositionUpdatedEventPublisher positionUpdatedEventPublisher;

    public PositionService(PositionRepository positionRepository, ProcessedTradeRepository processedTradeRepository, TradeRepository tradeRepository, PositionUpdatedEventPublisher positionUpdatedEventPublisher) {
        this.positionRepository = positionRepository;
        this.processedTradeRepository = processedTradeRepository;
        this.tradeRepository = tradeRepository;
        this.positionUpdatedEventPublisher = positionUpdatedEventPublisher;
    }

    @Transactional
    public void applyTrade(TradeEvent trade){
        if(processedTradeRepository.existsById(trade.eventId())){
            log.info("Skipping already processed TradeEvent  eventId={} (duplicate entry)",trade.eventId());
            return;
        }

        applyFillToPosition(trade.buyClientOrderId(), true,trade.symbol(),trade.price(), trade.quantity());
        applyFillToPosition(trade.sellClientOrderId(), false,trade.symbol(),trade.price(), trade.quantity());


        tradeRepository.save(new Trade(
                trade.tradeId(),
                trade.symbol(),
                trade.buyClientOrderId(),
                trade.sellClientOrderId(),
                trade.price(),
                trade.quantity(),
                trade.occurredAt()
        ));

        processedTradeRepository.save(new ProcessedTradeEvent(trade.eventId()));
    }

    @Transactional
    private void applyFillToPosition(String accountKey, boolean isBuy, String symbol, BigDecimal price, BigDecimal quantity) {
        Position position = positionRepository.findByAccountKeyAndSymbol(accountKey, symbol).orElseGet(() ->new Position(accountKey, symbol));
        position.applyFill(isBuy,quantity,price);
        positionRepository.save(position);

        positionUpdatedEventPublisher.publish(position);

        log.info("Applied fill to position accountKey={}, symbol={}, isBuy={} qty={} price={} -> newQty={}, newAvgCost={}, realizedPnl={}",
                accountKey,symbol,isBuy,quantity,price,
                position.getQuantity(),position.getAverageCost(),position.getRealizedPnl());
    }



    @Transactional(readOnly = true)
    public List<PositionResponse> getAllPositions(){
        return positionRepository.findAll().stream()
                .map(position -> PositionResponse.from(position))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TradeResponse> getRecentTrades(){
        return tradeRepository.findTop50ByOrderByOccurredAtDesc().stream()
                .map(trade -> TradeResponse.from(trade))
                .toList();
    }
}
