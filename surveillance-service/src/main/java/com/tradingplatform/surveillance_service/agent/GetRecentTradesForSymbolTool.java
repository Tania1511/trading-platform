package com.tradingplatform.surveillance_service.agent;

import com.tradingplatform.surveillance_service.config.AgentProperties;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class GetRecentTradesForSymbolTool implements AgentTool{

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AgentProperties agentProperties;

    public GetRecentTradesForSymbolTool(AgentProperties agentProperties) {
        this.agentProperties = agentProperties;
    }

    @Override
    public String name() {
        return "get_recent_trades_for_symbol";
    }

    @Override
    public String description() {
        return "Fetch recent executed trades for a given symbol, to see how much genuine "
                + "trading activity is happening around a suspicious order.";
    }

    @Override
    public Map<String, Object> inputSchema() {
        return Map.of(
                "type","object",
                "properties", Map.of("symbol", Map.of("type", "string", "description", "The ticker symbol, e.g. AAPL")),
                "required", List.of("symbol")
        );
    }

    @Override
    public String execute(JsonNode input) {
        String symbol = input.get("symbol").asString();
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(agentProperties.getPositionServiceUrl() + "/api/trades"))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode allTrades = objectMapper.readTree(response.body());

            String filtered = StreamSupport.stream(allTrades.spliterator(), false)
                    .filter(t -> symbol.equalsIgnoreCase(t.get("symbol").asString()))
                    .map(JsonNode::toString)
                    .collect(Collectors.joining(",\n", "[", "]"));

            return filtered.equals("[]") ? "No recent trades found for " + symbol : filtered;
        }catch (Exception e){
            return "Error fetching trades: " + e.getMessage();
        }
    }
}
