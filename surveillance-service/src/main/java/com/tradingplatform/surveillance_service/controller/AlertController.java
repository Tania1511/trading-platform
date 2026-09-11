package com.tradingplatform.surveillance_service.controller;

import com.tradingplatform.surveillance_service.AlertService;
import com.tradingplatform.surveillance_service.dto.AlertResponse;
import com.tradingplatform.surveillance_service.dto.AskQuestionRequest;
import com.tradingplatform.surveillance_service.dto.AskQuestionResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;


    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<AlertResponse> getRecentAlerts(){
       return alertService.getRecentAlert();
    }

    @PostMapping("/{id}/ask")
    public AskQuestionResponse ask(@PathVariable UUID id, @RequestBody AskQuestionRequest request){
        String answer = alertService.askFollowUpQuestion(id, request.question());
        return new AskQuestionResponse(answer);
    }

}
