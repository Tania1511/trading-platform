package com.tradingplatform.surveillance_service.agent;

import com.tradingplatform.surveillance_service.config.AgentProperties;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Component
public class GetOrderDetailsTool implements AgentTool{

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final AgentProperties agentProperties;

    public GetOrderDetailsTool(AgentProperties agentProperties) {
        this.agentProperties = agentProperties;
    }

    @Override
    public String name() {
        return "get_order_details";
    }

    @Override
    public String description() {
        return "Fetch the current full details of an order by its internal order Id,"
                + "including its status, price, quantity, and how much has been filled.";
    }

    @Override
    public Map<String, Object> inputSchema() {
        return Map.of(
          "type", "object",
          "properties", Map.of("orderId", Map.of("type","string", "desription", "The order's UUID")),
          "required","orderId"
        );
    }

    @Override
    public String execute(JsonNode input) {
        String orderID = input.get("orderId").asString();
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(agentProperties.getOrderGatewayUrl() + "/orders/"+orderID))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch(Exception e){
            return "Error fetching order details: " + e.getMessage();
        }
    }
}
