package com.tradingplatform.positionservice.controller;

import com.tradingplatform.positionservice.dto.PositionResponse;
import com.tradingplatform.positionservice.dto.TradeResponse;
import com.tradingplatform.positionservice.service.PositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping("/api/positions")
    public List<PositionResponse> getAllPositions(){
        return  positionService.getAllPositions();
    }

    @GetMapping("/api/trades")
    public List<TradeResponse> getRecentTrades(){
        return positionService.getRecentTrades();
    }
}
