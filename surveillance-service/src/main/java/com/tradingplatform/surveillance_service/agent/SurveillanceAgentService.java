package com.tradingplatform.surveillance_service.agent;

import com.tradingplatform.surveillance_service.detector.SpoofingSignal;
import com.tradingplatform.surveillance_service.event.OrderCanceledEvent;
import com.tradingplatform.surveillance_service.model.SurveillanceAlert;
import org.springframework.stereotype.Service;

@Service
public class SurveillanceAgentService {

    private final AnthropicAgentClient anthropicAgentClient;


    public SurveillanceAgentService(AnthropicAgentClient anthropicAgentClient) {
        this.anthropicAgentClient = anthropicAgentClient;
    }

    public String investigate(OrderCanceledEvent event, SpoofingSignal signal){
        String prompt = """
                You are a trade surveillance analyst at a broker-dealer, investigating a 
                potential spoofing pattern flagged by an automated rule.
                
                FLAGGED ORDER:
                - Order ID: %s
                - Client order ID: %s
                - Symbol: %s
                - Size: %s
                - Original quantity: %s
                - Filled quantity before cancellation: %s (fill ratio: %s)
                - Time from placement to cancellation: %d seconds
                
                The rule flagged this because it is a large order, almost entirely
                unfilled, canceled very quickly after being placed - a pattern
                consistent with spoofing (placing an order to influence perceived
                supply/demand, with no intention of letting it execute).
                
                You have tools available to pull additional context: the order's
                current full details, and recent genuine trade activity in this
                symbol. Use them if they would help you reach a better-informed
                conclusion.
                
                Write a short (3-5 sentence) compliance note: state whether you
                believe this warrants further human review, and explain your
                reasoning using the specific evidence. Be willing to say the 
                evidence is ambiguous or could have an innocent explanation if
                that is genuinely the more accurate read - your job is an honest
                assessment, not confirming the rule's flag by default.                
                """.formatted(
                        event.orderId(), event.clientOrderId(), event.symbol(), event.symbol(),
                        event.originalQuantity(), event.filledQuantity(), signal.fillRatio(),
                        signal.secondsToCancel()
                );
        return anthropicAgentClient.investigate(prompt);
    }


    public String askFollowUp(SurveillanceAlert alert, String question) {

        String prompt = """
                
                You previously investigated this flagged order:
                - Order ID: %s
                - Client order ID: %s
                - Symbol: %s
                - Size: %s
                - Original quantity: %s, filled: %s (fill ratio: %s)
                - Seconds to cancel: %d
                - Your prior conclusion: %s
                
                A compliance reviewer now asks a follow-up question:
                "%s"
                
                Use you available tools if they would help answer this specifically.
                Anser directly and concisely.
                """.formatted(
                        alert.getOrderId(), alert.getClientOrderId(), alert.getSymbol(),
                        alert.getOriginalQuantity(), alert.getFilledQuantity(), alert.getFillRatio(),
                        alert.getSecondsToCancel(), alert.getAiExplanation(), question
                );

        return anthropicAgentClient.investigate(prompt);
    }
}
