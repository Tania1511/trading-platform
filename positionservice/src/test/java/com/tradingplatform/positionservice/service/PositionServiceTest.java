package com.tradingplatform.positionservice.service;

import com.tradingplatform.positionservice.event.TradeEvent;
import com.tradingplatform.positionservice.model.Position;
import com.tradingplatform.positionservice.model.ProcessedTradeEvent;
import com.tradingplatform.positionservice.repository.PositionRepository;
import com.tradingplatform.positionservice.repository.ProcessedTradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private ProcessedTradeRepository processedTradeRepository;

    @InjectMocks
    private PositionService positionService;

    private TradeEvent tradeEvent;

    @BeforeEach
    void setup(){
        positionService = new PositionService(positionRepository,processedTradeRepository);

        tradeEvent = new TradeEvent(UUID.randomUUID(), UUID.randomUUID(), "AAPL",
                                    UUID.randomUUID(),UUID.randomUUID(),"buy-1","sell-1",
                                    new BigDecimal("150.00"),new BigDecimal("10"), Instant.now());
    }

    @Test
    void newTradeEvent_appliesToBothPositions_andRecordsAsProcessed(){
        when(processedTradeRepository.existsById(tradeEvent.eventId())).thenReturn(false);
        when(positionRepository.findByAccountKeyAndSymbol(anyString(),anyString())).thenReturn(Optional.empty());
        when(positionRepository.save(any(Position.class))).thenAnswer(invocation -> invocation.getArgument(0));

        positionService.applyTrade(tradeEvent);

        verify(positionRepository,times(2)).findByAccountKeyAndSymbol(anyString(),anyString());
        verify(positionRepository,times(2)).save(any(Position.class));
        verify(processedTradeRepository,times(1)).save(any(ProcessedTradeEvent.class));
    }

    @Test
    void duplicateTradeEvent_isSkippedEntirely_noPositionChangesApplied(){
        when(processedTradeRepository.existsById(tradeEvent.eventId())).thenReturn(true);

        positionService.applyTrade(tradeEvent);

        verify(positionRepository,never()).findByAccountKeyAndSymbol(anyString(),anyString());
        verify(positionRepository,never()).save(any(Position.class));


        verify(processedTradeRepository,never()).save(any(ProcessedTradeEvent.class));
    }

}
