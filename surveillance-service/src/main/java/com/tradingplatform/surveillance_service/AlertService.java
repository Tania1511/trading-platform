package com.tradingplatform.surveillance_service;

import com.tradingplatform.surveillance_service.agent.SurveillanceAgentService;
import com.tradingplatform.surveillance_service.dto.AlertResponse;
import com.tradingplatform.surveillance_service.model.SurveillanceAlert;
import com.tradingplatform.surveillance_service.repository.SurveillanceAlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AlertService {

    private final SurveillanceAlertRepository alertRepository;
    private final SurveillanceAgentService agentService;


    public AlertService(SurveillanceAlertRepository alertRepository, SurveillanceAgentService agentService) {
        this.alertRepository = alertRepository;
        this.agentService = agentService;
    }

    public List<AlertResponse> getRecentAlert() {
        return alertRepository.findTop50ByOrderByCreatedAtDesc().stream()
                .map(alert -> AlertResponse.from(alert))
                .toList();
    }

    public String askFollowUpQuestion(UUID alertId, String question) {
        SurveillanceAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new NoSuchElementException("Alert not found : " + alertId));
        return agentService.askFollowUp(alert,question);
    }
}
